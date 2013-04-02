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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.MetaboliteImpl;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.ReconstructionImpl;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;

/**
 * @author John May
 */
public class RenameMetaboliteEditTest {

    @Test public void testUndo() {

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("b");
        Metabolite c = new MetaboliteImpl("d");
        Metabolite d = new MetaboliteImpl("d");

        MetabolicReaction r1 = new MetabolicReactionImpl();
        MetabolicReaction r2 = new MetabolicReactionImpl();
        MetabolicReaction r3 = new MetabolicReactionImpl();

        Reconstruction reconstruction = new ReconstructionImpl();

        r1.addReactant(a);
        r1.addReactant(b);
        r1.addProduct(c);

        r2.addReactant(a);
        r2.addProduct(c);
        r2.addProduct(d);

        r3.addReactant(a);
        r3.addProduct(d);

        reconstruction.addReaction(r1);
        reconstruction.addReaction(r2);
        reconstruction.addReaction(r3);

        RenameMetaboliteEdit edit = new RenameMetaboliteEdit(a, "e", reconstruction);

        edit.apply(); // apply the edit

        Assert.assertEquals(2, r1.getReactantCount());
        Assert.assertEquals(1, r1.getProductCount());
        Assert.assertEquals(1, r2.getReactantCount());
        Assert.assertEquals(2, r2.getProductCount());
        Assert.assertEquals(1, r3.getReactantCount());
        Assert.assertEquals(1, r3.getProductCount());

        Assert.assertEquals("e", r1.getReactants().toArray(new MetabolicParticipant[0])[1].getMolecule().getName());
        Assert.assertEquals("e", r2.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule().getName());
        Assert.assertEquals("e", r3.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule().getName());

        Metabolite e = r1.getReactants().toArray(new MetabolicParticipant[0])[1].getMolecule();

        Assert.assertEquals(3, reconstruction.getReactome().participatesIn(e).size());

        Assert.assertFalse(reconstruction.getMetabolome().contains(a));
        Assert.assertTrue(reconstruction.getMetabolome().contains(e));

        edit.undo();

        Assert.assertEquals(2, r1.getReactantCount());
        Assert.assertEquals(1, r1.getProductCount());
        Assert.assertEquals(1, r2.getReactantCount());
        Assert.assertEquals(2, r2.getProductCount());
        Assert.assertEquals(1, r3.getReactantCount());
        Assert.assertEquals(1, r3.getProductCount());

        Assert.assertEquals(a, r1.getReactants().toArray(new MetabolicParticipant[0])[1].getMolecule());
        Assert.assertEquals(a, r2.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule());
        Assert.assertEquals(a, r3.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule());

        Assert.assertEquals(3, reconstruction.getReactome().participatesIn(a).size());

        Assert.assertFalse(reconstruction.getMetabolome().contains(e));
        Assert.assertTrue(reconstruction.getMetabolome().contains(a));


    }

    @Test public void testRedo() {

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("b");
        Metabolite c = new MetaboliteImpl("c");
        Metabolite d = new MetaboliteImpl("d");

        MetabolicReaction r1 = new MetabolicReactionImpl();
        MetabolicReaction r2 = new MetabolicReactionImpl();
        MetabolicReaction r3 = new MetabolicReactionImpl();

        Reconstruction reconstruction = new ReconstructionImpl();

        r1.addReactant(a);
        r1.addReactant(b);
        r1.addProduct(c);

        r2.addReactant(a);
        r2.addProduct(c);
        r2.addProduct(d);

        r3.addReactant(a);
        r3.addProduct(d);

        reconstruction.addReaction(r1);
        reconstruction.addReaction(r2);
        reconstruction.addReaction(r3);

        RenameMetaboliteEdit edit = new RenameMetaboliteEdit(a, "e", reconstruction);

        edit.apply(); // apply the edit

        Assert.assertEquals(2, r1.getReactantCount());
        Assert.assertEquals(1, r1.getProductCount());
        Assert.assertEquals(1, r2.getReactantCount());
        Assert.assertEquals(2, r2.getProductCount());
        Assert.assertEquals(1, r3.getReactantCount());
        Assert.assertEquals(1, r3.getProductCount());

        Assert.assertEquals("e", r1.getReactants().toArray(new MetabolicParticipant[0])[1].getMolecule().getName());
        Assert.assertEquals("e", r2.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule().getName());
        Assert.assertEquals("e", r3.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule().getName());

        Metabolite e = r1.getReactants().toArray(new MetabolicParticipant[0])[1].getMolecule();

        Assert.assertEquals(0, reconstruction.reactome().participatesIn(a).size());
        Assert.assertEquals(3, reconstruction.reactome().participatesIn(e).size());

        // done by identifier
        Assert.assertFalse(reconstruction.metabolome().contains(a));
        Assert.assertTrue(reconstruction.metabolome().contains(e));

        edit.undo();

        Assert.assertEquals(2, r1.getReactantCount());
        Assert.assertEquals(1, r1.getProductCount());
        Assert.assertEquals(1, r2.getReactantCount());
        Assert.assertEquals(2, r2.getProductCount());
        Assert.assertEquals(1, r3.getReactantCount());
        Assert.assertEquals(1, r3.getProductCount());

        Assert.assertEquals(a, r1.getReactants().toArray(new MetabolicParticipant[0])[1].getMolecule());
        Assert.assertEquals(a, r2.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule());
        Assert.assertEquals(a, r3.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule());

        Assert.assertEquals(3, reconstruction.getReactome().participatesIn(a).size());

        Assert.assertFalse(reconstruction.getMetabolome().contains(e));
        Assert.assertTrue(reconstruction.getMetabolome().contains(a));

        edit.redo();

        Assert.assertEquals(2, r1.getReactantCount());
        Assert.assertEquals(1, r1.getProductCount());
        Assert.assertEquals(1, r2.getReactantCount());
        Assert.assertEquals(2, r2.getProductCount());
        Assert.assertEquals(1, r3.getReactantCount());
        Assert.assertEquals(1, r3.getProductCount());

        Assert.assertEquals(e, r1.getReactants().toArray(new MetabolicParticipant[0])[1].getMolecule());
        Assert.assertEquals(e, r2.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule());
        Assert.assertEquals(e, r3.getReactants().toArray(new MetabolicParticipant[0])[0].getMolecule());

        Assert.assertEquals(3, reconstruction.getReactome().participatesIn(e).size());

        Assert.assertFalse(reconstruction.getMetabolome().contains(a));
        Assert.assertTrue(reconstruction.getMetabolome().contains(e));


    }

}
