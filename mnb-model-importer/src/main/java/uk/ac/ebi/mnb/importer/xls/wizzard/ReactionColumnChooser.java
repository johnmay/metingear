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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import mnb.io.tabular.ExcelModelProperties;
import static mnb.io.tabular.type.ReactionColumn.*;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.mnb.core.ExpandableComponentGroup;
import uk.ac.ebi.mnb.parser.ExcelHelper;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import uk.ac.ebi.mnb.xls.options.ImporterOptions;
import uk.ac.ebi.caf.component.factory.LabelFactory;


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
        extends JPanel
        implements WizzardStage {

    private static final Logger LOGGER = Logger.getLogger(ReactionColumnChooser.class);

    private CellConstraints cc = new CellConstraints();

    private ExcelHelper helper;

    private JTable reactionsSheet;

    private JTable metabolitesSheet;

    private ImporterOptions options;

    private Map<String, String> rxnColumns = new HashMap<String, String>();

    private Map<String, JComboBox> comboBoxes = new HashMap();

    private ExcelModelProperties properties;

    private String[] colNames = new String[]{"Abbreviation", "Description", "Equation",
                                             "Classification",
                                             "Subsystem", "Source"};

    private List<String> columns = new ArrayList();

    private JTextField field = new JTextField(20);
    // start end

    private JSpinner start;

    private JSpinner end;
    // combo boxes

    private JComboBox abbreviation;

    private JComboBox description;

    private JComboBox equation;

    private JComboBox classification;

    private JComboBox subsystem;

    private JComboBox source;

    private JComboBox locus;

    // extra (model data)

    private JComboBox direction;
    private JComboBox deltaG;
    private JComboBox deltaGError;
    private JComboBox minFlux;
    private JComboBox maxFlux;

    private SelectionTable table;


    public ReactionColumnChooser(ExcelHelper helper,
                                 ExcelModelProperties properties) {
        super();

        this.helper = helper;
        this.properties = properties;

        columns.add("-"); // no selection
        for (char c = 'A'; c <= 'Z'; ++c) {
            columns.add(Character.toString(c));
        }

        start = new JSpinner(new SpinnerNumberModel(1, 1, 4000, 1));
        end = new JSpinner(new SpinnerNumberModel(1, 1, 4000, 1));

        abbreviation = ComboBoxFactory.newComboBox(columns);
        description = ComboBoxFactory.newComboBox(columns);

        equation = ComboBoxFactory.newComboBox(columns);
        classification = ComboBoxFactory.newComboBox(columns);

        subsystem = ComboBoxFactory.newComboBox(columns);
        source = ComboBoxFactory.newComboBox(columns);

        locus = ComboBoxFactory.newComboBox(columns);



        FormLayout layout = new FormLayout("right:p:grow, 4dlu, min, 4dlu, right:p:grow, 4dlu, min", "p");

        // content panel
        setLayout(layout);

        removeAll();

        add(LabelFactory.newFormLabel("Start Row"), cc.xy(1, layout.getRowCount()));
        add(start, cc.xy(3, layout.getRowCount()));
        add(LabelFactory.newFormLabel("End Row"), cc.xy(5, layout.getRowCount()));
        add(end, cc.xy(7, layout.getRowCount()));

        layout.appendRow(new RowSpec(Sizes.DLUY2));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));

        add(new JSeparator(), cc.xyw(1, layout.getRowCount(), 7));

        layout.appendRow(new RowSpec(Sizes.DLUY2));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));

        add(LabelFactory.newFormLabel("Identifier/Abbreviation"), cc.xy(1, layout.getRowCount()));
        add(abbreviation, cc.xy(3, layout.getRowCount()));
        add(LabelFactory.newFormLabel("Name/Description"), cc.xy(5, layout.getRowCount()));
        add(description, cc.xy(7, layout.getRowCount()));

        layout.appendRow(new RowSpec(Sizes.DLUY2));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));

        add(LabelFactory.newFormLabel("Reaction Equation"), cc.xy(1, layout.getRowCount()));
        add(equation, cc.xy(3, layout.getRowCount()));
        add(LabelFactory.newFormLabel("Classification (EC/TC Number)"), cc.xy(5, layout.getRowCount()));
        add(classification, cc.xy(7, layout.getRowCount()));

        layout.appendRow(new RowSpec(Sizes.DLUY2));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));

        add(LabelFactory.newFormLabel("Subsystem/Reaction type"), cc.xy(1, layout.getRowCount()));
        add(subsystem, cc.xy(3, layout.getRowCount()));
        add(LabelFactory.newFormLabel("Source/Reference:"), cc.xy(5, layout.getRowCount()));
        add(source, cc.xy(7, layout.getRowCount()));

        layout.appendRow(new RowSpec(Sizes.DLUY2));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));

        add(LabelFactory.newFormLabel("Locus"), cc.xy(1, layout.getRowCount()));
        add(locus, cc.xy(3, layout.getRowCount()));

        layout.appendRow(new RowSpec(Sizes.DLUY2));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));

        JPanel extra = PanelFactory.createDialogPanel("right:p:grow, 4dlu, min, 4dlu, right:p:grow, 4dlu, min",
                                                      "p, 2dlu, p, 2dlu, p");

        deltaG      = ComboBoxFactory.newComboBox(columns);
        deltaGError = ComboBoxFactory.newComboBox(columns);
        minFlux     = ComboBoxFactory.newComboBox(columns);
        maxFlux     = ComboBoxFactory.newComboBox(columns);
        direction   = ComboBoxFactory.newComboBox(columns);

        extra.add(LabelFactory.newFormLabel("Free energy / ΔG"), cc.xy(1, 1));
        extra.add(deltaG, cc.xy(3, 1));
        extra.add(LabelFactory.newFormLabel("Free energy / ΔG (error)"), cc.xy(5, 1));
        extra.add(deltaGError, cc.xy(7, 1));
        
        extra.add(LabelFactory.newFormLabel("Lower Bound Flux"), cc.xy(1, 3));
        extra.add(minFlux, cc.xy(3, 3));
        extra.add(LabelFactory.newFormLabel("Upper Bound Flux"), cc.xy(5, 3));
        extra.add(maxFlux, cc.xy(7, 3));

        extra.add(LabelFactory.newFormLabel("Direction"), cc.xy(1, 5));
        extra.add(direction, cc.xy(3, 5));

        add(new ExpandableComponentGroup("Extra Columns", extra), cc.xyw(1, layout.getRowCount(), 7, CellConstraints.FILL, CellConstraints.FILL));

        layout.appendRow(new RowSpec(Sizes.DLUY2));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));

        add(new JSeparator(), cc.xyw(1, layout.getRowCount(), 7));

        layout.appendRow(new RowSpec(Sizes.DLUY2));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));

        table = new SelectionTable(helper);

        JScrollPane pane = new BorderlessScrollPane(table, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        RowNumberTable rnt = new RowNumberTable(table);
        pane.setRowHeaderView(rnt);
        pane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
                       rnt.getTableHeader());
        pane.setPreferredSize(new Dimension(800, table.getRowHeight() * 10));
        add(pane, cc.xyw(1, layout.getRowCount(), 7));


        // set previous selections
        Preferences pref = Preferences.userNodeForPackage(ReactionColumnChooser.class);
        start.setValue(pref.getInt(properties.getPreferenceKey("rxn.start"), 1));
        end.setValue(pref.getInt(properties.getPreferenceKey("rxn.end"), 10));
        abbreviation.setSelectedIndex(pref.getInt(properties.getPreferenceKey(ABBREVIATION), 0));
        description.setSelectedIndex(pref.getInt(properties.getPreferenceKey(DESCRIPTION), 0));
        equation.setSelectedIndex(pref.getInt(properties.getPreferenceKey(EQUATION), 0));
        classification.setSelectedIndex(pref.getInt(properties.getPreferenceKey(CLASSIFICATION), 0));
        subsystem.setSelectedIndex(pref.getInt(properties.getPreferenceKey(SUBSYSTEM), 0));
        source.setSelectedIndex(pref.getInt(properties.getPreferenceKey(SOURCE), 0));
        locus.setSelectedIndex(pref.getInt(properties.getPreferenceKey(LOCUS), 0));
        minFlux.setSelectedIndex(pref.getInt(properties.getPreferenceKey(MIN_FLUX), 0));
        maxFlux.setSelectedIndex(pref.getInt(properties.getPreferenceKey(MAX_FLUX), 0));
        deltaG.setSelectedIndex(pref.getInt(properties.getPreferenceKey(FREE_ENERGY), 0));
        deltaGError.setSelectedIndex(pref.getInt(properties.getPreferenceKey(FREE_ENERGY_ERROR), 0));
        direction.setSelectedIndex(pref.getInt(properties.getPreferenceKey(DIRECTION), 0));


        // listeners to change table header name
        abbreviation.addActionListener(new TableHeaderChanger(abbreviation, "Abbreviation"));
        description.addActionListener(new TableHeaderChanger(description, "Description"));
        equation.addActionListener(new TableHeaderChanger(equation, "Equation"));
        classification.addActionListener(new TableHeaderChanger(classification, "Classification"));
        source.addActionListener(new TableHeaderChanger(source, "Source"));
        subsystem.addActionListener(new TableHeaderChanger(subsystem, "Subsystem"));

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


    public Boolean updateSelection() {

        // the column doesn't matter for the bounds
        properties.put("rxn.data.bounds", "A" + start.getValue() + ":" + "A" + end.getValue());

        properties.put("rxn.col.identifier", abbreviation.getSelectedItem());
        properties.put("rxn.col.description", description.getSelectedItem());
        properties.put("rxn.col.equation", equation.getSelectedItem());
        properties.put("rxn.col.classification", classification.getSelectedItem());
        properties.put("rxn.col.subsystem", subsystem.getSelectedItem());
        properties.put("rxn.col.source", source.getSelectedItem());
        properties.put("rxn.col.locus", locus.getSelectedItem());
        
        properties.put(FREE_ENERGY.getKey(),       deltaG.getSelectedItem());
        properties.put(FREE_ENERGY_ERROR.getKey(), deltaGError.getSelectedItem());
        properties.put(MIN_FLUX.getKey(),      minFlux.getSelectedItem());
        properties.put(MAX_FLUX.getKey(),      maxFlux.getSelectedItem());
        properties.put(DIRECTION.getKey(), direction.getSelectedItem());

        // set selections for next time
        Preferences pref = Preferences.userNodeForPackage(ReactionColumnChooser.class);
        pref.putInt(properties.getPreferenceKey("rxn.start"), (Integer) start.getValue());
        pref.putInt(properties.getPreferenceKey("rxn.end"), (Integer) end.getValue());
        pref.putInt(properties.getPreferenceKey(ABBREVIATION), abbreviation.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(DESCRIPTION), description.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(EQUATION), equation.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(CLASSIFICATION), classification.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(SUBSYSTEM), subsystem.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(SOURCE), source.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(LOCUS), locus.getSelectedIndex());

        // extra
        pref.putInt(properties.getPreferenceKey(FREE_ENERGY),       deltaG.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(FREE_ENERGY_ERROR), deltaGError.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(MIN_FLUX),      minFlux.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(MAX_FLUX),      maxFlux.getSelectedIndex());
        pref.putInt(properties.getPreferenceKey(DIRECTION),     direction.getSelectedIndex());



        return true;

    }


    public void reloadPanel() {
        if (properties.containsKey(properties.REACTION_SHEET)) {
            int index = Integer.parseInt(properties.getProperty(properties.REACTION_SHEET));
            table.setSheet(index);
        }
    }


    public String getDescription() {
        return "<html>Please confirm the appropiate columns in the reaction sheet. Please note that the<br>"
               + "data bounds should <b>not</b> include the table header</html>";
    }
}
