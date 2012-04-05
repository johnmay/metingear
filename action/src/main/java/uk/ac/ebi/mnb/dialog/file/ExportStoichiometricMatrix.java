/**
 * ExportReactionMatrix.java
 *
 * 2012.01.11
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.dialog.file;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.io.model.ReactionMatrixIO;
import uk.ac.ebi.metabolomes.core.reaction.matrix.StoichiometricMatrix;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;


/**
 *
 * ExportReactionMatrix 2012.01.11
 *
 * @version $Rev$ : Last Changed $Date$
 * @author johnmay
 * @author $Author$ (this version)
 *
 * Class description
 *
 */
public class ExportStoichiometricMatrix extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(ExportStoichiometricMatrix.class);

    private JComboBox storage = ComboBoxFactory.newComboBox("String", "Object");

    private JComboBox format = ComboBoxFactory.newComboBox("Table (tsv)", "Serialised");

    private JCheckBox useDouble = CheckBoxFactory.newCheckBox("Store values as double precission");

    private JFileChooser chooser = new JFileChooser();


    public ExportStoichiometricMatrix(JFrame frame,
                                      TargetedUpdate updater,
                                      ReportManager messages,
                                      SelectionController controller,
                                      UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "SaveDialog");

        storage.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (storage.getSelectedItem().equals("Object")) {
                    format.setSelectedItem("Serialized");
                }
            }
        });

        super.setDefaultLayout();

    }


    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Export the active reaction matrix");
        return label;
    }


    @Override
    public JPanel getForm() {

        JPanel panel = super.getForm();

        CellConstraints cc = new CellConstraints();

        panel.setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p, 4dlu, p"));

        panel.add(LabelFactory.newFormLabel("Storage"), cc.xy(1, 1));
        panel.add(storage, cc.xy(3, 1));

        panel.add(LabelFactory.newFormLabel("Format"), cc.xy(1, 3));
        panel.add(format, cc.xy(3, 3));

        panel.add(useDouble, cc.xyw(1, 5, 3));

        return panel;

    }


    @Override
    public void process() {

        ReactionMatrixIO.setConvertDoubleToInChI(!useDouble.isSelected());
        Object fmt = format.getSelectedItem();

        int choice = chooser.showSaveDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {

            File f = chooser.getSelectedFile();
            Reconstruction recon = ReconstructionManager.getInstance().getActive();
            StoichiometricMatrix s = recon.getMatrix();

            try {
                if (fmt.equals("Table (tsv)")) {
                    ReactionMatrixIO.writeBasicStoichiometricMatrix(s, new FileWriter(f));
                } else if (fmt.equals("Serialised")) {
                    ReactionMatrixIO.writeCompressedBasicStoichiometricMatrix(s, new FileOutputStream(f));
                }
            } catch (Exception ex) {
                addMessage(new ErrorMessage("Unable to save file: " + ex.getMessage()));
            }

        }

    }
}
