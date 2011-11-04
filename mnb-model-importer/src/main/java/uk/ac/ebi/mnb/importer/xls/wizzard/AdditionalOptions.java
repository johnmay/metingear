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
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import mnb.io.tabular.ExcelModelProperties;
import uk.ac.ebi.mnb.view.labels.MLabel;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.view.MCheckBox;

/**
 *          ImportPanel – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AdditionalOptions
        extends JPanel
        implements WizzardStage {

    private static final Logger LOGGER = Logger.getLogger(AdditionalOptions.class);
    private ExcelModelProperties properties;
    private JCheckBox chebi = new MCheckBox("chebi");
    private JCheckBox kegg = new MCheckBox("kegg");
    private JCheckBox skip = new MCheckBox("Skip Manual Assignment (can be completed manual)");

    public AdditionalOptions(ExcelModelProperties properties) {
        this.properties = properties;
    }



    private void init() {
        setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p, 4dlu, p"));
        CellConstraints cc = new CellConstraints();

        add(new MLabel("<html>Please select which resources should be<br>"
                       + "used to reconcile chemical names</html>"), cc.xy(1, 1));

        add(chebi, cc.xy(1, 5));
        add(kegg, cc.xy(3, 5));

        add(skip, cc.xyw(1, 3, 3));

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
