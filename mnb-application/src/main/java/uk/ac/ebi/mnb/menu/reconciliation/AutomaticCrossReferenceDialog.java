
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
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
import uk.ac.ebi.metabolomes.webservices.KeggCompoundWebServiceConnection;
import uk.ac.ebi.mnb.view.AltPanel;
import uk.ac.ebi.mnb.view.CheckBox;
import uk.ac.ebi.mnb.view.DialogController;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.mnb.view.labels.Label;


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


    public AutomaticCrossReferenceDialog(JFrame frame, DialogController controller) {

        super(frame, controller, "AutomaticCrossReference");

        chebiCheckBox = new CheckBox("ChEBI (currated only)");
        keggCheckBox = new CheckBox("KEGG Compound");
        chebiAllStarCheckBox = new CheckBox("ChEBI (a0ll)");

        layoutOptions();
    }


    private void layoutOptions() {
        setLayout(new FormLayout("10dlu, pref, 10dlu",
                                 "10dlu, pref, 2dlu, pref, 2dlu, pref, 4dlu, pref, 10dlu"));

        CellConstraints cc = new CellConstraints();

        // options
        JComponent selection = new AltPanel();

        selection.setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p"));
        selection.add(chebiCheckBox, cc.xy(1, 1));
        selection.add(chebiAllStarCheckBox, cc.xy(3, 1));
        selection.add(keggCheckBox, cc.xy(1, 3));

        // close and run buttons
        JComponent component = new AltPanel();
        component.setLayout(new FormLayout("p:grow, right:min,4dlu ,right:min", "p"));
        component.add(getCloseButton(), cc.xy(2, 1));
        component.add(getRunButton(), cc.xy(4, 1));

        add(new Label("Match name(s) to chemical databases"), cc.xy(2, 2));
        add(new JSeparator(JSeparator.HORIZONTAL), cc.xy(2, 4));
        add(selection, cc.xy(2, 6));
        add(component, cc.xy(2, 8));

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

