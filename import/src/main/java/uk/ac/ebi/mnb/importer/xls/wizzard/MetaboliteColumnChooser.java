/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.importer.xls.wizzard;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mnb.io.tabular.ExcelModelProperties;
import mnb.io.tabular.type.EntityColumn;
import mnb.io.tabular.util.ExcelUtilities;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.BorderlessScrollPane;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.mnb.parser.ExcelHelper;
import uk.ac.ebi.caf.component.factory.LabelFactory;


/**
 *          MetaboliteColumnChooser – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetaboliteColumnChooser
        extends JPanel
        implements WizzardStage {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteColumnChooser.class);

    private ExcelModelProperties properties = new ExcelModelProperties();
    // spinners

    private JSpinner start;

    private JSpinner end;
    // combo boxes

    private JComboBox abbreviation;

    private JComboBox description;

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

        abbreviation = ComboBoxFactory.newComboBox((Object[])ExcelUtilities.getComboBoxValues(0, 40));
        description = ComboBoxFactory.newComboBox((Object[])ExcelUtilities.getComboBoxValues(0, 40));
        compartment = ComboBoxFactory.newComboBox((Object[])ExcelUtilities.getComboBoxValues(0, 40));
        charge = ComboBoxFactory.newComboBox((Object[])ExcelUtilities.getComboBoxValues(0, 40));
        formula = ComboBoxFactory.newComboBox((Object[])ExcelUtilities.getComboBoxValues(0, 40));
        kegg = ComboBoxFactory.newComboBox((Object[])ExcelUtilities.getComboBoxValues(0, 40));
        chebi = ComboBoxFactory.newComboBox((Object[])ExcelUtilities.getComboBoxValues(0, 40));
        pubchem = ComboBoxFactory.newComboBox((Object[])ExcelUtilities.getComboBoxValues(0, 40));
        table = new SelectionTable(helper);


        setLayout(new FormLayout("p, 4dlu, p, 4dlu, p, 4dlu, p",
                                 "p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p"));
//
        add(LabelFactory.newFormLabel("Data starts (excluding header):"), cc.xy(1, 1));
        add(start, cc.xy(3, 1));
        add(LabelFactory.newFormLabel("Data ends:"), cc.xy(5, 1));
        add(end, cc.xy(7, 1));
//
        add(new JSeparator(), cc.xyw(1, 3, 7));

        add(LabelFactory.newFormLabel("Identifier/Abbreviation"), cc.xy(1, 5));
        add(abbreviation, cc.xy(3, 5));
        add(LabelFactory.newFormLabel("Name"), cc.xy(5, 5));
        add(description, cc.xy(7, 5));
        add(LabelFactory.newFormLabel("Compartment"), cc.xy(1, 7));
        add(compartment, cc.xy(3, 7));
        add(LabelFactory.newFormLabel("Charge"), cc.xy(5, 7));
        add(charge, cc.xy(7, 7));
        add(LabelFactory.newFormLabel("Molecular formula"), cc.xy(1, 9));
        add(formula, cc.xy(3, 9));

        add(new JSeparator(), cc.xyw(1, 11, 7));

        add(LabelFactory.newFormLabel("KEGG Ligand Xref"), cc.xy(1, 13));
        add(kegg, cc.xy(3, 13));
        add(LabelFactory.newFormLabel("ChEBI Xref"), cc.xy(5, 13));
        add(chebi, cc.xy(7, 13));
        add(LabelFactory.newFormLabel("PubChem-Compound Xref"), cc.xy(1, 15));
        add(pubchem, cc.xy(3, 15));

        add(new JSeparator(), cc.xyw(1, 17, 7));

        pane = new BorderlessScrollPane(table);
        RowNumberTable rnt = new RowNumberTable(table);
        pane.setRowHeaderView(rnt);
        pane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
                       rnt.getTableHeader());
        pane.setPreferredSize(new Dimension(800, table.getRowHeight() * 10));
        add(pane, cc.xyw(1, 19, 7));


        // set previous selections
        Preferences pref = Preferences.userNodeForPackage(MetaboliteColumnChooser.class);
        start.setValue(pref.getInt(properties.getPreferenceKey("ent.start"), 1));
        end.setValue(pref.getInt(properties.getPreferenceKey("ent.end"), 10));
        abbreviation.setSelectedIndex(pref.getInt(properties.getPreferenceKey(EntityColumn.ABBREVIATION), 0));
        description.setSelectedIndex(pref.getInt(properties.getPreferenceKey(EntityColumn.DESCRIPTION), 0));
        compartment.setSelectedIndex(pref.getInt(properties.getPreferenceKey(EntityColumn.COMPARTMENT), 0));
        charge.setSelectedIndex(pref.getInt(properties.getPreferenceKey(EntityColumn.CHARGE), 0));
        formula.setSelectedIndex(pref.getInt(properties.getPreferenceKey(EntityColumn.FORMULA), 0));
        kegg.setSelectedIndex(pref.getInt(properties.getPreferenceKey(EntityColumn.KEGG_XREF), 0));
        chebi.setSelectedIndex(pref.getInt(properties.getPreferenceKey(EntityColumn.CHEBI_XREF), 0));
        pubchem.setSelectedIndex(pref.getInt(properties.getPreferenceKey(EntityColumn.PUBCHEM_XREF), 0));



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
        description.addActionListener(new TableHeaderChanger(description, "Name"));
        compartment.addActionListener(new TableHeaderChanger(compartment, "Compartment"));
        charge.addActionListener(new TableHeaderChanger(charge, "Charge"));
        formula.addActionListener(new TableHeaderChanger(formula, "Formula"));
        chebi.addActionListener(new TableHeaderChanger(chebi, "ChEBI"));
        kegg.addActionListener(new TableHeaderChanger(kegg, "KEGG Ligand"));
        pubchem.addActionListener(new TableHeaderChanger(pubchem, "PubChem-Compound"));




    }


    public Boolean updateSelection() {

        properties.put("ent.data.bounds", "A" + start.getValue() + ":" + "A" + end.getValue());

        setProperty(EntityColumn.ABBREVIATION.getKey(), abbreviation);
        setProperty(EntityColumn.DESCRIPTION.getKey(), description);
        setProperty(EntityColumn.COMPARTMENT.getKey(), compartment);
        setProperty(EntityColumn.CHARGE.getKey(), charge);
        setProperty(EntityColumn.FORMULA.getKey(), formula);
        setProperty(EntityColumn.KEGG_XREF.getKey(), kegg);
        setProperty(EntityColumn.CHEBI_XREF.getKey(), chebi);
        setProperty(EntityColumn.PUBCHEM_XREF.getKey(), pubchem);


        // set selections for next time
        Preferences pref = Preferences.userNodeForPackage(MetaboliteColumnChooser.class);
        pref.putInt(properties.getPreferenceKey("ent.start"), (Integer) start.getValue());
        pref.putInt(properties.getPreferenceKey("ent.end"), (Integer) end.getValue());
        pref.putInt(properties.getPreferenceKey(EntityColumn.ABBREVIATION), abbreviation.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(EntityColumn.DESCRIPTION), description.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(EntityColumn.COMPARTMENT), compartment.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(EntityColumn.CHARGE), charge.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(EntityColumn.FORMULA), formula.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(EntityColumn.KEGG_XREF), kegg.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(EntityColumn.CHEBI_XREF), chebi.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(EntityColumn.PUBCHEM_XREF), pubchem.getSelectedIndex());

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
