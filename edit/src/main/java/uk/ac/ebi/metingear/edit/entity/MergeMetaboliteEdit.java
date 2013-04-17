/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.metingear.AppliableEdit;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import java.util.ArrayList;
import java.util.List;

/**
 * A compound edit which merges several <i>separate</i> metabolites into a single
 * union. The undoable edit allows you <i>apply</i> the edit to the data model
 * with {@link #apply()}.
 *
 * @author John May
 */
public final class MergeMetaboliteEdit extends CompoundEdit
                                       implements AppliableEdit {

    private final Reconstruction reconstruction;
    private final Metabolite union;
    private final List<Metabolite> separate;

    public MergeMetaboliteEdit(List<Metabolite> separate, Metabolite union,
                               Reconstruction reconstruction) {

        this.separate = separate;
        this.union = union;
        this.reconstruction = reconstruction;

        // construction of the compound edit
        build();

    }

    /**
     * Applies the edit to the object model. This edit is rather complex and can
     * be difficult to correctly perform all required steps.
     */
    @Override public final void apply() {

        // update reaction and the participants
        for (final Metabolite replace : separate) {
            // access the reactions that reference the molecule to replace
            List<MetabolicReaction> reactions = new ArrayList<MetabolicReaction>(reconstruction.participatesIn(replace));
            for (final MetabolicReaction reaction : reactions) {

                List<MetabolicParticipant> reactants = new ArrayList<MetabolicParticipant>(reaction.getReactants());
                List<MetabolicParticipant> products = new ArrayList<MetabolicParticipant>(reaction.getProducts());

                for (MetabolicParticipant reactant : reactants) {
                    if (reactant.getMolecule() == replace) {
                        reaction.removeReactant(reactant);
                        reaction.addReactant(createReplacement(reactant, union));
                    }
                }

                for (MetabolicParticipant product : products) {
                    if (product.getMolecule() == replace) {
                        reaction.removeProduct(product);
                        reaction.addProduct(createReplacement(product, union));
                    }
                }

                // update reactome maps
                reconstruction.dissociate(replace, reaction);
                reconstruction.associate(union, reaction);

            }
        }

        for (final Metabolite replace : separate) {
            reconstruction.metabolome().remove(replace);
        }
        reconstruction.metabolome().add(union);

    }

    /**
     * Creates the compound edit
     */
    private void build() {

        // remove separate metabolites
        for (final Metabolite replace : separate) {
            addEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    reconstruction.metabolome().add(replace);
                }

                @Override
                public void redo() throws CannotRedoException {
                    reconstruction.metabolome().remove(replace);
                }
            });
        }
        // add new 'union' metabolite
        addEdit(new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                reconstruction.metabolome().remove(union);
            }

            @Override
            public void redo() throws CannotRedoException {
                reconstruction.metabolome().add(union);
            }
        });

        // add the undoable edits for the reaction reactants/products
        for (final Metabolite replace : separate) {
            for (final MetabolicReaction reaction : reconstruction.participatesIn(replace)) {

                addEdit(new AbstractUndoableEdit() {
                    @Override
                    public void undo() throws CannotUndoException {
                        reconstruction.associate(replace, reaction);
                    }

                    @Override
                    public void redo() throws CannotRedoException {
                        reconstruction.dissociate(replace, reaction);
                    }
                });

                for (MetabolicParticipant p : reaction.getReactants()) {
                    if (p.getMolecule() == replace) {
                        addEdit(new RemoveReactantEdit(p, reaction));
                        addEdit(new AddReactantEdit(createReplacement(p, union), reaction));
                    }
                }

                for (MetabolicParticipant p : reaction.getProducts()) {
                    if (p.getMolecule() == replace) {
                        addEdit(new RemoveProductEdit(p, reaction));
                        addEdit(new AddProductEdit(createReplacement(p, union), reaction));
                    }
                }

                addEdit(new AbstractUndoableEdit() {
                    @Override
                    public void undo() throws CannotUndoException {
                        reconstruction.dissociate(union, reaction);
                    }

                    @Override
                    public void redo() throws CannotRedoException {
                        reconstruction.associate(union, reaction);
                    }
                }); // make sure reaction is kept in sync


            }
        }

        // finish the compound edit
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
