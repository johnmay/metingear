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
import java.security.InvalidParameterException;
import java.util.prefs.Preferences;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.io.external.HomologySearchFactory;
import uk.ac.ebi.chemet.render.factory.LabelFactory;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.MComboBox;
import uk.ac.ebi.mnb.interfaces.DialogController;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.settings.SourceItemDisplayType;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.chemet.render.factory.PanelFactory;
import uk.ac.ebi.chemet.render.factory.FieldFactory;

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
    private JTextField blastp;

    public PreferenceDialog(JFrame frame, DialogController controller) {
        super(frame, controller, "SaveDialog");
        setDefaultLayout();
    }

    @Override
    public JPanel getOptions() {

        JPanel options = PanelFactory.createDialogPanel("p, 4dlu, p, 4dlu, p, 4dlu, p", "p, 4dlu, p");
        CellConstraints cc = new CellConstraints();



        metSourceView = new MComboBox(SourceItemDisplayType.values());
        metSourceView.setSelectedItem(Settings.getInstance().getDisplayType(Settings.VIEW_SOURCE_METABOLITE));
        rxnSourceView = new MComboBox(SourceItemDisplayType.values());
        rxnSourceView.setSelectedItem(Settings.getInstance().getDisplayType(Settings.VIEW_SOURCE_REACTION));
        blastp = FieldFactory.newField(15);
        blastp.setText(Preferences.userNodeForPackage(HomologySearchFactory.class).get("blastp.path", ""));

        options.add(LabelFactory.newFormLabel("Metabolite source view:"), cc.xy(1, 1));
        options.add(metSourceView, cc.xy(3, 1));
        options.add(LabelFactory.newFormLabel("Reaction source view:"), cc.xy(5, 1));
        options.add(rxnSourceView, cc.xy(7, 1));
        options.add(LabelFactory.newFormLabel("blastp path (2.2.25+ recomended):"), cc.xy(1, 3));
        options.add(blastp, cc.xy(3, 3));



        return options;

    }

    @Override
    public void process() {
        Settings settings = Settings.getInstance();
        settings.put(Settings.VIEW_SOURCE_METABOLITE, (SourceItemDisplayType) metSourceView.getSelectedItem());
        settings.put(Settings.VIEW_SOURCE_REACTION, (SourceItemDisplayType) rxnSourceView.getSelectedItem());

        //TODO delgate this to a settings class
        try {
            settings.setBlastPreferences(blastp.getText());
        } catch (InvalidParameterException ex) {
            MainView.getInstance().addWarningMessage(ex.getMessage());
        }

    }

    @Override
    public boolean update() {
        return MainView.getInstance().getSourceListController().update();
    }
}
