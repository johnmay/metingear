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
package uk.ac.ebi.mnb.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.metingear.AppliableEdit;
import uk.ac.ebi.mnb.interfaces.UndoableEntityEdit;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 * @name RemoveAnnotations - 2011.10.02 <br> Keeps track of a annotation
 * removal
 */
public class RemoveAnnotationEdit
        extends UndoableEntityEdit implements AppliableEdit {

    private static final Logger LOGGER = Logger
            .getLogger(RemoveAnnotationEdit.class);
    private AnnotatedEntity entity;
    private Collection<? extends Annotation> annotations;

    public RemoveAnnotationEdit(AnnotatedEntity entity, Collection<? extends Annotation> annotations) {
        this.entity = entity;
        this.annotations = new ArrayList<Annotation>(annotations);
    }

    public RemoveAnnotationEdit(AnnotatedEntity entity, Annotation... annotations) {
        this.entity = entity;
        this.annotations = Arrays.asList(annotations);
    }

    @Override public void apply() {
        for (Annotation annotation : annotations) {
            entity.removeAnnotation(annotation);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        for (Annotation annotation : annotations) {
            entity.removeAnnotation(annotation);
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        for (Annotation annotation : annotations) {
            entity.addAnnotation(annotation);
        }
    }

    @Override
    public String getPresentationName() {
        return "Remove annotations";
    }

    @Override
    public Collection<AnnotatedEntity> getEntities() {
        return Arrays.asList(entity);
    }
}
