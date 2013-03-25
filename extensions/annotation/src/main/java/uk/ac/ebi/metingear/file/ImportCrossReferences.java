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
import com.google.common.base.Joiner;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.chemet.tools.annotation.IdentifierMapper;
import uk.ac.ebi.mdk.domain.DefaultIdentifierFactory;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.undo.CompoundEdit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

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

    enum KeyType implements IdentifierMapper.KeyAccessor {

        IDENTIFIER("Identifier") {
            @Override public String key(AnnotatedEntity entity) {
                return entity.getIdentifier().getAccession();
            }
        },
        ABBREVIATION("Abbreviation") {
            @Override public String key(AnnotatedEntity entity) {
                return entity.getAbbreviation();
            }
        },
        NAME("Name") {
            @Override public String key(AnnotatedEntity entity) {
                return entity.getName();
            }
        };

        private final String displayName;

        KeyType(String displayName) {
            this.displayName = displayName;
        }

        @Override public String toString() {
            return displayName;
        }
    }

    private File selected;
    private JLabel selectedLabel;
    private JComboBox separator = ComboBoxFactory.newComboBox(Separators
                                                                      .values());
    private JCheckBox headerCheck;
    private JRadioButton infer, single, mapped;
    private JComboBox resource = ComboBoxFactory
            .newComboBox(DefaultIdentifierFactory.getInstance()
                                                 .getSupportedIdentifiers());
    private JComboBox mapTo = ComboBoxFactory.newComboBox(KeyType.values());
    private boolean header;
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

        JPanel selection = new JPanel();

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


        selection.add(getLabel("header"), cc.xy(1, 3));
        selection.add(headerCheck, cc.xy(3, 3));
        selection.add(getLabel("separator"), cc.xy(1, 5));
        selection.add(separator, cc.xy(3, 5));


        final JPanel config = new JPanel();
        final CardLayout configLayout = new CardLayout();

        infer = radioButton(new AbstractAction("infer") {
            @Override public void actionPerformed(ActionEvent e) {
                configLayout.show(config, "infer");
                nCols = 2;
                loadPreview();
                component.revalidate();
                pack();
            }
        });

        single = radioButton(new AbstractAction("single") {
            @Override public void actionPerformed(ActionEvent e) {
                configLayout.show(config, "single");
                nCols = 2;
                loadPreview();
                component.revalidate();
                pack();
            }
        });

        mapped = radioButton(new AbstractAction("mapped") {
            @Override public void actionPerformed(ActionEvent e) {
                configLayout.show(config, "mapped");
                nCols = 3;
                loadPreview();
                component.revalidate();
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


        JPanel inferConfig = new JPanel(new FormLayout("p:grow", "p"));
        JPanel singleConfig = new JPanel(new FormLayout("p:grow", "p, 4dlu, p"));
        JPanel mappedConfig = new JPanel(new FormLayout("p:grow", "p"));

        JTextArea inferInfo = area("inferArea");
        JTextArea singleInfo = area("singleArea");
        JTextArea mappedInfo = area("mappedArea");

        inferInfo.setBackground(getBackground());
        singleInfo.setBackground(getBackground());
        mappedInfo.setBackground(getBackground());

        inferConfig.add(inferInfo, cc.xy(1, 1));
        singleConfig.add(singleInfo, cc.xy(1, 1));
        singleConfig.add(resource, cc.xy(1, 3));
        final DefaultIdentifierFactory ids = DefaultIdentifierFactory
                .getInstance();
        resource.setRenderer(new ListCellRenderer() {

            private JLabel label = LabelFactory
                    .newLabel("N/A", LabelFactory.Size.SMALL);

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                label.setFont(selectedLabel.getFont());
                Identifier id = (Identifier) value;
                label.setText(id.getShortDescription());
                label.setToolTipText(id.getLongDescription());
                label.setBackground(isSelected ? list.getSelectionBackground()
                                               : list.getBackground());
                label.setBackground(isSelected ? list.getSelectionForeground()
                                               : list.getForeground());
                return label;
            }
        });
        mappedConfig.add(mappedInfo, cc.xy(1, 1));

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
        JTable preview = new JTable(model);

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
                String[] row;
                String[] tmpHeader = null;

                if (header) {
                    tmpHeader = reader.readNext();
                }

                for (int i = 0; i < rows.length && (row = reader
                        .readNext()) != null; i++) {
                    rows[i] = Arrays.copyOf(row, nCols);
                }


                final String[][] data = rows;

                final String[] header =
                        tmpHeader != null ? Arrays.copyOf(tmpHeader, nCols)
                                          : new String[nCols];

                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        model.setDataVector(data, header);
                        model.fireTableDataChanged();
                        self.pack();
                    }
                });


            } catch (FileNotFoundException e) {
                report(new ErrorMessage("File not found: " + e.getMessage()));
            } catch (IOException e) {
                report(new ErrorMessage("An error occurred whilst reading the file: " + e
                        .getMessage()));
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) { // can't do anything }
                }
            }
        }
    }

    @Override public void process() {

        final CompoundEdit edit = new CompoundEdit();
        final AnnotationFactory annotations = DefaultAnnotationFactory
                .getInstance();

        @SuppressWarnings("unchecked")
        IdentifierMapper.KeyAccessor<String> accessor =
                (IdentifierMapper.KeyAccessor<String>) mapTo.getSelectedItem();

        Reconstruction recon = DefaultReconstructionManager.getInstance()
                                                           .active();

        IdentifierMapper.Handler handler = new IdentifierMapper.Handler() {
            @Override
            public boolean handle(AnnotatedEntity entity, Identifier id) {
                final Annotation annotation = annotations.getCrossReference(id);
                edit.addEdit(new AddAnnotationEdit(entity, annotation));
                entity.addAnnotation(annotation);
                return true;
            }
        };

        IdentifierMapper<String> mapper
                = new IdentifierMapper<String>(recon.getMetabolome(),
                                               accessor,
                                               handler,
                                               DefaultIdentifierFactory
                                                       .getInstance());


        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(selected),
                                   separatingChar(),
                                   '\0');
            String[] row = null;

            if (infer.isSelected()) {
                while ((row = reader.readNext()) != null) {
                    if (row.length == 2) {
                        mapper.map(row[0], row[1]);
                    }
                }
            } else if (single.isSelected()) {
                Identifier identifier = ((Identifier) resource
                        .getSelectedItem()).newInstance();
                while ((row = reader.readNext()) != null) {
                    if (row.length == 2) {
                        mapper.map(row[0], row[1], identifier
                                .getShortDescription());
                    }
                }
            } else if (mapped.isSelected()) {
                while ((row = reader.readNext()) != null) {
                    if (row.length == 3) {
                        mapper.map(row[0], row[1], row[2]);
                    }
                }
            }


        } catch (IOException e) {
            report(new ErrorMessage("unable to map identifiers: " + e
                    .getMessage()));
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                // ignored
            }
        }


        // finished editing
        edit.end();
        addEdit(edit);

        // report problems
        if (!mapper.ambiguous().isEmpty()) {
            addReport(new WarningMessage("The following accessions were ambiguous and could not be mapped: " + Joiner
                    .on(", ").join(mapper.ambiguous())));
        }
        if (!mapper.unknown().isEmpty()) {
            addReport(new WarningMessage("The following resouce names were unknown: " + Joiner
                    .on(", ").join(mapper.unknown())));
        }
        if (!mapper.invalid().isEmpty()) {
            addReport(new WarningMessage("The following accessions were invalid: " + Joiner
                    .on(", ").join(mapper.invalid())));
        }
        if (!mapper.unmapped().isEmpty()) {
            addReport(new WarningMessage("Unable to find entities matching: " + Joiner
                    .on(", ").join(mapper.unmapped())));
        }

    }
}
