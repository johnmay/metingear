/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.metingear.edit.entity;

import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.Metabolome;
import uk.ac.ebi.mdk.domain.entity.collection.Reactome;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import java.util.ArrayList;
import java.util.List;

/**
 * A compound edit which splits a single metabolite between two sets of
 * reactions. The split metabolites/reactions are defined as left and right.
 *
 * @author John May
 */
public final class SplitMetaboliteEdit extends CompoundEdit {

    private final Reconstruction reconstruction;
    private final Metabolite original;
    private final Metabolite left;
    private final Metabolite right;
    private final List<MetabolicReaction> leftReactions;
    private final List<MetabolicReaction> rightReactions;

    public SplitMetaboliteEdit(Metabolite original,
                               Metabolite left, List<MetabolicReaction> leftReactions,
                               Metabolite right, List<MetabolicReaction> rightReactions,
                               Reconstruction reconstruction) {

        this.original = original;
        this.reconstruction = reconstruction;

        this.left = left;
        this.right = right;

        this.leftReactions = leftReactions;
        this.rightReactions = rightReactions;

        // construction of the compound edit
        build();

    }

    /**
     * Applies the edit to the object model. This edit is rather complex and can
     * be difficult to correctly perform all required steps.
     */
    public final void apply() {

        Metabolome metabolome = reconstruction.getMetabolome();
        Reactome reactome = reconstruction.getReactome();

        metabolome.remove(original);
        metabolome.add(left);
        metabolome.add(right);

        for (MetabolicReaction leftReaction : leftReactions) {

            List<MetabolicParticipant> reactants = new ArrayList<MetabolicParticipant>(leftReaction.getReactants());
            List<MetabolicParticipant> products = new ArrayList<MetabolicParticipant>(leftReaction.getProducts());

            for (MetabolicParticipant reactant : reactants) {
                if (reactant.getMolecule() == original) {
                    leftReaction.removeReactant(reactant);
                    leftReaction.addReactant(createReplacement(reactant, left));
                }
            }

            for (MetabolicParticipant product : products) {
                if (product.getMolecule() == original) {
                    leftReaction.removeProduct(product);
                    leftReaction.addProduct(createReplacement(product, left));
                }
            }


            reconstruction.dissociate(original, leftReaction);
            reconstruction.associate(left, leftReaction);


        }

        for (MetabolicReaction rightReaction : rightReactions) {

            List<MetabolicParticipant> reactants = new ArrayList<MetabolicParticipant>(rightReaction.getReactants());
            List<MetabolicParticipant> products = new ArrayList<MetabolicParticipant>(rightReaction.getProducts());

            for (MetabolicParticipant reactant : reactants) {
                if (reactant.getMolecule() == original) {
                    rightReaction.removeReactant(reactant);
                    rightReaction.addReactant(createReplacement(reactant, right));
                }
            }

            for (MetabolicParticipant product : products) {
                if (product.getMolecule() == original) {
                    rightReaction.removeProduct(product);
                    rightReaction.addProduct(createReplacement(product, right));
                }
            }

            reconstruction.dissociate(original, rightReaction);
            reconstruction.associate(right, rightReaction);

        }

    }

    /**
     * Creates the compound edit
     */
    private void build() {

        final Metabolome metabolome = reconstruction.getMetabolome();
        final Reactome reactome = reconstruction.getReactome();

        addEdit(new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                metabolome.add(original);
                metabolome.remove(left);
                metabolome.remove(right);
            }

            @Override
            public void redo() throws CannotRedoException {
                metabolome.remove(original);
                metabolome.add(left);
                metabolome.add(right);
            }
        });


        for (final MetabolicReaction leftReaction : leftReactions) {

            addEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    reconstruction.associate(original, leftReaction);
                }

                @Override
                public void redo() throws CannotRedoException {
                    reconstruction.dissociate(original, leftReaction);
                }
            });

            List<MetabolicParticipant> reactants = new ArrayList<MetabolicParticipant>(leftReaction.getReactants());
            List<MetabolicParticipant> products = new ArrayList<MetabolicParticipant>(leftReaction.getProducts());

            for (MetabolicParticipant reactant : reactants) {
                if (reactant.getMolecule() == original) {
                    addEdit(new RemoveReactantEdit(reactant, leftReaction));
                    addEdit(new AddReactantEdit(createReplacement(reactant, left), leftReaction));
                }
            }

            for (MetabolicParticipant product : products) {
                if (product.getMolecule() == original) {
                    addEdit(new RemoveProductEdit(product, leftReaction));
                    addEdit(new AddProductEdit(createReplacement(product, left), leftReaction));
                }
            }

            addEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    reconstruction.dissociate(left, leftReaction);
                }

                @Override
                public void redo() throws CannotRedoException {
                    reconstruction.associate(left, leftReaction);
                }
            });


        }

        for (final MetabolicReaction rightReaction : rightReactions) {

            addEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    reconstruction.associate(original, rightReaction);
                }

                @Override
                public void redo() throws CannotRedoException {
                    reconstruction.dissociate(original, rightReaction);
                }
            });

            List<MetabolicParticipant> reactants = new ArrayList<MetabolicParticipant>(rightReaction.getReactants());
            List<MetabolicParticipant> products = new ArrayList<MetabolicParticipant>(rightReaction.getProducts());

            for (MetabolicParticipant reactant : reactants) {
                if (reactant.getMolecule() == original) {
                    addEdit(new RemoveReactantEdit(reactant, rightReaction));
                    addEdit(new AddReactantEdit(createReplacement(reactant, right), rightReaction));
                }
            }

            for (MetabolicParticipant product : products) {
                if (product.getMolecule() == original) {
                    addEdit(new RemoveProductEdit(product, rightReaction));
                    addEdit(new AddProductEdit(createReplacement(product, right), rightReaction));
                }
            }

            addEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    reconstruction.dissociate(right, rightReaction);
                }

                @Override
                public void redo() throws CannotRedoException {
                    reconstruction.associate(right, rightReaction);
                }
            });


        }

        // complete the compound edit
        end();

    }

    /**
     * Helper method to create a replacement participant with the new
     * metabolite.
     *
     * @param p a participant to get the coefficient/compartment from
     * @param m a metabolite to set on the participant
     * @return copy of the participant with the new metabolite
     */
    private static MetabolicParticipant createReplacement(MetabolicParticipant p, Metabolite m) {
        MetabolicParticipant copy = p.newInstance();
        copy.setMolecule(m);
        copy.setCoefficient(p.getCoefficient());
        copy.setCompartment(p.getCompartment());
        return copy;
    }

}
