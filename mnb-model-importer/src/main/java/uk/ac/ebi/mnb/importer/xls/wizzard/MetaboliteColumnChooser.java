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
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mnb.io.tabular.ExcelModelProperties;
import mnb.io.tabular.util.ExcelUtilities;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.parser.ExcelHelper;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import uk.ac.ebi.mnb.view.ComboBox;
import uk.ac.ebi.mnb.view.DialogPanel;
import uk.ac.ebi.mnb.view.labels.DialogLabel;

/**
 *          MetaboliteColumnChooser – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetaboliteColumnChooser
        extends DialogPanel
        implements WizzardStage {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteColumnChooser.class);

    private ExcelModelProperties properties = new ExcelModelProperties();
    // spinners
    private JSpinner start;
    private JSpinner end;
    // combo boxes
    private JComboBox abbreviation;
    private JComboBox name;
    private JComboBox compartment;
    private JComboBox charge;
    private JComboBox formula;
    //
    private JComboBox kegg;
    private JComboBox chebi;
    private JComboBox pubchem;
    private SelectionTable table;
    //
    private ExcelHelper helper;
    private JScrollPane pane;
    //
    private CellConstraints cc = new CellConstraints();

    public MetaboliteColumnChooser(ExcelHelper helper, ExcelModelProperties properties) {
        super();
        this.properties = properties;
        this.helper = helper;

        start = new JSpinner(new SpinnerNumberModel(1, 1, 4000, 1));
        end = new JSpinner(new SpinnerNumberModel(1, 1, 4000, 1));

        abbreviation = new ComboBox(ExcelUtilities.getComboBoxValues(0, 40));
        name = new ComboBox(ExcelUtilities.getComboBoxValues(0, 40));
        compartment = new ComboBox(ExcelUtilities.getComboBoxValues(0, 40));
        charge = new ComboBox(ExcelUtilities.getComboBoxValues(0, 40));
        formula = new ComboBox(ExcelUtilities.getComboBoxValues(0, 40));
        kegg = new ComboBox(ExcelUtilities.getComboBoxValues(0, 40));
        chebi = new ComboBox(ExcelUtilities.getComboBoxValues(0, 40));
        pubchem = new ComboBox(ExcelUtilities.getComboBoxValues(0, 40));
        table = new SelectionTable(helper);


        setLayout(new FormLayout("p, 4dlu, p, 4dlu, p, 4dlu, p",
                "p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p"));
//
        add(new DialogLabel("Data starts (excluding header):", SwingConstants.RIGHT), cc.xy(1, 1));
        add(start, cc.xy(3, 1));
        add(new DialogLabel("Data ends:", SwingConstants.RIGHT), cc.xy(5, 1));
        add(end, cc.xy(7, 1));
//
        add(new JSeparator(), cc.xyw(1, 3, 7));

        add(new DialogLabel("Abbreviation:", SwingConstants.RIGHT), cc.xy(1, 5));
        add(abbreviation, cc.xy(3, 5));
        add(new DialogLabel("Name:", SwingConstants.RIGHT), cc.xy(5, 5));
        add(name, cc.xy(7, 5));
        add(new DialogLabel("Compartment:", SwingConstants.RIGHT), cc.xy(1, 7));
        add(compartment, cc.xy(3, 7));
        add(new DialogLabel("Charge:", SwingConstants.RIGHT), cc.xy(5, 7));
        add(charge, cc.xy(7, 7));
        add(new DialogLabel("Molecular formula:", SwingConstants.RIGHT), cc.xy(1, 9));
        add(formula, cc.xy(3, 9));

        add(new JSeparator(), cc.xyw(1, 11, 7));

        add(new DialogLabel("KEGG cross-reference:", SwingConstants.RIGHT), cc.xy(1, 13));
        add(kegg, cc.xy(3, 13));
        add(new DialogLabel("ChEBI cross-reference:", SwingConstants.RIGHT), cc.xy(5, 13));
        add(chebi, cc.xy(7, 13));
        add(new DialogLabel("PubChem cross-reference:", SwingConstants.RIGHT), cc.xy(1, 15));
        add(pubchem, cc.xy(3, 15));

        add(new JSeparator(), cc.xyw(1, 17, 7));

        pane = new BorderlessScrollPane(table);
        RowNumberTable rnt = new RowNumberTable(table);
        pane.setRowHeaderView(rnt);
        pane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
                rnt.getTableHeader());
        pane.setPreferredSize(new Dimension(800, table.getRowHeight() * 10));
        add(pane, cc.xyw(1, 19, 7));


        // action listeners
        // Spinner listeners for shading the table
        start.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                table.setStart((Integer) start.getValue());
                repaint();
            }
        });
        end.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                table.setEnd((Integer) end.getValue());
                repaint();
            }
        });

        abbreviation.addActionListener(new TableHeaderChanger(abbreviation, "Abbreviation"));
        name.addActionListener(new TableHeaderChanger(name, "Name"));
        compartment.addActionListener(new TableHeaderChanger(compartment, "Compartment"));
        charge.addActionListener(new TableHeaderChanger(charge, "Charge"));
        formula.addActionListener(new TableHeaderChanger(formula, "Formula"));
        chebi.addActionListener(new TableHeaderChanger(chebi, "ChEBI"));
        kegg.addActionListener(new TableHeaderChanger(kegg, "KEGG"));
        pubchem.addActionListener(new TableHeaderChanger(pubchem, "PubChem"));


    }

    public Boolean updateSelection() {

        properties.put("ent.data.bounds", "A" + start.getValue() + ":" + "A" + end.getValue());

        setProperty("ent.col.abbreviation", abbreviation);
        setProperty("ent.col.name", name);
        setProperty("ent.col.compartment", compartment);
        setProperty("ent.col.charge", charge);
        setProperty("ent.col.formula", formula);
        setProperty("ent.col.xref.kegg", kegg);
        setProperty("ent.col.xref.chebi", chebi);
        setProperty("ent.col.xref.chebi", chebi);


        return true;

    }

    private class TableHeaderChanger extends AbstractAction {

        private String name;
        private JComboBox cb;

        public TableHeaderChanger(JComboBox combobox, String name) {
            this.cb = combobox;
            this.name = name;
        }

        public void actionPerformed(ActionEvent ae) {
            table.setHeader(cb.getSelectedIndex(), name);
            repaint();
            revalidate();
        }
    }

    public void setProperty(String key, JComboBox combobox) {
        if (combobox.getSelectedIndex() != 0) {
            String item = (String) combobox.getSelectedItem();
            this.properties.setProperty(key, item.trim());
        }
    }

    public void reloadPanel() {
        if (properties.containsKey(properties.METABOLITE_SHEET)) {
            table.setSheet(Integer.parseInt(properties.getProperty(ExcelModelProperties.METABOLITE_SHEET)));
        }
    }

    public String getDescription() {
        return "<html>Please confirm the appropiate columns in the metabolite sheet</html>";
    }
}
