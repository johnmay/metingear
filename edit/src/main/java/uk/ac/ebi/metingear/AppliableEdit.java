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

import javax.swing.undo.UndoableEdit;

/**
 * An appliable edit is an undoable edit which also defines an {@link #apply()}
 * method to actually perform the edit. This is useful when the edit requires
 * several non-trivial steps in the correct order.
 *
 * @author John May
 */
public interface AppliableEdit extends UndoableEdit {

    /**
     * Apply the edit
     */
    public void apply();

}
