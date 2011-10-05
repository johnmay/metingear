/**
 * NameEdit.java
 *
 * 2011.10.02
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.edit;

import javax.swing.undo.*;
import uk.ac.ebi.core.AnnotatedEntity;

/**
 * @name   NameEdit - 2011.10.02 <br>
 *          Allows undo/redo on name changing of an {@see AnnotatedEntity}
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AbbreviationEdit extends AbstractUndoableEdit {

    private AnnotatedEntity entity;
    private String oldName;
    private String newName;

    public AbbreviationEdit(AnnotatedEntity entity, String newName) {
        this.entity = entity;
        this.newName = newName;
        this.oldName = entity.getAbbreviation(); // todo use interface MetabolicEntity
    }

    @Override
    public void undo() throws CannotUndoException {
        entity.setAbbreviation(oldName);
    }

    @Override
    public void redo() throws CannotRedoException {
        entity.setAbbreviation(newName);
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public String getPresentationName() {
        return "Set name ";
    }
}
