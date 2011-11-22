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

import java.util.Arrays;
import java.util.Collection;
import javax.swing.undo.*;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.UndoableEntityEdit;

/**
 * @name   NameEdit - 2011.10.02 <br>
 *          Allows undo/redo on abbreviation changing of an {@see AnnotatedEntity}
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AccessionEdit extends UndoableEntityEdit {

    private AnnotatedEntity entity;
    private String oldAccession;
    private String newAccession;

    public AccessionEdit(AnnotatedEntity entity, String newAccession) {
        this.entity = entity;
        this.newAccession = newAccession;
        this.oldAccession = entity.getAccession(); // todo use interface MetabolicEntity
    }

    @Override
    public void undo() throws CannotUndoException {
        entity.getIdentifier().setAccession(oldAccession);
    }

    @Override
    public void redo() throws CannotRedoException {
        entity.getIdentifier().setAccession(newAccession);
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
    }   @Override
    public Collection<AnnotatedEntity> getEntities() {
        return Arrays.asList(entity);
    }
}
