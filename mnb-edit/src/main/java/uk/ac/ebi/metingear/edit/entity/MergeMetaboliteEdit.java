/*
 * Copyright (c) 2012. John May <jwmay@users.sf.net>
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

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import java.util.ArrayList;
import java.util.List;

/**
 * A compound edit which merge several <i>separate</i> metabolites into a single
 * union.
 *
 * @author John May
 */
public class MergeMetaboliteEdit extends CompoundEdit {

    private final Reconstruction reconstruction;
    private final Metabolite union;
    private final List<Metabolite> separate;

    public MergeMetaboliteEdit(List<Metabolite> separate, Metabolite union,
                               Reconstruction reconstruction) {

        this.separate = separate;
        this.union = union;
        this.reconstruction = reconstruction;
        create();

    }

    /**
     * Applies the edit
     */
    public void apply() {


        for (final Metabolite replace : separate) {
            reconstruction.getMetabolome().remove(replace);
        }
        reconstruction.getMetabolome().add(union);

        // update reaction and the participants
        for (final Metabolite replace : separate) {
            // access the reactions that reference the molecule to replace
            List<MetabolicReaction> reactions = new ArrayList<MetabolicReaction>(reconstruction.getReactome().getReactions(replace));
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
                reconstruction.getReactome().removeKey(replace, reaction);
                reconstruction.getReactome().update(reaction);

            }
        }


    }

    /**
     * Creates the compound edit
     */
    private void create() {

        // remove separate metabolites
        for (final Metabolite replace : separate) {
            addEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    reconstruction.getMetabolome().add(replace);
                }

                @Override
                public void redo() throws CannotRedoException {
                    reconstruction.getMetabolome().remove(replace);
                }
            });
        }
        // add new 'union' metabolite
        addEdit(new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                reconstruction.getMetabolome().remove(union);
            }

            @Override
            public void redo() throws CannotRedoException {
                reconstruction.getMetabolome().add(union);
            }
        });

        // add the undoable edits for the reaction reactants/products
        for (final Metabolite replace : separate) {
            for (final MetabolicReaction reaction : reconstruction.getReactome().getReactions(replace)) {

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

                // make sure reaction is kept in sync
                addEdit(new AbstractUndoableEdit() {
                    @Override
                    public void undo() throws CannotUndoException {
                        reconstruction.getReactome().removeKey(union, reaction);
                        reconstruction.getReactome().update(reaction);
                    }

                    @Override
                    public void redo() throws CannotRedoException {
                        reconstruction.getReactome().removeKey(replace, reaction);
                        reconstruction.getReactome().update(reaction);
                    }
                });

            }
        }

        // finish the compound edit
        end();


    }

    /**
     * Creates a replacement with the given.
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
