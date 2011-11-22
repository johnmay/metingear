/**
 * RemoveAnnotations.java
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
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.Annotation;
import uk.ac.ebi.mnb.interfaces.UndoableEntityEdit;

/**
 * @name    RemoveAnnotations - 2011.10.02 <br>
 *          Keeps track of a annotation removal
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AnnotationRemoval
        extends UndoableEntityEdit {

    private static final Logger LOGGER = Logger.getLogger(AnnotationRemoval.class);
    private AnnotatedEntity entity;
    private Collection<Annotation> annotations;

    public AnnotationRemoval(AnnotatedEntity entity, Collection<Annotation> annotations) {
        this.entity = entity;
        this.annotations = annotations;
    }

    public AnnotationRemoval(AnnotatedEntity entity, Annotation... annotations) {
        this.entity = entity;
        this.annotations = Arrays.asList(annotations);
    }

    @Override
    public void redo() throws CannotRedoException {
        for (Annotation annotation : annotations) {
            entity.removeAnnotation(annotation);
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        for (Annotation annotation : annotations) {
            entity.addAnnotation(annotation);
        }
    }

    @Override
    public String getPresentationName() {
        return "Remove annotations";
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
    public Collection<AnnotatedEntity> getEntities() {
        return Arrays.asList(entity);
    }
}
