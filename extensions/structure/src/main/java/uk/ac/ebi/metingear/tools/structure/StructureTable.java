package uk.ac.ebi.metingear.tools.structure;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import uk.ac.ebi.mdk.tool.transport.AminoAcid;
import uk.ac.ebi.mdk.ui.render.molecule.MoleculeRenderer;
import uk.ac.ebi.mdk.ui.render.table.ChemicalStructureRenderer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
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
             .setCellRenderer(new ChemicalStructureRenderer());

        table.setComponentPopupMenu(new StructurePopupMenu(table, (DefaultTableModel) table.getModel()));

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
        private StructurePopupMenu(final JTable table, final DefaultTableModel model) {


            add(new AbstractAction("Copy as Unique SMILES") {
                @Override public void actionPerformed(ActionEvent e) {
                    Point p = (Point) table.getClientProperty(popupLocation);
                    if (p != null) { // popup triggered by mouse
                        int row = table.rowAtPoint(p);
                        IAtomContainer container = (IAtomContainer) model.getValueAt(row, 0);
                        try {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(SmilesGenerator.unique().create(container)), null);
                        } catch (CDKException ex) {

                        }
                    }
                    else { // popup triggered otherwise

                    }
                }
            });
            add(new AbstractAction("Copy as Isomeric SMILES (non-canonical)") {
                @Override public void actionPerformed(ActionEvent e) {
                    Point p = (Point) table.getClientProperty(popupLocation);
                    if (p != null) { // popup triggered by mouse
                        int row = table.rowAtPoint(p);
                        IAtomContainer container = (IAtomContainer) model.getValueAt(row, 0);
                        try {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(SmilesGenerator.isomeric().create(container)), null);
                        } catch (CDKException ex) {

                        }
                    }
                    else { // popup triggered otherwise

                    }
                }
            });
            add(new AbstractAction("Copy as InChI") {
                @Override public void actionPerformed(ActionEvent e) {
                    Point p = (Point) table.getClientProperty(popupLocation);
                    if (p != null) { // popup triggered by mouse
                        int row = table.rowAtPoint(p);
                        IAtomContainer container = (IAtomContainer) model.getValueAt(row, 0);
                        try {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(InChIGeneratorFactory.getInstance().getInChIGenerator(container).getInchi()), null);
                        } catch (CDKException ex) {

                        }
                    }
                    else { // popup triggered otherwise

                    }
                }
            });
            add(new AbstractAction("Copy as Molfile") {
                @Override public void actionPerformed(ActionEvent e) {
                    Point p = (Point) table.getClientProperty(popupLocation);
                    if (p != null) { // popup triggered by mouse
                        int row = table.rowAtPoint(p);
                        IAtomContainer container = (IAtomContainer) model.getValueAt(row, 0);
                        try {
                            StringWriter sw = new StringWriter();
                            MDLV2000Writer writer = new MDLV2000Writer(sw);
                            writer.write(container);
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sw.toString()), null);
                        } catch (CDKException ex) {

                        }
                    }
                    else { // popup triggered otherwise

                    }
                }
            });             
        }
    }

    private static class TransferableImage implements Transferable {

        Image i;

        public TransferableImage(Image i) {
            this.i = i;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            }
            else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavor.equals(flavors[i])) {
                    return true;
                }
            }

            return false;
        }
    }
}
