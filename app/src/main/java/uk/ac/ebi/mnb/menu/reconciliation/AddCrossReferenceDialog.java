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
package uk.ac.ebi.mnb.menu.reconciliation;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.ui.edit.crossreference.IdentifierEditor;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.util.HashMap;
import java.util.Map;

/**
 *          AddCrossReferenceDialog – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AddCrossReferenceDialog
        extends ControllerDialog
        {

    private static final Logger LOGGER = Logger.getLogger(AddCrossReferenceDialog.class);
    private static final Map<String, Byte> nameIndexMap = new HashMap();
    private AnnotatedEntity entity = null;
    private IdentifierEditor editor = new IdentifierEditor();

    public AddCrossReferenceDialog(JFrame frame,
                                   TargetedUpdate updater,
                                   ReportManager messages,
                                   SelectionController controller,
                                   UndoableEditListener undo) {

        super(frame, updater, messages, controller, undo, "AddCrossReference");


        setDefaultLayout();
    }

    public void setComponent(AnnotatedEntity reconComponent) {
        this.entity = reconComponent;
    }

    @Override
    public JPanel getForm() {

        JPanel panel = super.getForm();

        panel.add(editor);

        return panel;

    }

    @Override
    public void process() {
        entity.addAnnotation(DefaultAnnotationFactory.getInstance().getCrossReference(editor.getIdentifier()));
    }

    @Override
    public boolean update() {
        return MainView.getInstance().getViewController().update();
    }
}
