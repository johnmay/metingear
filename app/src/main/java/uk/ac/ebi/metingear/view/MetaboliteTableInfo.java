/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
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

package uk.ac.ebi.metingear.view;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import uk.ac.ebi.caf.utility.font.EBIIcon;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.annotation.primitive.DoubleAnnotation;
import uk.ac.ebi.mdk.domain.annotation.primitive.FloatAnnotation;
import uk.ac.ebi.mdk.domain.annotation.primitive.StringAnnotation;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.ui.edit.crossreference.IdentifierEditor;
import uk.ac.ebi.mdk.ui.edit.table.RatingCellEditor;
import uk.ac.ebi.mdk.ui.render.table.RatingCellRenderer;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Display information on a metabolite.
 *
 * @author John May
 */
public final class MetaboliteTableInfo {

    private final View                 view;
    private final UndoableEditListener editListener;

    public MetaboliteTableInfo(UndoableEditListener editListener) {
        this.view = new View(new MetaboliteInfoModel(null, editListener, Collections.<MetabolicReaction>emptyList()));
        this.editListener = editListener;
    }

    public void addTableModelListener(TableModelListener tml) {
        view.tableView.addTableModelListener(tml);
    }

    public JComponent component() {
        return view.component;
    }

    public void setMtbl(Metabolite mtbl, List<MetabolicReaction> reactions) {
        view.setModel(new MetaboliteInfoModel(mtbl, editListener, reactions));
    }

    private static final class View {

        private final JScrollPane component;
        private final TableView   tableView;

        private View(final MetaboliteInfoModel model) {
            this.tableView = new TableView(model);
            this.component = new JScrollPane();

            this.tableView.setPreferredScrollableViewportSize(component.getPreferredSize());

            this.component.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            this.component.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            this.component.setViewportView(tableView);
            this.component.setOpaque(false);
            this.component.getViewport().setOpaque(false);
            this.component.setViewportBorder(new MyTableBorder(component.getViewport(),
                                                               tableView));
        }

        private void setModel(MetaboliteInfoModel model) {
            tableView.setModel(model);
            tableView.xrefRenderer.editing = -1;
        }
    }

    private static final class TableView extends JTable {

        private final FieldTypeRenderer        typeRenderer         = new FieldTypeRenderer();
        private final CrossReferenceEditor     xrefRenderer         = new CrossReferenceEditor();
        private final BasicValueRenderer       valueRenderer        = new BasicValueRenderer();
        private final NameRenderer             nameRenderer         = new NameRenderer();
        private final FormulaRenderer          formulaRenderer      = new FormulaRenderer();
        private final BasicValueRenderer       valueMonoRenderer    = new BasicValueRenderer();
        private final BasicValueRenderer       lineNotationRenderer = new BasicValueRenderer();
        private final BasicValueEditor         valueEditor          = new BasicValueEditor();
        private final BasicValueEditor         lineNotationSelector = new BasicValueEditor();
        private final AnnotationEditor         annotationEditor     = new AnnotationEditor();
        private final ReactionListRenderer     reactionRender       = new ReactionListRenderer();
        private final RatingCellRenderer       ratingCellRenderer   = new RatingCellRenderer();
        private final RatingCellEditor         ratingCellEditor     = new RatingCellEditor();
        private       List<TableModelListener> listeners            = new ArrayList<TableModelListener>();
        private MetaboliteInfoModel model;

