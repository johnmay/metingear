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

package uk.ac.ebi.metingear;

import org.junit.Test;
import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.undo.UndoableEdit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author John May
 */
public class EditBuilderTest {

    @Test
    public void associate_GeneToGeneProduct() throws Exception {
        Reconstruction recon = mock(Reconstruction.class);
        Gene g1 = mock(Gene.class);
        GeneProduct p1 = mock(GeneProduct.class);
        Gene g2 = mock(Gene.class);
        GeneProduct p2 = mock(GeneProduct.class);

        UndoableEdit edit = new EditBuilder(recon).associate(g1).with(p1)
                                                  .associate(g2).with(p2)
                                                  .apply();

        verify(recon).associate(g1, p1);
        verify(recon).associate(g2, p2);

        edit.undo();

        verify(recon).dissociate(g1, p1);
        verify(recon).dissociate(g2, p2);
    }

    @Test
    public void dissociate_GeneToGeneProduct() throws Exception {
        Reconstruction recon = mock(Reconstruction.class);
        Gene g1 = mock(Gene.class);
        GeneProduct p1 = mock(GeneProduct.class);
        Gene g2 = mock(Gene.class);
        GeneProduct p2 = mock(GeneProduct.class);

        UndoableEdit edit = new EditBuilder(recon).dissociate(g1).with(p1)
                                                  .dissociate(g2).with(p2)
                                                  .apply();

        verify(recon).dissociate(g1, p1);
        verify(recon).dissociate(g2, p2);

        edit.undo();

        verify(recon).associate(g1, p1);
        verify(recon).associate(g2, p2);
    }

    @Test
    public void associate_GeneProductToReaction() throws Exception {
        Reconstruction recon = mock(Reconstruction.class);
        MetabolicReaction r1 = mock(MetabolicReaction.class);
        GeneProduct p1 = mock(GeneProduct.class);
        MetabolicReaction r2 = mock(MetabolicReaction.class);
        GeneProduct p2 = mock(GeneProduct.class);

        UndoableEdit edit = new EditBuilder(recon).associate(p1).with(r1)
                                                  .associate(p2).with(r2)
                                                  .apply();

        verify(recon).associate(p1, r1);
        verify(recon).associate(p2, r2);

        edit.undo();

        verify(recon).dissociate(p1, r1);
        verify(recon).dissociate(p2, r2);
    }

    @Test
    public void dissociate_GeneProductToReaction() throws Exception {
        Reconstruction recon = mock(Reconstruction.class);
        MetabolicReaction r1 = mock(MetabolicReaction.class);
        GeneProduct p1 = mock(GeneProduct.class);
        MetabolicReaction r2 = mock(MetabolicReaction.class);
        GeneProduct p2 = mock(GeneProduct.class);

        UndoableEdit edit = new EditBuilder(recon).dissociate(p1).with(r1)
                                                  .dissociate(p2).with(r2)
                                                  .apply();

        verify(recon).dissociate(p1, r1);
        verify(recon).dissociate(p2, r2);

        edit.undo();

        verify(recon).associate(p1, r1);
        verify(recon).associate(p2, r2);
    }


}
