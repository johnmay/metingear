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
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.MetaboliteImpl;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.ReconstructionImpl;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 */
public class SplitMetaboliteEditTest {

    @Test
    public void apply() throws Exception {

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("c");
        Metabolite c = new MetaboliteImpl("d");
        Metabolite d = new MetaboliteImpl("e");

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

        Metabolite a1 = new MetaboliteImpl("a1");
        Metabolite a2 = new MetaboliteImpl("a2");

        SplitMetaboliteEdit edit = new SplitMetaboliteEdit(a,
                                                           a1, Arrays.asList(r1, r2),
                                                           a2, Arrays.asList(r3),
                                                           reconstruction);

        edit.apply(); // apply the edit

        assertEquals(2, r1.getReactantCount());
        assertEquals(1, r1.getProductCount());

        assertEquals(1, r2.getReactantCount());
        assertEquals(2, r2.getProductCount());

        assertEquals(1, r3.getReactantCount());
        assertEquals(1, r3.getProductCount());

        // a1 -> r1 and r2
        assertThat(r1.getReactants().get(1).getMolecule(),
                   is(sameInstance(a1)));
        assertThat(r2.getReactants().get(0).getMolecule(),
                   is(sameInstance(a1)));

        // a2 -> r3
        assertThat(r3.getReactants().get(0).getMolecule(),
                   is(sameInstance(a2)));


        assertTrue("all references of 'a' should be removed",
                   reconstruction.getReactome().getReactions(a).isEmpty());

        assertThat("'a1' should have 2 reactions",
                   reconstruction.getReactome().getReactions(a1).size(),
                   is(2));
        assertThat("'a2' should have 1 reactions",
                   reconstruction.getReactome().getReactions(a2).size(),
                   is(1));


    }


    @Test
    public void testUndo() throws Exception {

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("c");
        Metabolite c = new MetaboliteImpl("d");
        Metabolite d = new MetaboliteImpl("e");

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

        Metabolite a1 = new MetaboliteImpl("a1");
        Metabolite a2 = new MetaboliteImpl("a2");

        SplitMetaboliteEdit edit = new SplitMetaboliteEdit(a,
                                                           a1, Arrays.asList(r1, r2),
                                                           a2, Arrays.asList(r3),
                                                           reconstruction);

        edit.apply(); // apply the edit

        assertEquals(2, r1.getReactantCount());
        assertEquals(1, r1.getProductCount());

        assertEquals(1, r2.getReactantCount());
        assertEquals(2, r2.getProductCount());

        assertEquals(1, r3.getReactantCount());
        assertEquals(1, r3.getProductCount());

        // a1 -> r1 and r2
        assertThat(r1.getReactants().get(1).getMolecule(),
                   is(sameInstance(a1)));
        assertThat(r2.getReactants().get(0).getMolecule(),
                   is(sameInstance(a1)));

        // a2 -> r3
        assertThat(r3.getReactants().get(0).getMolecule(),
                   is(sameInstance(a2)));


        assertTrue("all references of 'a' should be removed",
                   reconstruction.getReactome().getReactions(a).isEmpty());

        assertThat("'a1' should have 2 reactions",
                   reconstruction.getReactome().getReactions(a1).size(),
                   is(2));
        assertThat("'a2' should have 1 reactions",
                   reconstruction.getReactome().getReactions(a2).size(),
                   is(1));

        edit.undo();

        assertEquals(2, r1.getReactantCount());
        assertEquals(1, r1.getProductCount());

        assertEquals(1, r2.getReactantCount());
        assertEquals(2, r2.getProductCount());

        assertEquals(1, r3.getReactantCount());
        assertEquals(1, r3.getProductCount());

        // a1 should no long have r1 and r2
        assertThat(r1.getReactants().get(1).getMolecule(),
                   is(not(sameInstance(a1))));
        assertThat(r2.getReactants().get(0).getMolecule(),
                   is(not(sameInstance(a1))));
        assertThat(r1.getReactants().get(1).getMolecule(),
                   is(sameInstance(a)));
        assertThat(r2.getReactants().get(0).getMolecule(),
                   is(sameInstance(a)));

        // a2 should no long have r3
        assertThat(r3.getReactants().get(0).getMolecule(),
                   is(not(sameInstance(a2))));
        assertThat(r3.getReactants().get(0).getMolecule(),
                   is(sameInstance(a)));


        assertThat("'a1' should have 3 reactions",
                   reconstruction.getReactome().getReactions(a).size(),
                   is(3));
        assertThat("'a1' should have 0 reactions",
                   reconstruction.getReactome().getReactions(a1).size(),
                   is(0));
        assertThat("'a2' should have 0 reactions",
                   reconstruction.getReactome().getReactions(a2).size(),
                   is(0));

    }

