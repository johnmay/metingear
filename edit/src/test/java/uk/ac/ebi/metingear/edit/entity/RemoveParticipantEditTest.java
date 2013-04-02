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

import org.junit.Test;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.MetaboliteImpl;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipantImplementation;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;

import javax.swing.undo.UndoableEdit;

import static org.junit.Assert.assertEquals;

/**
 * @author John May
 */
public class RemoveParticipantEditTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullParticipant() {
        new RemoveParticipantEdit((MetabolicParticipant) null, new MetabolicReactionImpl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullMetabolite() {
        new RemoveParticipantEdit((Metabolite) null, new MetabolicReactionImpl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullReaction() {
        new RemoveParticipantEdit(new MetabolicParticipantImplementation(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_MissingParticipant() {
        // participant is not present in the reaction
        new RemoveParticipantEdit(new MetabolicParticipantImplementation(), new MetabolicReactionImpl());
    }

    @Test
    public void testUndo_reactant() {

        MetabolicReactionImpl reaction = new MetabolicReactionImpl();

        MetabolicParticipant reactant = new MetabolicParticipantImplementation();
        MetabolicParticipant product = new MetabolicParticipantImplementation();

        reaction.addReactant(reactant);
        reaction.addProduct(product);

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

        UndoableEdit edit = new RemoveParticipantEdit(reactant, reaction);

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
    public void testRedo_reactant() {

        MetabolicReactionImpl reaction = new MetabolicReactionImpl();

        MetabolicParticipant reactant = new MetabolicParticipantImplementation();
        MetabolicParticipant product = new MetabolicParticipantImplementation();

        reaction.addProduct(product);
        reaction.addReactant(reactant);

        UndoableEdit edit = new RemoveParticipantEdit(reactant, reaction);

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

        edit.redo(); // redo the removal

        assertEquals("reactant count was different,",
                     0, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

    }

    @Test
    public void testUndo_product() {

        MetabolicReactionImpl reaction = new MetabolicReactionImpl();

        MetabolicParticipant reactant = new MetabolicParticipantImplementation();
        MetabolicParticipant product = new MetabolicParticipantImplementation();

        reaction.addReactant(reactant);
        reaction.addProduct(product);

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

        UndoableEdit edit = new RemoveParticipantEdit(product, reaction);

        reaction.removeProduct(product);

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     0, reaction.getProductCount());

        edit.undo();

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

    }

    @Test
    public void testRedo_product() {

        MetabolicReactionImpl reaction = new MetabolicReactionImpl();

        MetabolicParticipant reactant = new MetabolicParticipantImplementation();
        MetabolicParticipant product = new MetabolicParticipantImplementation();

        reaction.addProduct(product);
        reaction.addReactant(reactant);

        UndoableEdit edit = new RemoveParticipantEdit(product, reaction);

        reaction.removeProduct(product);

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     0, reaction.getProductCount());

        edit.undo();

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

        edit.redo(); // redo the removal

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     0, reaction.getProductCount());


    }

    @Test
    public void testRemoveMetabolite() {
        MetabolicReactionImpl reaction = new MetabolicReactionImpl();

        Metabolite hydrogen = new MetaboliteImpl("", "H+");

        MetabolicParticipant reactantC = new MetabolicParticipantImplementation(hydrogen, Organelle.CYTOPLASM);
        MetabolicParticipant reactantE = new MetabolicParticipantImplementation(hydrogen, Organelle.EXTRACELLULAR);

        reaction.addReactant(reactantC);
        reaction.addReactant(reactantE);

        UndoableEdit edit = new RemoveParticipantEdit(hydrogen, reaction);

        assertEquals("reactant count was different,",
                     2, reaction.getReactantCount());

        reaction.removeReactant(reactantC);
        reaction.removeReactant(reactantE);

        assertEquals("reactant count was different,",
                     0, reaction.getReactantCount());

        edit.undo();

        assertEquals("reactant count was different,",
                     2, reaction.getReactantCount());

    }

    /**
     * Ensures if a participant is pressent in the reactants and participants
     * then both are removed
     */
    @Test
    public void testUndo_duplicate() {

        MetabolicReactionImpl reaction = new MetabolicReactionImpl();

        MetabolicParticipant reactant = new MetabolicParticipantImplementation();
        MetabolicParticipant product = new MetabolicParticipantImplementation();

        reaction.addReactant(reactant);
        reaction.addProduct(reactant);
        reaction.addProduct(product);

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     2, reaction.getProductCount());

        UndoableEdit edit = new RemoveParticipantEdit(reactant, reaction);

        reaction.removeReactant(reactant);
        reaction.removeProduct(product);

        assertEquals("reactant count was different,",
                     0, reaction.getReactantCount());
        assertEquals("product count was different,",
                     1, reaction.getProductCount());

        edit.undo();

        assertEquals("reactant count was different,",
                     1, reaction.getReactantCount());
        assertEquals("product count was different,",
                     2, reaction.getProductCount());

    }


}
