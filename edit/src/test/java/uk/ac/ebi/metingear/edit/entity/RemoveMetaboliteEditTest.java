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
import uk.ac.ebi.mdk.domain.entity.MetaboliteImpl;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.ReconstructionImpl;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipantImplementation;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;

import javax.swing.undo.UndoableEdit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 */
public class RemoveMetaboliteEditTest {

    @Test
    public void testUndo() {

        MetaboliteImpl a = new MetaboliteImpl("a", "a");
        MetaboliteImpl b = new MetaboliteImpl("b", "b");
        MetaboliteImpl c = new MetaboliteImpl("c", "c");
        MetaboliteImpl d = new MetaboliteImpl("d", "d");

        MetabolicReaction r1 = new MetabolicReactionImpl();
        MetabolicReaction r2 = new MetabolicReactionImpl();

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));
        r1.addProduct(new MetabolicParticipantImplementation(c));

        r2.addReactant(new MetabolicParticipantImplementation(c));
        r2.addReactant(new MetabolicParticipantImplementation(d));
        r2.addProduct(new MetabolicParticipantImplementation(a));

        Reconstruction reconstruction = new ReconstructionImpl();

        reconstruction.addMetabolite(a);
        reconstruction.addMetabolite(b);
        reconstruction.addMetabolite(c);
        reconstruction.addMetabolite(d);
        reconstruction.getReactome().add(r1);
        reconstruction.getReactome().add(r2);

        UndoableEdit edit = new RemoveMetaboliteEdit(reconstruction, a);

        // remove metabolite 'a'
        reconstruction.remove(a);

        assertFalse("metabolome contained metabolite, ",
                    reconstruction.getMetabolome().contains(a));
        assertEquals("metabolome contained metabolite, ",
                     0,
                     reconstruction.getReactome().participatesIn(a).size());
        assertEquals("incorrect r1 reactant count, ",
                     1,
                     r1.getReactantCount());
        assertEquals("incorrect r2 product count, ",
                     0,
                     r2.getProductCount());

        // undo the removal
        edit.undo();

        assertTrue("metabolome did not contain metabolite, ",
                   reconstruction.getMetabolome().contains(a));
        assertEquals("metabolome contained metabolite, ",
                     2,
                     reconstruction.getReactome().participatesIn(a).size());
        assertEquals("incorrect r1 reactant count, ",
                     2,
                     r1.getReactantCount());
        assertEquals("incorrect r2 product count, ",
                     1,
                     r2.getProductCount());

    }

    @Test
    public void testUndo_duplicate() {

        MetaboliteImpl a = new MetaboliteImpl("a", "a");
        MetaboliteImpl b = new MetaboliteImpl("b", "b");
        MetaboliteImpl c = new MetaboliteImpl("c", "c");
        MetaboliteImpl d = new MetaboliteImpl("d", "d");

        MetabolicReaction r1 = new MetabolicReactionImpl();
        MetabolicReaction r2 = new MetabolicReactionImpl();

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));
        r1.addProduct(new MetabolicParticipantImplementation(a));
        r1.addProduct(new MetabolicParticipantImplementation(d));

        r2.addReactant(new MetabolicParticipantImplementation(c));
        r2.addReactant(new MetabolicParticipantImplementation(d));
        r2.addProduct(new MetabolicParticipantImplementation(a));

        Reconstruction reconstruction = new ReconstructionImpl();

        reconstruction.addMetabolite(a);
        reconstruction.addMetabolite(b);
        reconstruction.addMetabolite(c);
        reconstruction.addMetabolite(d);
        reconstruction.getReactome().add(r1);
        reconstruction.getReactome().add(r2);

        UndoableEdit edit = new RemoveMetaboliteEdit(reconstruction, a);

        // remove metabolite 'a'
        reconstruction.remove(a);

        assertFalse("metabolome contained metabolite, ",
                    reconstruction.getMetabolome().contains(a));
        assertEquals("metabolome contained metabolite, ",
                     0,
                     reconstruction.getReactome().participatesIn(a).size());
        assertEquals("incorrect r1 reactant count, ",
                     1,
                     r1.getReactantCount());
        assertEquals("incorrect r1 product count, ",
                     1,
                     r1.getReactantCount());
        assertEquals("incorrect r2 product count, ",
                     0,
                     r2.getProductCount());

        // undo the removal
        edit.undo();

        assertTrue("metabolome did not contain metabolite, ",
                   reconstruction.getMetabolome().contains(a));
        assertEquals("metabolome contained metabolite, ",
                     2,
                     reconstruction.getReactome().participatesIn(a).size());
        assertEquals("incorrect r1 reactant count, ",
                     2,
                     r1.getReactantCount());
        assertEquals("incorrect r1 reactant count, ",
                     2,
                     r1.getProductCount());
        assertEquals("incorrect r2 product count, ",
                     1,
                     r2.getProductCount());


    }

    @Test
    public void testRedo() {

        MetaboliteImpl a = new MetaboliteImpl("a", "a");
        MetaboliteImpl b = new MetaboliteImpl("b", "b");
        MetaboliteImpl c = new MetaboliteImpl("c", "c");
        MetaboliteImpl d = new MetaboliteImpl("d", "d");

        MetabolicReaction r1 = new MetabolicReactionImpl();
        MetabolicReaction r2 = new MetabolicReactionImpl();

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));
        r1.addProduct(new MetabolicParticipantImplementation(c));

        r2.addReactant(new MetabolicParticipantImplementation(c));
        r2.addReactant(new MetabolicParticipantImplementation(d));
        r2.addProduct(new MetabolicParticipantImplementation(a));

        Reconstruction reconstruction = new ReconstructionImpl();

        reconstruction.addMetabolite(a);
        reconstruction.addMetabolite(b);
        reconstruction.addMetabolite(c);
        reconstruction.addMetabolite(d);
        reconstruction.getReactome().add(r1);
        reconstruction.getReactome().add(r2);

        UndoableEdit edit = new RemoveMetaboliteEdit(reconstruction, a);

        // remove metabolite 'a'
        reconstruction.remove(a);

        assertFalse("metabolome contained metabolite, ",
                    reconstruction.getMetabolome().contains(a));
        assertEquals("metabolome contained metabolite, ",
                     0,
                     reconstruction.getReactome().participatesIn(a).size());
        assertEquals("incorrect r1 reactant count, ",
                     1,
                     r1.getReactantCount());
        assertEquals("incorrect r2 product count, ",
                     0,
                     r2.getProductCount());

        // undo the removal
        edit.undo();

        assertTrue("metabolome did not contain metabolite, ",
                   reconstruction.getMetabolome().contains(a));
        assertEquals("metabolome contained metabolite, ",
                     2,
                     reconstruction.getReactome().participatesIn(a).size());
        assertEquals("incorrect r1 reactant count, ",
                     2,
                     r1.getReactantCount());
        assertEquals("incorrect r2 product count, ",
                     1,
                     r2.getProductCount());

        // redo the removal
        edit.redo();

        assertFalse("metabolome contained metabolite, ",
                    reconstruction.getMetabolome().contains(a));
        assertEquals("metabolome contained metabolite, ",
                     0,
                     reconstruction.getReactome().participatesIn(a).size());
        assertEquals("incorrect r1 reactant count, ",
                     1,
                     r1.getReactantCount());
        assertEquals("incorrect r2 product count, ",
                     0,
                     r2.getProductCount());

    }


}
