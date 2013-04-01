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

import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.metingear.AppliableEdit;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * UndoableEdit to dissociate a gene with a gene product.
 *
 * @author John May
 */
public class GeneToGeneProductDissociation
        extends AbstractUndoableEdit
        implements AppliableEdit {

    private final Reconstruction reconstruction;
    private final GeneProduct product;
    private final Gene gene;

    public GeneToGeneProductDissociation(Reconstruction reconstruction,
                                         Gene gene,
                                         GeneProduct product) {
        this.reconstruction = reconstruction;
        this.product = product;
        this.gene = gene;
    }

    @Override public void apply() {
        reconstruction.dissociate(gene, product);
    }

    @Override public void undo() throws CannotUndoException {
        super.undo();
        reconstruction.associate(gene, product);
    }

    @Override public void redo() throws CannotRedoException {
        super.redo();
        reconstruction.dissociate(gene, product);
    }
}
