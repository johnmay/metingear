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

import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Collection;

/**
 * @author John May
 */
public class RemoveGeneProduct extends AbstractUndoableEdit {

    private final GeneProduct product;
    private final Reconstruction reconstruction;
    private final Collection<Gene> genes;

    public RemoveGeneProduct(Reconstruction reconstruction,
                             GeneProduct product) {
        this.reconstruction = reconstruction;
        this.product = product;
        this.genes = product.getGenes();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        // add gene references
        for (Gene gene : this.genes) {
            gene.removeProduct(product);
            product.remove(gene);
        }
        reconstruction.getProteome().add(product);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        // remove gene references
        for (Gene gene : this.genes) {
            gene.removeProduct(product);
            this.product.remove(gene);
        }
        reconstruction.getProteome().remove(product);
    }

}
