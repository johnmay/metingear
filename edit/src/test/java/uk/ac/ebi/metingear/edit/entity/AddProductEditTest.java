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
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipantImplementation;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;

import javax.swing.undo.UndoableEdit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;

/**
 * @author John May
 */
public class AddProductEditTest {

    @Test
    public void testUndo() throws Exception {

        MetabolicReaction r1 = new MetabolicReactionImpl();

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("b");
        Metabolite c = new MetaboliteImpl("c");

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));

        UndoableEdit edit = new AddProductEdit(new MetabolicParticipantImplementation(c), r1);

        r1.addProduct(new MetabolicParticipantImplementation(c));

        Assert.assertThat(r1.getProductCount(), is(1));
        Assert.assertThat(r1.getProducts().get(0).getMolecule(), is(sameInstance(c)));

        edit.undo();

        Assert.assertThat(r1.getProductCount(), is(0));

    }

    @Test
    public void testUndo_MultipleProducts() throws Exception {

        MetabolicReaction r1 = new MetabolicReactionImpl();

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("b");
        Metabolite c = new MetaboliteImpl("c");
        Metabolite d = new MetaboliteImpl("d");

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));
        r1.addProduct(new MetabolicParticipantImplementation(d));

        UndoableEdit edit = new AddProductEdit(new MetabolicParticipantImplementation(c), r1);

        r1.addProduct(new MetabolicParticipantImplementation(c));


        Assert.assertThat(r1.getProductCount(), is(2));
        Assert.assertThat(r1.getProducts().get(0).getMolecule(), is(sameInstance(d)));
        Assert.assertThat(r1.getProducts().get(1).getMolecule(), is(sameInstance(c)));

        edit.undo();

        Assert.assertThat(r1.getProductCount(), is(1));

    }

    @Test
    public void testRedo() throws Exception {

        MetabolicReaction r1 = new MetabolicReactionImpl();

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("b");
        Metabolite c = new MetaboliteImpl("c");

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));

        UndoableEdit edit = new AddProductEdit(new MetabolicParticipantImplementation(c), r1);

        edit.undo();

        Assert.assertThat(r1.getProductCount(), is(0));

        edit.redo();

        Assert.assertThat(r1.getProductCount(), is(1));
        Assert.assertThat(r1.getProducts().get(0).getMolecule(), is(c));


    }

    @Test
    public void testRedo_Multiple() throws Exception {

        MetabolicReaction r1 = new MetabolicReactionImpl();

        Metabolite a = new MetaboliteImpl("a");
        Metabolite b = new MetaboliteImpl("b");
        Metabolite c = new MetaboliteImpl("c");
        Metabolite d = new MetaboliteImpl("d");

        r1.addReactant(new MetabolicParticipantImplementation(a));
        r1.addReactant(new MetabolicParticipantImplementation(b));
        r1.addProduct(new MetabolicParticipantImplementation(d));

        UndoableEdit edit = new AddProductEdit(new MetabolicParticipantImplementation(c), r1);

        edit.undo();

        Assert.assertThat(r1.getProductCount(), is(1));

        edit.redo();

        Assert.assertThat(r1.getProductCount(), is(2));
        Assert.assertThat(r1.getProducts().get(0).getMolecule(), is(d));
        Assert.assertThat(r1.getProducts().get(1).getMolecule(), is(c));


    }
}
