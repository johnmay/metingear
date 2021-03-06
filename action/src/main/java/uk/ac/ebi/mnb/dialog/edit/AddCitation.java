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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.caf.component.factory.FieldFactory;

/**
 * @name    AddCitation - 2011.10.07 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AddCitation extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(AddCitation.class);
    private JTextField field = FieldFactory.newField(25);

    public AddCitation(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "SaveDialog");
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Add a citation to one or more reconstruction entities");
        return label;
    }

    @Override
    public JPanel getForm() {
        JPanel options = super.getForm();
        options.add(field);
        //new Pub
        return options;
    }


    @Override
    public void process() {

    }

    

    

}
