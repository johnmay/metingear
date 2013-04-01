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

import org.junit.Test;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipantImplementation;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;

import javax.swing.undo.UndoableEdit;

import static org.junit.Assert.assertEquals;

/**
 * @author John May
 */
public class RemoveReactantEditTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullParticipant() {
        new RemoveReactantEdit(null, new MetabolicReactionImpl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullReaction() {
        new RemoveReactantEdit(new MetabolicParticipantImplementation(), null);
    }

    @Test
    public void testUndo() {

        MetabolicReactionImpl reaction = new MetabolicReactionImpl();

        MetabolicParticipant reactant = new MetabolicParticipantImplementation();
        MetabolicParticipant product = new MetabolicParticipantImplementation();

        reaction.addReactant(reactant);
        reaction.addProduct(product);

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

        UndoableEdit edit = new RemoveReactantEdit(reactant, reaction);

        reaction.removeReactant(reactant);

        assertEquals("reactant count was different,",
                     0, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

        edit.undo();

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

    }

    @Test
    public void testRedo() {

        MetabolicReactionImpl reaction = new MetabolicReactionImpl();

        MetabolicParticipant reactant = new MetabolicParticipantImplementation();
        MetabolicParticipant product = new MetabolicParticipantImplementation();

        reaction.addProduct(product);

        UndoableEdit edit = new RemoveReactantEdit(reactant, reaction);
        edit.undo();

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());


        edit.redo(); // redo the removal

        assertEquals("reactant count was different,",
                     0, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

    }


}
