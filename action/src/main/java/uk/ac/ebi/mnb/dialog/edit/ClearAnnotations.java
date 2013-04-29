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

package uk.ac.ebi.mnb.dialog.edit;

import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.edit.RemoveAnnotationEdit;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.SwingUtilities;
import javax.swing.undo.CompoundEdit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

/** @author John May */
public class ClearAnnotations extends ControllerAction {

    public ClearAnnotations(MainController controller) {
        super("ClearAnnotations", controller);
    }

    @Override public void actionPerformed(ActionEvent e) {
        CompoundEdit edit = new CompoundEdit();
        for (AnnotatedEntity entity : getSelection().getEntities()) {
            Collection<Annotation> annotations = new ArrayList<Annotation>(entity.getAnnotations());
            edit.addEdit(new RemoveAnnotationEdit(entity, annotations));
            for (Annotation annotation : annotations) {
                entity.removeAnnotation(annotation);
            }
        }
        edit.end();
        getController().getUndoManager().addEdit(edit);
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                update();
            }
        });
    }
}
