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

import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.observation.Observation;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * An undoable edit for renaming a metabolite.
 *
 * @author John May
 */
public final class RenameMetaboliteEdit extends AbstractUndoableEdit {

    private final ReplaceMetaboliteEdit delegate;

    public RenameMetaboliteEdit(Metabolite metabolite, String newName,
                                Reconstruction reconstruction) {
        this.delegate = new ReplaceMetaboliteEdit(metabolite, create(metabolite, newName), reconstruction);
    }

    public Metabolite create(Metabolite original, String name) {
        Metabolite m = (Metabolite) original.newInstance();
        m.setIdentifier(original.getIdentifier());
        m.setAbbreviation(original.getAbbreviation());
        m.setName(name);
        m.setRating(original.getRating());
        m.addAnnotations(original.getAnnotations());
        for (Class<? extends Observation> c : m.getObservationClasses()) {
            for (Observation observation : original.getObservations(c)) {
                m.addObservation(observation);
            }
        }
        return m;
    }

    public void apply() {
        delegate.apply();
    }

    @Override public void redo() throws CannotRedoException {
        super.redo();
        delegate.redo();
    }

    @Override public void undo() throws CannotUndoException {
        super.undo();
        delegate.undo();
    }
}
