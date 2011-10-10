/**
 * SequenceComparisson.java
 *
 * 2011.10.07
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
package uk.ac.ebi.mnb.dialog.tools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.io.external.HomologySearchFactory;
import uk.ac.ebi.chemet.io.external.RunnableTask;
import uk.ac.ebi.core.HomologyDatabaseManager;
import uk.ac.ebi.interfaces.GeneProduct;
import uk.ac.ebi.metabonater.components.theme.MTextField;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.MLabels;
import uk.ac.ebi.mnb.core.TaskManager;
import uk.ac.ebi.mnb.core.Utilities;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.Updatable;
import uk.ac.ebi.mnb.view.CheckBox;
import uk.ac.ebi.mnb.view.MComboBox;

/**
 * @name    SequenceComparisson - 2011.10.07 <br>
 *          Dialog to build sequence analysis tasks
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class SequenceHomology extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(SequenceHomology.class);
    private CellConstraints cc = new CellConstraints();
    // options
    private CheckBox remote = new CheckBox("Remote (webservices)");
    private MComboBox tool = new MComboBox(Arrays.asList("BLAST", "FASTA"));
    private MComboBox database = new MComboBox(HomologyDatabaseManager.getInstance().getNames());
    private JSpinner cpu = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
    private JSpinner results = new JSpinner(new SpinnerNumberModel(50, 10, 2500, 50));
    private JTextField field = new MTextField("1e-30");

    public SequenceHomology(JFrame frame, Updatable updater, MessageManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "BuildDialog");

        remote.setEnabled(false);
        setDefaultLayout();
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Please select options for sequence homology searching");
        return label;
    }

    @Override
    public JPanel getOptions() {
        JPanel panel = super.getOptions();

        panel.setLayout(new FormLayout("p, 4dlu, p, 4dlu, p, 4dlu, p", "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"));

        panel.add(MLabels.newFormLabel("Program:"), cc.xy(1, 1));
        panel.add(tool, cc.xy(3, 1));
        panel.add(remote, cc.xyw(5, 1, 3));

        panel.add(new JSeparator(), cc.xyw(1, 3, 7));

        panel.add(MLabels.newFormLabel("CPUs:",
                                       "The number of CPUs to use. Higher numbers improve speed but may slow down the system"),
                  cc.xy(1, 5));
        panel.add(cpu, cc.xy(3, 5));
        panel.add(MLabels.newFormLabel("Expected Value:",
                                       "Lower numbers will improve performance but may decrease the number of returned results"),
                  cc.xy(5, 5));
        panel.add(field, cc.xy(7, 5));

        panel.add(MLabels.newFormLabel("Database:"), cc.xy(1, 7));
        panel.add(database, cc.xy(3, 7));

        panel.add(MLabels.newFormLabel("Max Results"), cc.xy(5, 7));
        panel.add(results, cc.xy(7, 7));


        return panel;

    }

    @Override
    public void process() {
        HomologySearchFactory factory = HomologySearchFactory.getInstance();

        Collection<GeneProduct> products = Utilities.getGeneProducts(getSelection());
        File db = HomologyDatabaseManager.getInstance().getPath((String) database.getSelectedItem());

        try {
            RunnableTask task = factory.getTabularBLASTP(products, db, 1e-30, (Integer) cpu.getValue(), (Integer) results.getValue());
            TaskManager.getInstance().add(task);
        } catch (IOException ex) {
            addMessage(new ErrorMessage("Unable to perform sequence homology search: " + ex.getMessage()));
        } catch (Exception ex) {
            addMessage(new ErrorMessage("Unable to perform sequence homology search: " + ex.getMessage()));
        }
    }
}