        private TableView(final MetaboliteInfoModel localModel) {
            super(localModel);
            this.model = localModel;
            
            // set mono-spaced fonts
            this.valueMonoRenderer.label.setFont(new Font("Cousine", Font.BOLD, 12));
            this.lineNotationRenderer.label.setFont(new Font("Cousine", Font.PLAIN, 12));
            this.lineNotationSelector.field.setEditable(false);
            this.lineNotationSelector.field.setFont(new Font("Cousine", Font.PLAIN, 12));
            this.lineNotationSelector.field.setBackground(Color.WHITE);

            // style
            setIntercellSpacing(new Dimension(0, 0));
            setShowGrid(false);
            setTableHeader(null);

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int rowIndex = rowAtPoint(e.getPoint());
                    if (model.typeOf(rowIndex) == MetaboliteInfoModel.Type.Reactions) {
                        editCellAt(rowIndex, 1);
                    }
                    else if (model.typeOf(rowIndex) == MetaboliteInfoModel.Type.Xref) {
                        if (xrefRenderer.editing != rowIndex)
                            editCellAt(rowIndex, 1);
                    }
                    else {
                        xrefRenderer.stopCellEditing();
                    }
                }
            });

            addTableModelListener(new TableModelListener() {
                @Override public void tableChanged(TableModelEvent e) {
//                updateDimensions();
                }
            });
        }

        final void addTableModelListener(TableModelListener listener) {
            if (listeners == null)
                return;
            listeners.add(listener);
            for (TableModelListener l : listeners)
                model.removeTableModelListener(l);
            for (TableModelListener l : listeners)
                model.addTableModelListener(l);
        }

        @Override public TableCellRenderer getCellRenderer(int row, int column) {
            if (column == 0)
                return typeRenderer;
            switch (model.typeOf(row)) {
                case Name:
                    return nameRenderer;
                case Confidence:
                    return ratingCellRenderer;
                case Reactions:
                    return reactionRender;
                case Formula:
                    return formulaRenderer;
                case Xref:
                    return xrefRenderer;
                case Abbreviation:
                    return valueMonoRenderer;
                case LineNotation:
                    return lineNotationRenderer;
                default:
                    return valueRenderer;
            }
        }

        @Override public TableCellEditor getCellEditor(int row, int column) {
            switch (model.typeOf(row)) {
                case Confidence:
                    return ratingCellEditor;
                case Reactions:
                    return reactionRender;
                case Formula:
                    return formulaRenderer;
                case EditableAnnotation:
                    return annotationEditor;
                case Xref:
                    return xrefRenderer;
                case LineNotation:
                    return lineNotationSelector;
                default:
                    return valueEditor;
            }
        }

        @Override public void setModel(TableModel model) {
            if (listeners != null) {
                for (TableModelListener l : listeners) {
                    this.model.removeTableModelListener(l);
                }
            }
            super.setModel(model);
            this.model = (MetaboliteInfoModel) model;
            if (listeners != null) {
                for (TableModelListener l : listeners) {
                    this.model.addTableModelListener(l);
                }
            }
            updateDimensions(this);
        }

        @Override public void removeEditor() {

            TableCellEditor editor = getCellEditor();
            // must be called here to remove the editor and to avoid an inifinite
            // loop, because the table is an editor listener and the
            // editingCanceled method calls this removeEditor method
            super.removeEditor();
            if (editor != null) {
                editor.cancelCellEditing();
            }
        }

        private void updateDimensions() {
            final JTable thisTable = this;
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    updateDimensions(thisTable);
                    thisTable.getParent().getParent().repaint();
                }
            });
        }

        private static void updateDimensions(JTable tbl) {
            int maxCol0Width = 0;
            try {
                for (int row = 0; row < tbl.getRowCount(); row++) {

                    maxCol0Width = Math.max(tbl.prepareRenderer(tbl.getCellRenderer(row, 0), row, 0).getPreferredSize().width,
                                            maxCol0Width);

                    int rowHeight = Math.max(tbl.prepareRenderer(tbl.getCellRenderer(row, 0), row, 0).getPreferredSize().height,
                                             tbl.prepareRenderer(tbl.getCellRenderer(row, 1), row, 1).getPreferredSize().height);

                    if (tbl.isCellEditable(row, 1)) {
                        rowHeight = Math.max(tbl.prepareEditor(tbl.getCellEditor(row, 1), row, 1).getPreferredSize().height,
                                             rowHeight);
                    }

                    tbl.setRowHeight(row, rowHeight);

                }
            } catch (ClassCastException e) {
                System.out.println(e.getMessage());
            }


            tbl.getColumnModel().getColumn(0).setMaxWidth(maxCol0Width);
            tbl.getColumnModel().getColumn(0).setMinWidth(maxCol0Width);
            tbl.getColumnModel().getColumn(0).setPreferredWidth(maxCol0Width);
        }
    }

    private static final class FieldTypeRenderer implements TableCellRenderer {

        private JLabel label = new JLabel();

        private FieldTypeRenderer() {
            label.setOpaque(true);
            label.setBackground(new Color(0xe2e9f6));
            label.setForeground(new Color(0x444444));
            label.setVerticalAlignment(SwingConstants.TOP);
            label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            label.setFont(new Font("Verdana", Font.BOLD, 12));
        }

        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            label.setText(value.toString());
            return label;
        }
    }

    private static final class BasicValueRenderer implements TableCellRenderer {

        private JLabel label = new JLabel();

        private BasicValueRenderer() {
            label.setForeground(new Color(0x444444));
            label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            label.setFont(new Font("Verdana", Font.PLAIN, 12));
        }

        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            label.setText(value.toString());
            label.setToolTipText(value.toString());
            return label;
        }
    }

    private static final class NameRenderer implements TableCellRenderer {

        private JLabel label = new JLabel();

        private NameRenderer() {
            label.setForeground(new Color(0x444444));
            label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            label.setFont(new Font("Verdana", Font.PLAIN, 12));
        }

        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value == null) value = "";
            label.setText(ChemicalNameHtmlStyler.styleHtml(value.toString()));
            label.setToolTipText(value.toString());
            return label;
        }
    }

    private static final class FormulaRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

        private JLabel     label = new JLabel();
        private JTextField field = new JTextField();

        private FormulaRenderer() {
            label.setForeground(new Color(0x444444));
            label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            label.setFont(new Font("Verdana", Font.PLAIN, 12));
            field.setOpaque(true);
            field.setBackground(new Color(0xCBFFC8));
            field.setForeground(new Color(0x444444));
            field.setFont(new Font("Verdana", Font.PLAIN, 12));
            field.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        }

        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            IMolecularFormula formula = (IMolecularFormula) value;
            String content = "";
            if (formula != null) {
                content = formulaAsHTML(formula);
            }
            label.setText(content);
            return label;
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            IMolecularFormula formula = (IMolecularFormula) value;
            String content = "";
            if (formula != null) {
                content = formulaAsASCII(formula);
            }
            field.setText(content);
            return field;
        }

        @Override public Object getCellEditorValue() {
            return field.getText();
        }

        private static String formulaAsHTML(IMolecularFormula formula) {
            String html = MolecularFormulaManipulator.getHTML(formula, false, false);
            html = html.replaceAll("<sub>1</sub>", ""); // CH4 not C1H4, patched in future CDK release
            Integer chg = formula.getCharge();
            if (chg != null && chg != 0) {
                if (formula.getIsotopeCount() > 1)
                    html = "[" + html + "]";
                String symbol = chg > 0 ? "+" : "–";
                String mag = Math.abs(chg) > 1 ? Integer.toString(Math.abs(chg)) : "";
                html += "<sup>" + mag + symbol + "</sup>";
            }
            return "<html>" + html + "</html>";
        }

        private static String formulaAsASCII(IMolecularFormula formula) {
            String ascii = MolecularFormulaManipulator.getString(formula);
            Integer chg = formula.getCharge();
            if (chg != null && chg != 0) {
                if (formula.getIsotopeCount() > 1)
                    ascii = "[" + ascii + "]";
                String symbol = chg > 0 ? "+" : "–";
                String mag = Math.abs(chg) > 1 ? Integer.toString(Math.abs(chg)) : "";
                ascii += mag + symbol;
            }
            return ascii;
        }
    }

    private static final class AnnotationEditor extends AbstractCellEditor implements TableCellEditor {

        private final JTextField field      = new JTextField();
        private       Annotation annotation = null;

        private AnnotationEditor() {
            field.setOpaque(true);
            field.setBackground(new Color(0xCBFFC8));
            field.setForeground(new Color(0x444444));
            field.setFont(new Font("Verdana", Font.PLAIN, 12));
            field.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            field.setText(value.toString());
            annotation = (Annotation) value;
            return field;
        }

        @Override public Object getCellEditorValue() {

            String content = field.getText().trim();

            if (annotation == null || content.isEmpty())
                return null;

            String oldContent = annotation.toString();
            String newContent = content;

            if (oldContent.equals(newContent))
                return annotation;

            Annotation newAnnotation = annotation.newInstance();
            if (newAnnotation instanceof StringAnnotation) {
                ((StringAnnotation) newAnnotation).setValue(content);
            }
            else if (newAnnotation instanceof DoubleAnnotation) {
                try {
                    ((DoubleAnnotation) newAnnotation).setValue(Double.parseDouble(content));
                } catch (NumberFormatException e) {
                    return annotation;
                }
            }
            else if (newAnnotation instanceof FloatAnnotation) {
                try {
                    ((FloatAnnotation) newAnnotation).setValue(Float.parseFloat(content));
                } catch (NumberFormatException e) {
                    return annotation;
                }
            }
            return newAnnotation;
        }
    }


    private static final class ReactionListRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

        private JScrollPane pane = new JScrollPane();
        private JList       list = new JList();
        int selectedIndex = -1;

        private ReactionListRenderer() {
//            list.setForeground(new Color(0x444444));
            list.setBorder(BorderFactory.createEmptyBorder());
            list.setLayoutOrientation(JList.VERTICAL);
            list.addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    final int x = e.getX();
                    final int y = e.getY();
                    // only display a hand if the cursor is over the items
                    final Rectangle cellBounds = list.getCellBounds(0, list.getModel().getSize() - 1);
                    if (cellBounds != null && cellBounds.contains(x, y)) {
                        list.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        selectedIndex = list.locationToIndex(e.getPoint());
                        list.repaint();
                    }
                    else {
                        list.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        selectedIndex = -1;
                        list.repaint();
                    }
                }
            });
            list.addMouseListener(new MouseAdapter() {
                @Override public void mouseExited(MouseEvent e) {
                    selectedIndex = -1;
                    list.repaint();
                }
            });
            pane.setViewportView(list);
            pane.setOpaque(false);
            pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            pane.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            final JLabel selectedLabel = new JLabel();
            final JLabel defaultLabel = new JLabel();
            defaultLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            selectedLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            defaultLabel.setFont(new Font("Cousine", Font.PLAIN, 12));
            selectedLabel.setFont(new Font("Cousine", Font.PLAIN, 12));
            defaultLabel.setForeground(new Color(0x444444));
            selectedLabel.setForeground(new Color(0x444444));
            Map atrbs = selectedLabel.getFont().getAttributes();
            atrbs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            selectedLabel.setFont(selectedLabel.getFont().deriveFont(atrbs));
            list.setCellRenderer(new ListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    MetabolicReaction rxn = (MetabolicReaction) value;
                    String text = rxn.getAbbreviation() + ": " + buildSide(rxn.getReactants()) + " " + rxn.getDirection().toString() + " " + buildSide(rxn.getProducts());
                    JLabel label = index == selectedIndex ? selectedLabel : defaultLabel;
                    label.setText(text);
                    label.setToolTipText(text);
                    return label;
                }

                String buildSide(List<MetabolicParticipant> participants) {
                    StringBuilder sb = new StringBuilder();
                    for (MetabolicParticipant p : participants) {
                        if (sb.length() > 0)
                            sb.append(" + ");
                        if (p.getCoefficient() != 1)
                            sb.append(p.getCoefficient()).append(" ");
                        if (p.getMolecule().getAbbreviation().isEmpty())
                            sb.append("??");
                        sb.append(p.getMolecule().getAbbreviation());
                        // todo default compartment
                        if (p.getCompartment() != Organelle.CYTOPLASM)
                            sb.append(" [").append(p.getCompartment().getAbbreviation()).append("]");
                    }
                    return sb.toString();
                }
            });
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override public void valueChanged(ListSelectionEvent e) {
                    MetabolicReaction reaction = (MetabolicReaction) list.getSelectedValue();
                    if (reaction == null)
                        return;
                    MainController mainController = MainView.getInstance();
                    ViewController viewController = mainController.getViewController();
                    EntityCollection manager = viewController.getSelection();
                    manager.clear().add(reaction);
                    viewController.setSelection(manager);
                }
            });
        }

        @SuppressWarnings("unchecked")
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            List<MetabolicReaction> reactions = (List<MetabolicReaction>) value;
            DefaultListModel model = new DefaultListModel();
            for (MetabolicReaction reaction : reactions)
                model.addElement(reaction);
            list.setVisibleRowCount(Math.min(reactions.size(), 5));
            list.setModel(model);
            return pane;
        }

        @SuppressWarnings("unchecked")
        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            List<MetabolicReaction> reactions = (List<MetabolicReaction>) value;
            DefaultListModel model = new DefaultListModel();
            for (MetabolicReaction reaction : reactions)
                model.addElement(reaction);
            list.setVisibleRowCount(Math.min(reactions.size(), 5));
            list.setModel(model);
            return pane;
        }

        @Override public Object getCellEditorValue() {
            return null;
        }
    }

    private static final class BasicValueEditor extends AbstractCellEditor implements TableCellEditor {

        private final JTextField field = new JTextField();

        private BasicValueEditor() {
            field.setOpaque(true);
            field.setBackground(new Color(0xCBFFC8));
            field.setForeground(new Color(0x444444));
            field.setFont(new Font("Verdana", Font.PLAIN, 12));
            field.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            field.setText(value.toString());
            return field;
        }

        @Override public Object getCellEditorValue() {
            return field.getText().trim();
        }
    }

    private static final class CrossReferenceEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

        private final JLabel           defaultLabel  = new JLabel();
        private final JLabel           selectedLabel = new JLabel();
        private final JButton          editButton    = new JButton();
        private final IdentifierEditor idEditor      = new IdentifierEditor();
        private final JPanel           cardPanel     = new JPanel();
        private final CardLayout       layout        = new CardLayout();
        private CrossReference xref;

        private int editing = -1;

        Color fg = new Color(0x444444);

        private CrossReferenceEditor() {
            editButton.setBorder(BorderFactory.createEmptyBorder());
            editButton.setForeground(fg);
            editButton.setOpaque(false);
            editButton.setIcon(EBIIcon.EDIT.create().size(11).color(fg).icon());
            editButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    layout.show(cardPanel, "edit");
                }
            });
            editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            defaultLabel.setForeground(fg);
            defaultLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            defaultLabel.setVerticalAlignment(SwingConstants.CENTER);
            selectedLabel.setForeground(fg);
            selectedLabel.setBorder(BorderFactory.createEmptyBorder());
            selectedLabel.setVerticalAlignment(SwingConstants.CENTER);

            Font font = new Font("Cousine", Font.PLAIN, 12);
            defaultLabel.setFont(font);
            Map atrbs = font.getAttributes();
            atrbs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            selectedLabel.setFont(font.deriveFont(atrbs));
            selectedLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            selectedLabel.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    try {
                        URL url = xref.getIdentifier().getURL();
                        Desktop.getDesktop().browse(url.toURI());
                    } catch (IOException e1) {
                        Logger.getLogger(getClass()).error(e1);
                    } catch (URISyntaxException e1) {
                        Logger.getLogger(getClass()).error(e1);
                    }
                }
            });

            Box viewPanel = Box.createHorizontalBox();
            viewPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            viewPanel.add(selectedLabel);
            viewPanel.add(Box.createHorizontalStrut(15));
            viewPanel.add(editButton);
            viewPanel.add(Box.createHorizontalGlue());

            idEditor.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

            cardPanel.setLayout(layout);
            cardPanel.setBorder(BorderFactory.createEmptyBorder());
            cardPanel.setOpaque(false);
            cardPanel.add(viewPanel, "view");
            cardPanel.add(idEditor, "edit");
            layout.show(cardPanel, "view");
        }

        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            defaultLabel.setText(value.toString());
            return defaultLabel;
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editing = row;
            xref = (CrossReference) value;
            selectedLabel.setText(xref.toString());
            idEditor.setIdentifier(xref.getIdentifier());
            return cardPanel;
        }

        @Override public Object getCellEditorValue() {
            Identifier identifier = idEditor.getIdentifier();

            // trigger deletion
            if (identifier == null || identifier.getAccession().trim().isEmpty())
                return null;

            // no change made by editor
            if (xref.getIdentifier() == identifier)
                return xref;

            return new CrossReference(identifier);
        }

        @Override public void cancelCellEditing() {
            editing = -1;
            layout.show(cardPanel, "view");
            super.cancelCellEditing();
        }

        @Override public boolean stopCellEditing() {
            editing = -1;
            layout.show(cardPanel, "view");
            return super.stopCellEditing();
        }
    }

    private static final class MyTableBorder extends AbstractBorder {

        private final JViewport vpt;
        private final JTable    tbl;

        public MyTableBorder(JViewport vpt, JTable tbl) {
            this.tbl = tbl;
            this.vpt = vpt;
        }

        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Rectangle clip = g.getClipBounds();
            g.setColor(new Color(0xE2E9F6));
            g.fillRect(clip.x, 0, tbl.getColumnModel().getColumn(0).getWidth(), clip.height);
        }
    }

}
