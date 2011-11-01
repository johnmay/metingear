/**
 * ImportPanel.java
 *
 * 2011.09.26
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
package uk.ac.ebi.mnb.importer.xls.wizzard;

import com.jgoodies.forms.layout.CellConstraints;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import mnb.io.tabular.ExcelModelProperties;
import uk.ac.ebi.visualisation.ViewUtils;
import uk.ac.ebi.mnb.view.labels.MLabel;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.view.DialogPanel;

/**
 *          ImportPanel – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AdditionalOptions
        extends DialogPanel
        implements WizzardStage {

    private static final Logger LOGGER = Logger.getLogger(AdditionalOptions.class);
    private ExcelModelProperties properties;
    private JCheckBox chebi = new JCheckBox();
    private JCheckBox kegg = new JCheckBox();

    public AdditionalOptions(ExcelModelProperties properties) {
        this.properties = properties;
    }

    private void init() {
        setLayout(ViewUtils.formLayoutHelper(2, 5, 4, 4));
        CellConstraints cc = new CellConstraints();
        add(new JLabel("Please select web services to reconcile chemical names"), cc.xy(1, 1));
        add(new MLabel("ChEBI (recomended)"), cc.xy(1, 3));
        add(chebi, cc.xy(3, 3));
        add(new MLabel("KEGG"), cc.xy(1, 5));
        add(kegg, cc.xy(3, 5));
    }

    public Boolean updateSelection() {

        properties.put("import.ws.chebi", Boolean.toString(chebi.isSelected()));
        properties.put("import.ws.kegg", Boolean.toString(kegg.isSelected()));


        return true;
    }

    public void reloadPanel() {
        init();
    }
    private JProgressBar bar = new JProgressBar();

    public JProgressBar getProgressBar() {
        return bar;
    }

    public String getDescription() {
        return "<html>Please confirm additional options</html>";
    }
}
