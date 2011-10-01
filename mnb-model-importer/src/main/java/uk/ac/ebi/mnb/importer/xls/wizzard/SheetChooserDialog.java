
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
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import mnb.io.tabular.ExcelModelProperties;
import uk.ac.ebi.mnb.view.GeneralPanel;
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
public class SheetChooserDialog
  extends GeneralPanel
  implements WizzardStage {

    private static final Logger LOGGER = Logger.getLogger(SheetChooserDialog.class);
    private FormLayout layout = new FormLayout("p, 4dlu, p, 4dlu, p", "p,4dlu,p,4dlu,p,4dlu,p");
    private CellConstraints cc = new CellConstraints();
    private ExcelImporter excelHandler;
    private ImporterOptions options;
    //  private JTable reactionsSheet;
    //  private JTable metabolitesSheet;
    private JComboBox reactionCB;
    private JComboBox metaboliteCB;
    private JComboBox geneCB;
    private ExcelModelProperties properties;


    public SheetChooserDialog(ExcelImporter excelImporter, ImporterOptions options,
                              ExcelModelProperties properties) {
        super();
        this.excelHandler = excelImporter;
        this.options = options;
        this.properties = properties;
        init();
    }


    private void init() {
        // get all sheet names
        List<String> sheetNames = excelHandler.getSheetNames();
        sheetNames.add(0, "- None -");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(layout);

        reactionCB = new JComboBox(new DefaultComboBoxModel(sheetNames.toArray()));

        // again.. set the selection to the first one
        List<Integer> reactionSheets = excelHandler.getReactionSheetIndices();
        if( reactionSheets.size() > 0 ) {
            reactionCB.setSelectedItem(sheetNames.get(reactionSheets.get(0) + 1));
        }

        metaboliteCB = new JComboBox(new DefaultComboBoxModel(sheetNames.toArray()));

        // again.. set the selection to the first one
        List<Integer> metaboliteSheets = excelHandler.getMetaboliteSheetIndices();
        if( metaboliteSheets.size() > 0 ) {
            metaboliteCB.setSelectedItem(sheetNames.get(metaboliteSheets.get(0) + 1));
        }

        geneCB = new JComboBox(new DefaultComboBoxModel(sheetNames.toArray()));

        mainPanel.add(reactionCB, cc.xy(3, 1));
        mainPanel.add(metaboliteCB, cc.xy(3, 3));
        mainPanel.add(geneCB, cc.xy(3, 5));
        add(mainPanel, BorderLayout.CENTER);

    }


    public Boolean updateSelection() {

        // the index 0: - None - selection so we check it's more then subtract 1
        // to get the actual selection

        // save the reaction sheet selection
        Integer reactionIndex = reactionCB.getSelectedIndex();
        if( reactionIndex > 0 ) {
            String index = ((Integer) (reactionIndex - 1)).toString();
            properties.put("rxn.sheet", index);
        } else {
            // can't do anything without a reaction table
            return false;
        }

        // save the metabolite sheet selection
        Integer metabolicIndex = metaboliteCB.getSelectedIndex();
        if( metabolicIndex > 0 ) {
            String index = ((Integer) (metabolicIndex - 1)).toString();
            properties.put("ent.sheet", index);
        }


        // options are okay
        return true;
    }


    public void reloadPanel() {
        //    throw new UnsupportedOperationException( "Not supported yet." );
    }


}

