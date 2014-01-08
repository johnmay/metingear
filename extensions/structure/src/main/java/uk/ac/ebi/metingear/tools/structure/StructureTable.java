package uk.ac.ebi.metingear.tools.structure;

import org.apache.log4j.Logger;
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

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

        private final JTable            table;
        private final DefaultTableModel model;

        private StructurePopupMenu(final JTable table, final DefaultTableModel model) {
            this.table = table;
            this.model = model;
            add(new AbstractAction("Copy as Unique SMILES") {
                @Override public void actionPerformed(ActionEvent e) {
                    IAtomContainer container = getSelectedStructure();
                    if (container == null)
                        return;
                    try {
                        setClipboardText(SmilesGenerator.unique().create(container));
                    } catch (CDKException ex) {
                        Logger.getLogger(getClass()).error(ex);
                    }
                }
            });
            add(new AbstractAction("Copy as Isomeric SMILES (non-canonical)") {
                @Override public void actionPerformed(ActionEvent e) {
                    IAtomContainer container = getSelectedStructure();
                    if (container == null)
                        return;
                    try {
                        setClipboardText(SmilesGenerator.isomeric().create(container));
                    } catch (CDKException ex) {
                        Logger.getLogger(getClass()).error(ex);
                    }
                }
            });

            add(new AbstractAction("Copy as InChI") {
                @Override public void actionPerformed(ActionEvent e) {
                    IAtomContainer container = getSelectedStructure();
                    if (container == null)
                        return;
                    try {
                        setClipboardText(InChIGeneratorFactory.getInstance().getInChIGenerator(container).getInchi());
                    } catch (CDKException ex) {
                        Logger.getLogger(getClass()).error(ex);
                    }
                }
            });
            add(new AbstractAction("Copy as Molfile") {
                @Override public void actionPerformed(ActionEvent e) {
                    IAtomContainer container = getSelectedStructure();
                    if (container == null)
                        return;
                    try {
                        StringWriter sw = new StringWriter();
                        MDLV2000Writer writer = new MDLV2000Writer(sw);
                        writer.write(container);
                        setClipboardText(sw.toString());
                    } catch (CDKException ex) {
                        Logger.getLogger(getClass()).error(ex);
                    }
                }
            });
            add(new AbstractAction("Save Image") {
                @Override public void actionPerformed(ActionEvent e) {
                    IAtomContainer container = getSelectedStructure();
                    if (container == null)
                        return;
                    try {
                        BufferedImage img = MoleculeRenderer.getInstance().getImage(container, 512);
                        JFileChooser jfc = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
                        jfc.setFileFilter(filter);

                        File home = new File(System.getProperty("user.home"));
                        File dskt = new File(home, "Desktop");

                        if (dskt.exists())
                            home = dskt;

                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-hh-mm-ss");
                        String formattedDate = sdf.format(date);

                        jfc.setSelectedFile(new File(home, "Metingear-" + formattedDate + ".png"));

                        int opt = jfc.showSaveDialog(table);
                        if (opt == JFileChooser.APPROVE_OPTION) {
                            File f = jfc.getSelectedFile();
                            String name = f.getAbsolutePath();
                            String extenstion = name.substring(name.lastIndexOf('.') + 1);
                            if (!extenstion.equals("png"))
                                name += ".png";
                            if (f.exists()) {
                                if (JOptionPane.CANCEL_OPTION == JOptionPane.showConfirmDialog(table, "The file " + name + " already exists - would you like to overwrite it?"))
                                    return;
                            }
                            ImageIO.write(img, "png", new File(name));
                        }
                    } catch (CDKException ex) {
                        Logger.getLogger(getClass()).error(ex);
                    } catch (IOException e1) {
                        Logger.getLogger(getClass()).error(e1);
                    }
                }
            });
        }

        private void setClipboardText(String text) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
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
