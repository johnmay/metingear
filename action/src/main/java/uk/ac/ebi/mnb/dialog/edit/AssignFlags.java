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

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.undo.CompoundEdit;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * Action to automatically add annotation flags to selected entities.
 *
 * @author johnmay
 */
public class AssignFlags extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(AssignFlags.class);

    public AssignFlags(String command, MainController controller) {
        super(command, controller);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        DefaultAnnotationFactory factory = DefaultAnnotationFactory
                .getInstance();

        CompoundEdit edit = new CompoundEdit();

        Collection<AnnotatedEntity> entities = getSelection().getEntities();
        for (AnnotatedEntity entity : entities) {
            for (Annotation flag : factory.getMatchingFlags(entity)) {
                edit.addEdit(AddAnnotationEdit.edit(entity, flag));
            }
        }

        edit.end();
        update();
    }

}
