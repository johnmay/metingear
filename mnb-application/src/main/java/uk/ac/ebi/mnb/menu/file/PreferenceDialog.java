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
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.view.DialogPanel;
import uk.ac.ebi.mnb.view.ComboBox;
import uk.ac.ebi.mnb.interfaces.DialogController;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.settings.SourceItemDisplayType;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.mnb.view.labels.Label;

/**
 *          Preferences - 2011.10.02 <br>
 *          The preferences pane for MNB. The pane loads the ApplicationPreferences
 *          Singleton and allows editing of the attributes
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class PreferenceDialog extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(PreferenceDialog.class);
    private JComboBox metSourceView;
    private JComboBox rxnSourceView;

    public PreferenceDialog(JFrame frame, DialogController controller) {
        super(frame, controller, "SaveDialog");
        setDefaultLayout();
    }

    @Override
    public JPanel getOptions() {

        JPanel options = new DialogPanel(new FormLayout("p, 4dlu, p, 4dlu, p, 4dlu, p", "p"));
        CellConstraints cc = new CellConstraints();

        metSourceView = new ComboBox(SourceItemDisplayType.values());
        metSourceView.setSelectedItem(Settings.getInstance().getDisplayType(Settings.VIEW_SOURCE_METABOLITE));
        rxnSourceView = new ComboBox(SourceItemDisplayType.values());
        rxnSourceView.setSelectedItem(Settings.getInstance().getDisplayType(Settings.VIEW_SOURCE_REACTION));


        options.add(new Label("Metabolites: ", SwingConstants.RIGHT), cc.xy(1, 1));
        options.add(metSourceView, cc.xy(3, 1));
        options.add(new Label("Reactions: ", SwingConstants.RIGHT), cc.xy(5, 1));
        options.add(rxnSourceView, cc.xy(7, 1));

        return options;

    }

    @Override
    public void process() {
        Settings settings = Settings.getInstance();
        settings.put(Settings.VIEW_SOURCE_METABOLITE, (SourceItemDisplayType) metSourceView.getSelectedItem());
        settings.put(Settings.VIEW_SOURCE_REACTION, (SourceItemDisplayType) rxnSourceView.getSelectedItem());
    }

    @Override
    public boolean update() {
        return MainFrame.getInstance().getSourceListController().update();
    }
}
