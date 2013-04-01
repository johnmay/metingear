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

package uk.ac.ebi.mnb.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.UndoableEntityEdit;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides undoable edit support for replacing a single annotation with
 * another.
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class ReplaceAnnotationEdit extends UndoableEntityEdit {

    private static final Logger LOGGER = Logger.getLogger(ReplaceAnnotationEdit.class);

    private AnnotatedEntity entity;
    private Annotation original;
    private Annotation replacement;
    
    public ReplaceAnnotationEdit(AnnotatedEntity entity,
                                 Annotation original,
                                 Annotation replacement){
        this.entity      = entity;
        this.original    = original;
        this.replacement = replacement;
    }

    /**
     * Removes original annotation and adds the replacement
     * @throws CannotRedoException
     */
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        entity.removeAnnotation(original);
        entity.addAnnotation(replacement);
    }

    /**
     * Removes the replacement and adds the orginal
     * @throws CannotUndoException
     */
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        entity.removeAnnotation(replacement);
        entity.addAnnotation(original);
    }

    @Override
    public Collection<AnnotatedEntity> getEntities() {
        return Arrays.asList(entity);
    }
}
