/**
 * AddAnnotation.java
 *
 * 2012.02.14
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
package uk.ac.ebi.mnb.dialog.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.ExpandingComponentList;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.ui.edit.annotation.AnnotationChoiceEditor;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.awt.*;


/**
 *
 *          AddAnnotation 2012.02.14
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *
 *          Class description
 *
 */
public class AddAnnotation
        extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(AddAnnotation.class);

    private ExpandingComponentList<AnnotationChoiceEditor> list;


    public AddAnnotation(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "OkayDialog");
        final Window window = this;
        list = new ExpandingComponentList<AnnotationChoiceEditor>(window) {

            @Override
            public AnnotationChoiceEditor newComponent() {
                return new AnnotationChoiceEditor(window);
            }
        };
        setDefaultLayout();
        list.setBackground(getBackground());
    }


    @Override
    public JPanel getForm() {
        JPanel panel = super.getForm();
        panel.add(list.getComponent());
        return panel;
    }


    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setup();
        }
        super.setVisible(visible);
    }


    private void setup() {

        // ensure size of 1
        list.reset();

        // set the context of the annotation editor
        AnnotatedEntity entity = getSelection().getFirstEntity();
        list.getComponent(0).setContext(entity.getClass());

    }


    /**
     * Transfer the new annotations to the
     * selected entities
     */
    @Override
    public void process() {

        for (int i = 0; i < list.getSize(); i++) {
            AnnotationChoiceEditor chooser = list.getComponent(i);

            Annotation annotation = chooser.getEditor().newAnnotation();
            
            System.out.println(annotation);

            for (AnnotatedEntity entity : getSelection().getEntities()) {
                entity.addAnnotation(annotation);
            }

        }

    }
}
