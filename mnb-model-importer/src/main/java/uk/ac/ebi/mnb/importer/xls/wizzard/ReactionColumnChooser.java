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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ComponentFactory;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mnb.io.tabular.ExcelModelProperties;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.parser.ExcelHelper;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import uk.ac.ebi.mnb.view.ComboBox;
import uk.ac.ebi.mnb.view.DialogPanel;
import uk.ac.ebi.mnb.view.labels.DialogLabel;
import uk.ac.ebi.mnb.xls.options.ImporterOptions;

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
        extends DialogPanel
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
    //
    private SelectionTable table;

    public ReactionColumnChooser(ExcelHelper helper,
            ImporterOptions options,
            ExcelModelProperties properties) {
        super();

        this.helper = helper;
        this.options = options;
        this.properties = properties;

        columns.add("-"); // no selection
        for (char c = 'A'; c <= 'Z'; ++c) {
            columns.add(Character.toString(c));
        }

        start = new JSpinner(new SpinnerNumberModel(1, 1, 4000, 1));
        end = new JSpinner(new SpinnerNumberModel(1, 1, 4000, 1));

        abbreviation = new ComboBox(columns);
        description = new ComboBox(columns);

        equation = new ComboBox(columns);
        classification = new ComboBox(columns);

        subsystem = new ComboBox(columns);
        source = new ComboBox(columns);

        // content panel
        setLayout(new FormLayout("p, 4dlu, p, 4dlu, p, 4dlu, p", "p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p"));

        removeAll();

        add(new DialogLabel("Start Row:", SwingConstants.RIGHT), cc.xy(1, 1));
        add(start, cc.xy(3, 1));
        add(new DialogLabel("End Row:", SwingConstants.RIGHT), cc.xy(5, 1));
        add(end, cc.xy(7, 1));

        add(new JSeparator(), cc.xyw(1, 3, 7));

        add(new DialogLabel("Identifier/Abbreviation:", SwingConstants.RIGHT), cc.xy(1, 5));
        add(abbreviation, cc.xy(3, 5));
        add(new DialogLabel("Name/Description:", SwingConstants.RIGHT), cc.xy(5, 5));
        add(description, cc.xy(7, 5));

        add(new DialogLabel("Reaction Equation:", SwingConstants.RIGHT), cc.xy(1, 7));
        add(equation, cc.xy(3, 7));
        add(new DialogLabel("Classification (EC/TC Number):", SwingConstants.RIGHT), cc.xy(5, 7));
        add(classification, cc.xy(7, 7));

        add(new DialogLabel("Subsystem/Reaction type:", SwingConstants.RIGHT), cc.xy(1, 9));
        add(subsystem, cc.xy(3, 9));
        add(new DialogLabel("Source/Reference:", SwingConstants.RIGHT), cc.xy(5, 9));
        add(source, cc.xy(7, 9));

        add(new JSeparator(), cc.xyw(1, 11, 7));


        table = new SelectionTable(helper);

        JScrollPane pane = new BorderlessScrollPane(table);
        RowNumberTable rnt = new RowNumberTable(table);
        pane.setRowHeaderView(rnt);
        pane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
                rnt.getTableHeader());
        pane.setPreferredSize(new Dimension(800, table.getRowHeight() * 10));
        add(pane, cc.xyw(1, 13, 7));


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
