/**
 * Preferences.java
 *
 * 2011.10.02
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
package uk.ac.ebi.mnb.menu.file;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.view.DialogPanel;
import uk.ac.ebi.mnb.view.ComboBox;
import uk.ac.ebi.mnb.view.DialogController;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.labels.BoldLabel;

/**
 *          Preferences - 2011.10.02 <br>
 *          The preferences pane for MNB. The pane loads the ApplicationPreferences
 *          Singleton and allows editing of the attributes
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class Preferences extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(Preferences.class);
    private JPanel options;

    public Preferences(JFrame frame, DialogController controller) {
        super(frame, controller, "SaveDialog");
        setDefaultLayout();
    }

    @Override
    public JPanel getOptions() {

        JPanel options = new DialogPanel(new FormLayout("p, 4dlu, p, 4dlu, p, 4dlu, p", "p"));
        CellConstraints cc = new CellConstraints();

        JComboBox metaboliteSourceSelector = new ComboBox("Name", "Accession", "Abbreviation");
        JComboBox reactionSourceSelector = new ComboBox("Name", "Accession", "Abbreviation");

        options.add(new BoldLabel("Metabolites"), cc.xy(1, 1));
        options.add(metaboliteSourceSelector, cc.xy(3, 1));
        options.add(new BoldLabel("Reactions"), cc.xy(5, 1));
        options.add(reactionSourceSelector, cc.xy(7, 1));

        return options;

    }

    @Override
    public void process() {
        //        ApplicationPreferences preferences = ApplicationPreferences.getInstance();
        //        preferences.get(preferences.VIEW_SOURCE_METABOLITE);
    }

    @Override
    public void update() {
        MainFrame.getInstance().getSourceListController().update();
    }
}
