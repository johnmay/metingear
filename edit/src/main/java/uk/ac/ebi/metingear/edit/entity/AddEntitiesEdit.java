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

package uk.ac.ebi.metingear.edit.entity;


import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;

/**
 * @author John May
 */
public class AddEntitiesEdit extends AbstractUndoableEdit {

    private Reconstruction reconstruction;

    private Collection<Metabolite>        metabolites;
    private Collection<MetabolicReaction> reactions;
    private Collection<GeneProduct>       products;
    private Collection<Gene>              genes;

    public AddEntitiesEdit(Reconstruction   reconstruction,
                           EntityCollection entities) {

        this.reconstruction = reconstruction;

        this.metabolites = new ArrayList<Metabolite>(entities.get(Metabolite.class));
        this.reactions   = new ArrayList<MetabolicReaction>(entities.get(MetabolicReaction.class));
        this.products    = new ArrayList<GeneProduct>(entities.get(GeneProduct.class));

        if(entities.hasSelection(Gene.class)){
            System.err.println("Gene undo/redo not yet supported");
        }

    }

    @Override
    public void undo() throws CannotUndoException {
        reconstruction.getMetabolome().removeAll(metabolites);
        reconstruction.getReactome().removeAll(reactions);
        for(GeneProduct product : products){
            reconstruction.remove(product);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        reconstruction.getMetabolome().addAll(metabolites);
        reconstruction.getReactome().addAll(reactions);
        for(GeneProduct product : products){
            reconstruction.addProduct(product);
        }
    }
}
