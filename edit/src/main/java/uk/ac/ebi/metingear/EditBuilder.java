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

package uk.ac.ebi.metingear;

import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.metingear.association.GeneProductToReactionAssociation;
import uk.ac.ebi.metingear.association.GeneProductToReactionDissociation;
import uk.ac.ebi.metingear.association.GeneToGeneProductAssociation;
import uk.ac.ebi.metingear.association.GeneToGeneProductDissociation;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper for building undoable edits.
 *
 * @author John May
 */
public final class EditBuilder {

    private final Reconstruction reconstruction;
    private final List<AppliableEdit> edits = new ArrayList<AppliableEdit>();
    private final EditBuilder self = this;

    public EditBuilder(Reconstruction reconstruction) {
        this.reconstruction = reconstruction;
    }

    public WithBuilder<GeneProduct> associate(final Gene gene) {
        return new WithBuilder<GeneProduct>() {
            @Override public EditBuilder with(GeneProduct argument) {
                edits.add(new GeneToGeneProductAssociation(reconstruction, gene, argument));
                return self;
            }
        };
    }

    public WithBuilder<GeneProduct> dissociate(final Gene gene) {
        return new WithBuilder<GeneProduct>() {
            @Override public EditBuilder with(GeneProduct argument) {
                edits.add(new GeneToGeneProductDissociation(reconstruction, gene, argument));
                return self;
            }
        };
    }

    public WithBuilder<MetabolicReaction> associate(final GeneProduct product) {
        return new WithBuilder<MetabolicReaction>() {
            @Override public EditBuilder with(MetabolicReaction argument) {
                edits.add(new GeneProductToReactionAssociation(reconstruction, product, argument));
                return self;
            }
        };
    }

    public WithBuilder<MetabolicReaction> dissociate(final GeneProduct product) {
        return new WithBuilder<MetabolicReaction>() {
            @Override public EditBuilder with(MetabolicReaction argument) {
                edits.add(new GeneProductToReactionDissociation(reconstruction, product, argument));
                return self;
            }
        };
    }

    public UndoableEdit apply() {
        CompoundEdit compound = new CompoundEdit();
        for (AppliableEdit edit : edits) {
            edit.apply();
            compound.addEdit(edit);
        }
        compound.end();
        return compound;
    }

    public static interface WithBuilder<E> {
        public EditBuilder with(E argument);
    }
}
