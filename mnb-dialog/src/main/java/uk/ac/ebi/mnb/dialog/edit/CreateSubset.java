/**
 * CreateSubset.java
 *
 * 2012.01.30
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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.chemet.render.source.EntitySubset;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;


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
    public JPanel getOptions() {
        JPanel panel = super.getOptions();

        panel.add(field);

        return panel;
    }


    @Override
    public void process() {



        Reconstruction recon = ReconstructionManager.getInstance().getActive();
        EntitySubset subset = new EntitySubset(field.getText(), null);

        for (AnnotatedEntity entity : getSelection().getEntities()) {
            subset.add(entity);
        }

        recon.addSubset(subset);

        LOGGER.debug("Created new subset: " + field.getText());

    }
}
