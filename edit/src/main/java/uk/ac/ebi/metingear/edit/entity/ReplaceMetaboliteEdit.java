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
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import java.util.ArrayList;
import java.util.Collection;

/**
 * An undoable edit for replacing one metabolite with another.
 *
 * @author John May
 */
public final class ReplaceMetaboliteEdit extends CompoundEdit {

    private final Reconstruction reconstruction;
    private final Metabolite original;
    private final Metabolite replacement;
    private final Collection<MetabolicReaction> reactions;

    public ReplaceMetaboliteEdit(Metabolite original, Metabolite replacement,
                                 Reconstruction reconstruction) {
        this.original = original;
        this.replacement = replacement;
        this.reconstruction = reconstruction;
        this.reactions = reconstruction.participatesIn(original);
        build();
    }

    public void apply() {
        reconstruction.getMetabolome().remove(original);
        reconstruction.getMetabolome().add(replacement);
        for (final MetabolicReaction reaction : new ArrayList<MetabolicReaction>(reactions)) {

            reconstruction.dissociate(original, reaction);

            for (MetabolicParticipant reactant : new ArrayList<MetabolicParticipant>(reaction.getReactants())) {
                if (reactant.getMolecule() == original) {
                    reaction.removeReactant(reactant);
                    reaction.addReactant(createReplacement(reactant, replacement));
                }
            }
            for (MetabolicParticipant product : new ArrayList<MetabolicParticipant>(reaction.getProducts())) {
                if (product.getMolecule() == original) {
                    reaction.removeProduct(product);
                    reaction.addProduct(createReplacement(product, replacement));
                }
            }

            reconstruction.associate(replacement, reaction);
        }
    }

    private void build() {
        addEdit(new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                reconstruction.getMetabolome().remove(replacement);
                reconstruction.getMetabolome().add(original);
            }

            @Override
            public void redo() throws CannotRedoException {
                reconstruction.getMetabolome().remove(original);
                reconstruction.getMetabolome().add(replacement);
            }
        });
        for (final MetabolicReaction reaction : reactions) {
            addEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    reconstruction.associate(original, reaction);
                }

                @Override
                public void redo() throws CannotRedoException {
                    reconstruction.dissociate(original, reaction);
                }
            });
            for (MetabolicParticipant reactant : reaction.getReactants()) {
                if (reactant.getMolecule() == original) {
                    addEdit(new RemoveReactantEdit(reactant, reaction));
                    addEdit(new AddReactantEdit(createReplacement(reactant, replacement), reaction));
                }
            }
            for (MetabolicParticipant product : reaction.getProducts()) {
                if (product.getMolecule() == original) {
                    addEdit(new RemoveReactantEdit(product, reaction));
                    addEdit(new AddReactantEdit(createReplacement(product, replacement), reaction));
                }
            }
            addEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    reconstruction.dissociate(replacement, reaction);
                }

                @Override
                public void redo() throws CannotRedoException {
                    reconstruction.associate(replacement, reaction);
                }
            });
        }
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