    @Test
    public void testRedo() throws Exception {


        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("c");
        Metabolite c = new MetaboliteImpl("d");
        Metabolite d = new MetaboliteImpl("e");

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

        Metabolite a1 = new MetaboliteImpl("a1");
        Metabolite a2 = new MetaboliteImpl("a2");

        SplitMetaboliteEdit edit = new SplitMetaboliteEdit(a,
                                                           a1, Arrays.asList(r1, r2),
                                                           a2, Arrays.asList(r3),
                                                           reconstruction);

        edit.apply(); // apply the edit

        assertEquals(2, r1.getReactantCount());
        assertEquals(1, r1.getProductCount());

        assertEquals(1, r2.getReactantCount());
        assertEquals(2, r2.getProductCount());

        assertEquals(1, r3.getReactantCount());
        assertEquals(1, r3.getProductCount());

        // a1 -> r1 and r2
        assertThat(r1.getReactants().get(1).getMolecule(),
                   is(sameInstance(a1)));
        assertThat(r2.getReactants().get(0).getMolecule(),
                   is(sameInstance(a1)));

        // a2 -> r3
        assertThat(r3.getReactants().get(0).getMolecule(),
                   is(sameInstance(a2)));


        assertTrue("all references of 'a' should be removed",
                   reconstruction.getReactome().getReactions(a).isEmpty());

        assertThat("'a1' should have 2 reactions",
                   reconstruction.getReactome().getReactions(a1).size(),
                   is(2));
        assertThat("'a2' should have 1 reactions",
                   reconstruction.getReactome().getReactions(a2).size(),
                   is(1));

        edit.undo();

        assertEquals(2, r1.getReactantCount());
        assertEquals(1, r1.getProductCount());

        assertEquals(1, r2.getReactantCount());
        assertEquals(2, r2.getProductCount());

        assertEquals(1, r3.getReactantCount());
        assertEquals(1, r3.getProductCount());

        // a1 should no long have r1 and r2
        assertThat(r1.getReactants().get(1).getMolecule(),
                   is(not(sameInstance(a1))));
        assertThat(r2.getReactants().get(0).getMolecule(),
                   is(not(sameInstance(a1))));
        assertThat(r1.getReactants().get(1).getMolecule(),
                   is(sameInstance(a)));
        assertThat(r2.getReactants().get(0).getMolecule(),
                   is(sameInstance(a)));

        // a2 should no long have r3
        assertThat(r3.getReactants().get(0).getMolecule(),
                   is(not(sameInstance(a2))));
        assertThat(r3.getReactants().get(0).getMolecule(),
                   is(sameInstance(a)));


        assertThat("'a1' should have 3 reactions",
                   reconstruction.getReactome().getReactions(a).size(),
                   is(3));
        assertThat("'a1' should have 0 reactions",
                   reconstruction.getReactome().getReactions(a1).size(),
                   is(0));
        assertThat("'a2' should have 0 reactions",
                   reconstruction.getReactome().getReactions(a2).size(),
                   is(0));

        edit.redo(); // redo the action

        assertEquals(2, r1.getReactantCount());
        assertEquals(1, r1.getProductCount());

        assertEquals(1, r2.getReactantCount());
        assertEquals(2, r2.getProductCount());

        assertEquals(1, r3.getReactantCount());
        assertEquals(1, r3.getProductCount());

        // a1 should no long have r1 and r2
        assertThat(r1.getReactants().get(1).getMolecule(),
                   is(sameInstance(a1)));
        assertThat(r2.getReactants().get(0).getMolecule(),
                   is(sameInstance(a1)));
        assertThat(r1.getReactants().get(1).getMolecule(),
                   is(not(sameInstance(a))));
        assertThat(r2.getReactants().get(0).getMolecule(),
                   is(not(sameInstance(a))));

        // a2 should no long have r3
        assertThat(r3.getReactants().get(0).getMolecule(),
                   is(sameInstance(a2)));
        assertThat(r3.getReactants().get(0).getMolecule(),
                   is(not(sameInstance(a))));


        assertThat("'a1' should have 0 reactions",
                   reconstruction.getReactome().getReactions(a).size(),
                   is(0));
        assertThat("'a1' should have 2 reactions",
                   reconstruction.getReactome().getReactions(a1).size(),
                   is(2));
        assertThat("'a2' should have 1 reactions",
                   reconstruction.getReactome().getReactions(a2).size(),
                   is(1));

    }
}
