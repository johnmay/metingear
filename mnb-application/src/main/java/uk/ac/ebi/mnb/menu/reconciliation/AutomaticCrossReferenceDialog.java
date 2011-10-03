/**
 * AutomaticCrossReferenceDialog.java
 *
 * 2011.09.30
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
package uk.ac.ebi.mnb.menu.reconciliation;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
import uk.ac.ebi.metabolomes.webservices.KeggCompoundWebServiceConnection;
import uk.ac.ebi.mnb.view.DialogPanel;
import uk.ac.ebi.mnb.view.CheckBox;
import uk.ac.ebi.mnb.view.DialogController;
import uk.ac.ebi.mnb.view.DropdownDialog;

/**
 *          AutomaticCrossReferenceDialog – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AutomaticCrossReferenceDialog
        extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(AutomaticCrossReferenceDialog.class);
    private List<AnnotatedEntity> components;
    private ChEBIWebServiceConnection chebi;
    private KeggCompoundWebServiceConnection kegg;
    private JCheckBox chebiCheckBox;
    private JCheckBox keggCheckBox;
    private JCheckBox chebiAllStarCheckBox;

    public AutomaticCrossReferenceDialog(JFrame frame) {

        super(frame, "RunDialog");

        chebiCheckBox = new CheckBox("ChEBI (currated)");
        keggCheckBox = new CheckBox("KEGG Compound");
        chebiAllStarCheckBox = new CheckBox("ChEBI (all)");
        chebiAllStarCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                chebiCheckBox.setSelected(chebiAllStarCheckBox.isSelected());
            }
        });

        setDefaultLayout();

    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Match name(s) to chemical databases");
        return label;
    }

    @Override
    public JPanel getOptions() {

        JPanel options = new DialogPanel();

        CellConstraints cc = new CellConstraints();

        options.setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p"));
        options.add(chebiCheckBox, cc.xy(1, 1));
        options.add(chebiAllStarCheckBox, cc.xy(3, 1));
        options.add(keggCheckBox, cc.xy(1, 3));

        return options;
    }

    @Override
    public void process() {
        // seach names
    }

    @Override
    public void update() {
        // update
    }
}
