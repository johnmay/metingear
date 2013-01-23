/**
 * DeleteAnnotation.java
 *
 * 2011.10.04
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

import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.Updatable;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.event.ActionEvent;

/**
 * @name    DeleteAnnotation - 2011.10.04 <br>
 *          Action class to remove a given annotation
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class DeleteAnnotation extends AbstractAction {

    private final UndoableEditListener editListener;
    private final Updatable updatable;
    private final AnnotatedEntity entity;
    private final Annotation annotation;

    public DeleteAnnotation(AnnotatedEntity entity,
                            Annotation annotation,
                            Updatable updatable,
                            UndoableEditListener editListener) {
        this.editListener = editListener;
        this.updatable = updatable;
        this.entity = entity;
        this.annotation = annotation;
        putValue(SHORT_DESCRIPTION, "Removes selected annotation");
        putValue(LARGE_ICON_KEY, ResourceUtility.getIcon(getClass(), "close_16x16.png"));
    }

    public void actionPerformed(ActionEvent ae) {
        editListener.undoableEditHappened(new UndoableEditEvent(ae, new RemoveAnnotationEdit(entity, annotation)));
        entity.removeAnnotation(annotation);
        updatable.update();
    }
}
