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

package uk.ac.ebi.mnb.dialog.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.chemet.render.source.EntitySubset;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;


/**
 *
 *          CreateSubset 2012.01.30
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *
 *          Creates a subset based on the current collection
 *
 */
public class CreateSubset extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(CreateSubset.class);

    private JTextField field = FieldFactory.newField(30);


    public CreateSubset(JFrame frame,
                        TargetedUpdate updater,
                        ReportManager messages,
                        SelectionController controller,
                        UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "OkayDialog");
        setDefaultLayout();
    }


    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Create a new subset from the current selection");
        return label;
    }


    @Override
    public void setVisible(boolean visible) {
        field.setText("New collection");
        super.setVisible(visible);
    }


    @Override
    public JPanel getForm() {
        JPanel panel = super.getForm();

        panel.add(field);

        return panel;
    }


    @Override
    public void process() {



        Reconstruction recon = DefaultReconstructionManager.getInstance().active();
        EntitySubset subset = new EntitySubset(field.getText(), null);

        for (AnnotatedEntity entity : getSelection().getEntities()) {
            subset.add(entity);
        }

        recon.addSubset(subset);

        LOGGER.debug("Created new subset: " + field.getText());

    }
}
