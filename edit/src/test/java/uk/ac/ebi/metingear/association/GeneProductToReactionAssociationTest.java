/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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

package uk.ac.ebi.metingear.association;

import org.junit.Test;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.metingear.AppliableEdit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author John May
 */
public class GeneProductToReactionAssociationTest {

    @Test public void apply() {
        Reconstruction recon = mock(Reconstruction.class);
        MetabolicReaction reaction = mock(MetabolicReaction.class);
        GeneProduct product = mock(GeneProduct.class);

        AppliableEdit edit = new GeneProductToReactionAssociation(recon, product, reaction);

        edit.apply();

        verify(recon).associate(product, reaction);
    }

    @Test public void undo() {
        Reconstruction recon = mock(Reconstruction.class);
        MetabolicReaction reaction = mock(MetabolicReaction.class);
        GeneProduct product = mock(GeneProduct.class);

        AppliableEdit edit = new GeneProductToReactionAssociation(recon, product, reaction);

        edit.apply();

        verify(recon).associate(product, reaction);

        edit.undo();

        verify(recon).dissociate(product, reaction);
    }

    @Test public void redo() {
        Reconstruction recon = mock(Reconstruction.class);
        MetabolicReaction reaction = mock(MetabolicReaction.class);
        GeneProduct product = mock(GeneProduct.class);

        AppliableEdit edit = new GeneProductToReactionAssociation(recon, product, reaction);

        edit.apply();

        verify(recon).associate(product, reaction);

        edit.undo();

        verify(recon).dissociate(product, reaction);

        edit.redo();

        verify(recon).dissociate(product, reaction);
    }

}
