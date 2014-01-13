package uk.ac.ebi.metingear.tools.structure;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import uk.ac.ebi.mdk.tool.transport.AminoAcid;
import uk.ac.ebi.mdk.ui.render.table.ChemicalStructureRenderer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** @author John May */
public final class StructureTable {

    public static JTable aminoAcids() throws InvalidSmilesException {

        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());

        List<IAtomContainer> containers = new ArrayList<IAtomContainer>();

        for (AminoAcid aa : AminoAcid.values()) {
            IAtomContainer container = smipar.parseSmiles(aa.smiles());
            container.setProperty("Name",
                                  aa.structureName());
            containers.add(container);
        }

        return tableOf(containers, Arrays.asList("Name"));
    }

    public static JTable tableOf(List<IAtomContainer> containers, List<String> properties) {
        JTable table = new MyTable(createModel(containers, properties));

        table.getColumnModel().getColumn(0)
             .setCellEditor(null);
        table.getColumnModel().getColumn(0)
             .setCellRenderer(new ChemicalStructureRenderer(true));

        table.setComponentPopupMenu(new StructurePopupMenu(table, (DefaultTableModel) table.getModel()));

        table.getColumnModel().getColumn(0).setWidth(256);
        table.setRowHeight(256);

        return table;
    }

    private static TableModel createModel(List<IAtomContainer> containers, List<String> properties) {
        Object[][] data = new Object[containers.size()][properties.size() + 1];
        Object[] names = new Object[properties.size() + 1];

        names[0] = "Structure";
        for (int i = 0; i < properties.size(); i++)
            names[i + 1] = properties.get(i);

        for (int i = 0; i < containers.size(); i++) {
            data[i][0] = containers.get(i);
            for (int j = 0; j < properties.size(); j++)
                data[i][j + 1] = containers.get(i).getProperty(properties.get(j));
        }

        return new MyModel(data, names);
    }

    static final String popupLocation = "table.popupLocation";

    private static final class MyTable extends JTable {
        private MyTable(TableModel dm) {
            super(dm);
        }

        @Override public Point getPopupLocation(MouseEvent event) {
            // event may be null if triggered by keyboard, f.i.
            // thanks to @Mad for the heads up!
            ((JComponent) event.getComponent()).putClientProperty(
                    popupLocation, event != null ? event.getPoint() : null);
            return super.getPopupLocation(event);
        }
    }

    private static final class MyModel extends DefaultTableModel {
        private MyModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        @Override public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private static final class StructurePopupMenu extends JPopupMenu {

        private final JTable            table;
        private final DefaultTableModel model;

        private StructurePopupMenu(final JTable table, final DefaultTableModel model) {
            this.table = table;
            this.model = model;
            add(new AbstractAction("Copy as Unique SMILES") {
                @Override public void actionPerformed(ActionEvent e) {
                    StructureClipboard.copyAsUSmiles(getSelectedStructure());
                }
            });
            add(new AbstractAction("Copy as Isomeric SMILES (non-canonical)") {
                @Override public void actionPerformed(ActionEvent e) {
                    StructureClipboard.copyAsIsoSmiles(getSelectedStructure());
                }
            });

            add(new AbstractAction("Copy as InChI") {
                @Override public void actionPerformed(ActionEvent e) {
                    StructureClipboard.copyAsInChI(getSelectedStructure());
                }
            });
            add(new AbstractAction("Copy as Molfile") {
                @Override public void actionPerformed(ActionEvent e) {
                    StructureClipboard.copyAsMolfile(getSelectedStructure());
                }
            });
            JMenu menu = new JMenu("Copy as PNG");
            menu.add(new AbstractAction("256x256") {
                @Override public void actionPerformed(ActionEvent e) {
                    StructureClipboard.copyAsPng(getSelectedStructure(), 256);
                }
            });
            menu.add(new AbstractAction("512x512") {
                @Override public void actionPerformed(ActionEvent e) {
                    StructureClipboard.copyAsPng(getSelectedStructure(), 512);
                }
            });
            add(menu);
        }

        private IAtomContainer getSelectedStructure() {
            Point p = (Point) table.getClientProperty(popupLocation);
            if (p != null) { // popup triggered by mouse
                int row = table.rowAtPoint(p);
                return (IAtomContainer) model.getValueAt(row, 0);
            }
            else { // popup triggered otherwise
                return null;
            }
        }
    }
}
