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

import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

/**
 * An undoable edit for removing reactants from a reaction.
 *
 * @author John May
 */
public class RemoveReactantEdit extends AbstractUndoableEdit {

    private final MetabolicReaction reaction;
    private final MetabolicParticipant participant;

    public RemoveReactantEdit(MetabolicParticipant participant,
                              MetabolicReaction reaction) {

        if (participant == null)
            throw new IllegalArgumentException("null participant provided");
        if (reaction == null)
            throw new IllegalArgumentException("null reaction provided");

        this.participant = participant;
        this.reaction = reaction;
    }


    @Override
    public void redo() throws CannotRedoException {
        reaction.removeReactant(participant);
    }

    @Override
    public void undo() throws CannotRedoException {
        reaction.addReactant(participant);
    }

}
