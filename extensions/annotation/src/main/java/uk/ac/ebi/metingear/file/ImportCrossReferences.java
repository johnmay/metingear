/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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

package uk.ac.ebi.metingear.file;

import au.com.bytecode.opencsv.CSVReader;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.mdk.domain.DefaultIdentifierFactory;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.identifier.IdentifierFactory;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.core.ErrorMessage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author John May
 */
public class ImportCrossReferences extends AbstractControlDialog {

    enum Separators {
        TAB("Tabs", '\t'),
        COMMA("Commas", ',');

        private final String name;
        private final char c;

        Separators(String name, char c) {
            this.name = name;
            this.c = c;
        }


        @Override public String toString() {
            return name;
        }
    }

    private File selected;
    private JLabel selectedLabel;
    private JTable preview;
    private JComboBox separator = ComboBoxFactory.newComboBox(Separators
                                                                      .values());
    private JCheckBox headerCheck;
    private JCheckBox inferCheck;
    private JComboBox resource = ComboBoxFactory.newComboBox(DefaultIdentifierFactory.getInstance().getSupportedIdentifiers());
    private JComboBox mapTo    = ComboBoxFactory.newComboBox("Identifier", "Abbreviation", "Name");
    private boolean header;
    private boolean infer;
    private int previewLength = 5;
    private int nCols = 2;
    private DefaultTableModel model;
    private final JDialog self;

    public ImportCrossReferences(Window window) {
        super(window);
        this.self = this;
    }

    @Override public JComponent createForm() {
        final JComponent component = super.createForm();

        component.setLayout(new BoxLayout(component, BoxLayout.PAGE_AXIS));

        JPanel selection = PanelFactory.createDialogPanel();

        selection.setLayout(new FormLayout("right:p, 4dlu, p, 4dlu, min",
                                           "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"));
        CellConstraints cc = new CellConstraints();
        selection.add(getLabel("selected"), cc.xy(1, 1));
        JButton button = iconButton(new AbstractAction("browse") {
            @Override public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (chooser
                        .showOpenDialog(self) == JFileChooser.APPROVE_OPTION) {
                    selected = chooser.getSelectedFile();
                    selectedLabel.setText(selected.getName());
                    loadPreview();
                }
            }
        });
        selectedLabel = LabelFactory.newLabel("none");
        selection.add(selectedLabel, cc.xy(3, 1));
        selection.add(button, cc.xy(5, 1));


        headerCheck = CheckBoxFactory.newCheckBox();
        headerCheck.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent e) {
                header = headerCheck.isSelected();
                loadPreview();
            }
        });

        inferCheck = CheckBoxFactory.newCheckBox();
        inferCheck.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent e) {
                header = headerCheck.isSelected();
                loadPreview();
            }
        });


        selection.add(getLabel("header"), cc.xy(1, 3));
        selection.add(headerCheck, cc.xy(3, 3));
        selection.add(getLabel("separator"), cc.xy(1, 5));
        selection.add(separator, cc.xy(3, 5));


        JRadioButton infer, single, mapped;

        final JPanel config = new JPanel();
        final CardLayout configLayout = new CardLayout();

        infer = radioButton(new AbstractAction("infer") {
            @Override public void actionPerformed(ActionEvent e) {
                configLayout.show(config, "infer");
                nCols = 2;
                loadPreview();
                revalidate();
                pack();
            }
        });

        single = radioButton(new AbstractAction("single") {
            @Override public void actionPerformed(ActionEvent e) {
                configLayout.show(config, "single");
                nCols = 2;
                loadPreview();
                revalidate();
                pack();
            }
        });

        mapped = radioButton(new AbstractAction("mapped") {
            @Override public void actionPerformed(ActionEvent e) {
                configLayout.show(config, "mapped");
                nCols = 3;
                loadPreview();
                revalidate();
                pack();
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(infer);
        group.add(single);
        group.add(mapped);

        Box modes = Box.createHorizontalBox();
        modes.add(infer);
        modes.add(single);
        modes.add(mapped);

        selection.add(getLabel("mode"), cc.xy(1, 9));
        selection.add(modes, cc.xy(3, 9));

        selection.add(getLabel("mapTo"), cc.xy(1, 7));
        selection.add(mapTo, cc.xy(3, 7));


        JPanel inferConfig  = new JPanel(new FormLayout("p:grow", "p"));
        JPanel singleConfig = new JPanel(new FormLayout("p:grow", "p, 4dlu, p"));
        JPanel mappedConfig = new JPanel(new FormLayout("p:grow", "p"));

        inferConfig.add(area("inferArea"), cc.xy(1,1));
        singleConfig.add(area("singleArea"), cc.xy(1,1));
        singleConfig.add(resource, cc.xy(1, 3));
        final DefaultIdentifierFactory ids = DefaultIdentifierFactory.getInstance();
        resource.setRenderer(new ListCellRenderer() {

            private JLabel label = LabelFactory.newLabel("N/A", LabelFactory.Size.SMALL);

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                label.setFont(selectedLabel.getFont());
                Identifier id = (Identifier) value;
                label.setText(id.getShortDescription());
                label.setToolTipText(id.getLongDescription());
                label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                label.setBackground(isSelected ? list.getSelectionForeground() : list.getForeground());
                return label;
            }
        });
        mappedConfig.add(area("mappedArea"), cc.xy(1,1));

        config.setLayout(configLayout);
        config.add(inferConfig, "infer");
        config.add(singleConfig, "single");
        config.add(mappedConfig, "mapped");

        infer.setSelected(true);

        separator.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent e) {
                loadPreview();
            }
        });

        model = new DefaultTableModel(previewLength, 3);
        preview = new JTable(model);

        component.add(selection);
        component.add(config);
        component.add(new JSeparator());

        JPanel tableHolder = new JPanel();
        tableHolder.setLayout(new BorderLayout());
        tableHolder.add(preview.getTableHeader(), BorderLayout.NORTH);
        tableHolder.add(preview, BorderLayout.CENTER);

        component.add(tableHolder);

        return component;
    }

    private char separatingChar() {
        return ((Separators) separator.getSelectedItem()).c;
    }

    private void loadPreview() {
        if (selected != null && selected.exists()) {
            CSVReader reader = null;
            try {
                reader = new CSVReader(new FileReader(selected),
                                       separatingChar(),
                                       '\0');
                String[][] rows = new String[previewLength + 1][];
                String[]     row;
                String[] tmpHeader = null;

                if (header) {
                    tmpHeader = reader.readNext();
                }

                for(int i = 0; i < rows.length && (row = reader.readNext()) != null; i++){
                    rows[i] = Arrays.copyOf(row, nCols);
                }


                final String[][] data = rows;

                final String[] header = tmpHeader != null ? Arrays.copyOf(tmpHeader, nCols)
                                                          : new String[nCols];

                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        model.setDataVector(data, header);
                        model.fireTableDataChanged();
                        self.pack();
                    }
                });


            } catch (FileNotFoundException e) {
                report(new ErrorMessage("File was not found: " + e
                        .getMessage()));
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) { // can't do anything }
                }
            }
        }
    }
}
