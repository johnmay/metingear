
/**
 * SheetChooserDialog.java
 *
 * 2011.08.03
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
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;
import mnb.io.tabular.ExcelModelProperties;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.table.SelectableHeader;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.parser.ExcelImporter;
import uk.ac.ebi.mnb.xls.options.ImporterOptions;
import uk.ac.ebi.mnb.xls.options.SheetType;


/**
 * @name    SheetChooserDialog
 * @date    2011.08.03
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   Used to choose the sheet of the XLS file to import
 *
 */
public class ReactionColumnChooser
  extends GeneralPanel
  implements WizzardStage {

    private static final Logger LOGGER = Logger.getLogger(ReactionColumnChooser.class);
    private FormLayout layout = new FormLayout("pref:grow", "p");
    private CellConstraints cc = new CellConstraints();
    private ExcelImporter excelHandler;
    private JTable reactionsSheet;
    private JTable metabolitesSheet;
    private ImporterOptions options;
    private Map<String, String> rxnColumns = new HashMap<String, String>();
    private Map<String, JComboBox> comboBoxes = new HashMap();
    private ExcelModelProperties properties;
    private String[] colNames = new String[]{ "Identifier", "Description", "Equation",
                                              "Classification",
                                              "Subsystem", "Source" };


    public ReactionColumnChooser(ExcelImporter excelImporter,
                                 ImporterOptions options,
                                 ExcelModelProperties properties) {
        super();
        setName("Choose columns from reaction");
        this.excelHandler = excelImporter;
        this.options = options;
        this.properties = properties;

        rxnColumns.put("Identifier", "rxn.col.identifier");
        rxnColumns.put("Description", "rxn.col.description");
        rxnColumns.put("Equation", "rxn.col.equation");
        rxnColumns.put("Classification", "rxn.col.classification");
        rxnColumns.put("Subsystem", "rxn.col.subsystem");
        rxnColumns.put("Source", "rxn.col.source");


    }


    private JTextField field = new JTextField("A1:D10");


    private void init() {

        setLayout(layout);

        // content panel
        setLayout(ViewUtils.formLayoutHelper(2, rxnColumns.size() + 1, 2, 2));
        comboBoxes = new HashMap<String, JComboBox>();
        int rowIndex = 1;
        CellConstraints cc = new CellConstraints();

        add(new JLabel("Bounds"), cc.xy(1, rowIndex));
        add(field, cc.xy(3, rowIndex));

        rowIndex += 2;

        for( String colName : colNames ) {
            JLabel label = new JLabel(colName + ":");
            JComboBox comboBox =
                      new JComboBox(new String[]{ "-", "A", "B", "C", "D",
                                                  "E", "F", "G", "H", "I" });
            add(label, cc.xy(1, rowIndex));
            add(comboBox, cc.xy(3, rowIndex));
            rowIndex += 2;
            comboBoxes.put(colName, comboBox);
        }

    }


    public Boolean updateSelection() {

        properties.put("rxn.data.bounds", field.getText());


        for( String key : comboBoxes.keySet() ) {

            String propertiesKey = rxnColumns.get(key);
            String column = (String) comboBoxes.get(key).getSelectedItem();

            if( column.equals("-") == false ) {
                properties.put(propertiesKey, column);
            }

        }

//        properties.list(System.out);

        return true;

    }


    public void reloadPanel() {
        init();
    }


}

