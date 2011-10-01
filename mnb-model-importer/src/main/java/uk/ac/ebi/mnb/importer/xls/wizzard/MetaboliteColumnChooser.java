
/**
 * MetaboliteColumnChooser.java
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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.StyledEditorKit.ForegroundAction;
import mnb.io.tabular.ExcelModelProperties;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.ViewUtils;
import org.apache.log4j.Logger;


/**
 *          MetaboliteColumnChooser – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetaboliteColumnChooser
  extends GeneralPanel
  implements WizzardStage {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteColumnChooser.class);
    private String[] columns = new String[]{ "Abbreviation", "Name", "Compartment", "Charge",
                                             "Formula",
                                             "KEGG Id", "ChEBI Id",
                                             "PubChem Id" };
    private Map<String, String> keyMap = new HashMap();
    private Map<String, JComboBox> comboBoxMap = new HashMap();
    private ExcelModelProperties properties = new ExcelModelProperties();
    private JTextField field = new JTextField("A1:G10");


    public MetaboliteColumnChooser(ExcelModelProperties properties) {
        this.properties = properties;
        keyMap.put(columns[0], "ent.col.abbreviation");
        keyMap.put(columns[1], "ent.col.name");
        keyMap.put(columns[2], "ent.col.compartment");
        keyMap.put(columns[3], "ent.col.charge");
        keyMap.put(columns[4], "ent.col.formula");
        keyMap.put(columns[5], "ent.col.xref.kegg");
        keyMap.put(columns[6], "ent.col.xref.chebi");
        keyMap.put(columns[7], "ent.col.xref.pubchem");
    }


    public void init() {


        setLayout(ViewUtils.formLayoutHelper(2, columns.length + 1, 2, 2));
        CellConstraints cc = new CellConstraints();
        int y = 1;
        add(new JLabel("Data Bounds:"), cc.xy(1, y));
        add(field, cc.xy(3, y));
        y += 2;
        for( String column : columns ) {
            JComboBox box = new JComboBox(new String[]{ "-", "A", "B", "C", "D", "E", "F", "G" });
            comboBoxMap.put(column, box);
            add(new JLabel(column + ":"), cc.xy(1, y));
            add(box, cc.xy(3, y));
            y += 2;
        }



    }


    public Boolean updateSelection() {
        properties.put("ent.data.bounds", field.getText());
        for( String column : columns ) {
            String value = (String) comboBoxMap.get(column).getSelectedItem();
            if( value.equals("-") == false ) {
                properties.put(keyMap.get(column), value);
            }
        }
        return true;
    }


    public void reloadPanel() {
        init();
    }


}

