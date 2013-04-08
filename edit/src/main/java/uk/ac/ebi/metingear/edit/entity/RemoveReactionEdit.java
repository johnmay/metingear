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

import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import java.util.Collection;

/** @author John May */
public class RemoveReactionEdit extends CompoundEdit {

    private final Reconstruction reconstruction;
    private final MetabolicReaction reaction;
    private final Collection<GeneProduct> modifiers;

    public RemoveReactionEdit(final Reconstruction reconstruction, final MetabolicReaction reaction) {

        this.reconstruction = reconstruction;
        this.reaction = reaction;

        modifiers = reconstruction.enzymesOf(reaction);

        // compound edit makes things easier if we need to add links from products -> reactions
        super.addEdit(new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                reconstruction.addReaction(reaction);
                for (final MetabolicParticipant p : reaction.getParticipants()) {
                    reconstruction.associate(p.getMolecule(), reaction);
                }
                for (final GeneProduct p : modifiers) {
                    reconstruction.associate(p, reaction);
                }
            }

            @Override
            public void redo() throws CannotRedoException {
                reconstruction.remove(reaction);
                for (final MetabolicParticipant p : reaction
                        .getParticipants()) {
                    reconstruction.dissociate(p.getMolecule(), reaction);
                }
                for (final GeneProduct p : modifiers) {
                    reconstruction.dissociate(p, reaction);
                }
            }
        });
        end();

    }


}
