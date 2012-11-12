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

import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.undo.CompoundEdit;

/**
 * An undoable edit for removing participants from a reaction.
 *
 * @author John May
 */
public class RemoveParticipantEdit extends CompoundEdit {

    /**
     * Create a new participant edit. The constructor uses reference equality to
     * find the participant in the reaction and create a new {@link
     * RemoveReactantEdit} or {@link RemoveProductEdit} depending on which side
     * of the reaction the participant is on.
     * <p/>
     * If the participant is not found in the reaction an {@link
     * IllegalArgumentException} is thrown.
     *
     * @param participant the participant to remove
     * @param reaction    the reaction to remove
     */
    public RemoveParticipantEdit(MetabolicParticipant participant,
                                 MetabolicReaction reaction) {

        if (participant == null)
            throw new IllegalArgumentException("null participant provided");
        if (reaction == null)
            throw new IllegalArgumentException("null reaction provided");


        for (MetabolicParticipant p : reaction.getReactants()) {
            if (p == participant) {  // intended reference equality
                super.addEdit(new RemoveReactantEdit(participant, reaction));
                super.end();
                return;
            }
        }

        for (MetabolicParticipant p : reaction.getProducts()) {
            if (p == participant) {   // intended reference equality
                super.addEdit(new RemoveProductEdit(participant, reaction));
                super.end();
                return;
            }
        }

        throw new IllegalArgumentException("Participant is not present in reaction");

    }


}
