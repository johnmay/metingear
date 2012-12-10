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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.MetaboliteImpl;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.ReconstructionImpl;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipantImplementation;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;

/**
 * @author John May
 */
public class MergeMetaboliteEditTest {

    @Test
    public void testApply() throws Exception {

        MetabolicReaction r1 = new MetabolicReactionImpl();
        MetabolicReaction r2 = new MetabolicReactionImpl();

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("b");
        Metabolite c = new MetaboliteImpl("c");
        Metabolite d = new MetaboliteImpl("d");

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));
        r1.addProduct(new MetabolicParticipantImplementation(c));

        r2.addReactant(new MetabolicParticipantImplementation(a));
        r2.addReactant(new MetabolicParticipantImplementation(b));
        r2.addProduct(new MetabolicParticipantImplementation(d));

        Metabolite cd = new MetaboliteImpl("cd");

        Reconstruction reconstruction = new ReconstructionImpl();
        // add reactions (and their reactants)
        reconstruction.addReaction(r1);
        reconstruction.addReaction(r2);

        MergeMetaboliteEdit edit = new MergeMetaboliteEdit(Arrays.asList(c, d), cd, reconstruction);

        // apply the edit
        edit.apply();

        Assert.assertThat(r1.getProductCount(), is(1));
        Assert.assertThat(r2.getProductCount(), is(1));
        Assert.assertThat("merged metabolites should be the same instance across reactions",
                          r1.getProducts().get(0).getMolecule(),
                          is(sameInstance(r2.getProducts().get(0).getMolecule())));
        Assert.assertThat("the new reaction participants should not be the same instance",
                          r1.getProducts().get(0),
                          is(not(sameInstance(r2.getProducts().get(0)))));


    }

    @Test
    public void testUndo() throws Exception {

        MetabolicReaction r1 = new MetabolicReactionImpl();
        MetabolicReaction r2 = new MetabolicReactionImpl();

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("b");
        Metabolite c = new MetaboliteImpl("c");
        Metabolite d = new MetaboliteImpl("d");

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));
        r1.addProduct(new MetabolicParticipantImplementation(c));

        r2.addReactant(new MetabolicParticipantImplementation(a));
        r2.addReactant(new MetabolicParticipantImplementation(b));
        r2.addProduct(new MetabolicParticipantImplementation(d));

        Metabolite cd = new MetaboliteImpl("cd");

        Reconstruction reconstruction = new ReconstructionImpl();
        // add reactions (and their reactants)
        reconstruction.addReaction(r1);
        reconstruction.addReaction(r2);

        MergeMetaboliteEdit edit = new MergeMetaboliteEdit(Arrays.asList(c, d), cd, reconstruction);

        // apply the edit
        edit.apply();

        Assert.assertThat(r1.getProductCount(), is(1));
        Assert.assertThat(r2.getProductCount(), is(1));
        Assert.assertThat("merged metabolites should be the same instance across reactions",
                          r1.getProducts().get(0).getMolecule(),
                          is(sameInstance(r2.getProducts().get(0).getMolecule())));
        Assert.assertThat("the new reaction participants should not be the same instance",
                          r1.getProducts().get(0),
                          is(not(sameInstance(r2.getProducts().get(0)))));

        edit.undo();

        // try undoing the applied edit
        Assert.assertThat(r1.getProductCount(), is(1));
        Assert.assertThat(r2.getProductCount(), is(1));
        Assert.assertThat("merged metabolites should be not be the same instance across reactions (after undo)",
                          r1.getProducts().get(0).getMolecule(),
                          is(not(sameInstance(r2.getProducts().get(0).getMolecule()))));
        Assert.assertThat(r1.getProducts().get(0),
                          is(not(sameInstance(r2.getProducts().get(0)))));


    }

    @Test
    public void testRedo() throws Exception {
        MetabolicReaction r1 = new MetabolicReactionImpl();
        MetabolicReaction r2 = new MetabolicReactionImpl();

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("b");
        Metabolite c = new MetaboliteImpl("c");
        Metabolite d = new MetaboliteImpl("d");

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));
        r1.addProduct(new MetabolicParticipantImplementation(c));

        r2.addReactant(new MetabolicParticipantImplementation(a));
        r2.addReactant(new MetabolicParticipantImplementation(b));
        r2.addProduct(new MetabolicParticipantImplementation(d));

        Metabolite cd = new MetaboliteImpl("cd");

        Reconstruction reconstruction = new ReconstructionImpl();
        // add reactions (and their reactants)
        reconstruction.addReaction(r1);
        reconstruction.addReaction(r2);

        MergeMetaboliteEdit edit = new MergeMetaboliteEdit(Arrays.asList(c, d), cd, reconstruction);

        // apply the edit
        edit.apply();

        Assert.assertThat(r1.getProductCount(), is(1));
        Assert.assertThat(r2.getProductCount(), is(1));
        Assert.assertThat("merged metabolites should be the same instance across reactions",
                          r1.getProducts().get(0).getMolecule(),
                          is(sameInstance(r2.getProducts().get(0).getMolecule())));
        Assert.assertThat("the new reaction participants should not be the same instance",
                          r1.getProducts().get(0),
                          is(not(sameInstance(r2.getProducts().get(0)))));

        edit.undo();

        // try undoing the applied edit
        Assert.assertThat(r1.getProductCount(), is(1));
        Assert.assertThat(r2.getProductCount(), is(1));
        Assert.assertThat("merged metabolites should be not be the same instance across reactions (after undo)",
                          r1.getProducts().get(0).getMolecule(),
                          is(not(sameInstance(r2.getProducts().get(0).getMolecule()))));
        Assert.assertThat(r1.getProducts().get(0),
                          is(not(sameInstance(r2.getProducts().get(0)))));

        edit.redo();

        Assert.assertThat(r1.getProductCount(), is(1));
        Assert.assertThat(r2.getProductCount(), is(1));
        Assert.assertThat("merged metabolites should be the same instance across reactions",
                          r1.getProducts().get(0).getMolecule(),
                          is(sameInstance(r2.getProducts().get(0).getMolecule())));
        Assert.assertThat("the new reaction participants should not be the same instance",
                          r1.getProducts().get(0),
                          is(not(sameInstance(r2.getProducts().get(0)))));


    }
}
