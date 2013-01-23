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
import uk.ac.ebi.mdk.domain.entity.collection.Reactome;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import java.util.Collection;
import java.util.HashSet;

/**
 * An undoable edit that removes a metabolite from a reconstruction.
 *
 * @author John May
 */
public class RemoveMetaboliteEdit extends CompoundEdit {

    private Reconstruction reconstruction;
    private Metabolite metabolite;
    private final Collection<MetabolicReaction> reactions;

    /**
     * An undoable edit which removes a metabolite from the provide
     * reconstruction. This edit will also redo/undo the presence of a
     * metabolite in various reactions.
     *
     * @param reconstruction a reconstruction containing the metabolite
     * @param metabolite     a metabolite in the reaction
     */
    public RemoveMetaboliteEdit(final Reconstruction reconstruction,
                                final Metabolite metabolite) {

        final Reactome reactome = reconstruction.getReactome();
        this.reactions = new HashSet<MetabolicReaction>(reconstruction.getReactome()
                                                                      .getReactions(metabolite));

        // remove from metabolome
        super.addEdit(new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                reconstruction.getMetabolome().add(metabolite);
            }

            @Override
            public void redo() throws CannotRedoException {
                reconstruction.getMetabolome().remove(metabolite);
            }
        });

        // remove references from reactome
        for (final MetabolicReaction reaction : reactions) {
            super.addEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    reactome.update(reaction);
                }

                @Override
                public void redo() throws CannotRedoException {
                    reactome.removeKey(metabolite, reaction);
                }
            });
            super.addEdit(new RemoveParticipantEdit(metabolite, reaction));
        }

        super.end();

    }


}
