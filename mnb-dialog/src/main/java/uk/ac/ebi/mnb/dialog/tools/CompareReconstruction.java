/**
 * CompareReconstruction.java
 *
 * 2011.11.28
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.dialog.tools;

import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.VennDiagram;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import javax.media.jai.JAI;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.core.tools.ReconstructionComparison;
import uk.ac.ebi.core.tools.hash.seeds.AtomSeed;
import uk.ac.ebi.core.tools.hash.seeds.AtomicNumberSeed;
import uk.ac.ebi.core.tools.hash.seeds.BondOrderSumSeed;
import uk.ac.ebi.core.tools.hash.seeds.ChargeSeed;
import uk.ac.ebi.core.tools.hash.seeds.ConnectedAtomSeed;
import uk.ac.ebi.core.tools.hash.seeds.SeedFactory;
import uk.ac.ebi.core.tools.hash.seeds.StereoSeed;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.chemet.render.ViewUtilities;
import uk.ac.ebi.render.molecule.MetaboliteComparison;


/**
 * CompareReconstruction - 2011.11.28 <br> Class description
 *
 * @version $Rev$ : Last Changed $Date: 2011-12-15 22:07:54 +0000 (Thu,
 * 15 Dec 2011) $
 * @author johnmay
 * @author $Author$ (this version)
 */
public class CompareReconstruction
        extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(CompareReconstruction.class);

    private JComboBox recon1 = ComboBoxFactory.newComboBox();

    private JComboBox recon2 = ComboBoxFactory.newComboBox();

    private JComboBox recon3 = ComboBoxFactory.newComboBox();

    private VennDiagram venn;
    // options

    private JCheckBox hydrogen = CheckBoxFactory.newCheckBox("hydrogens");

    private JCheckBox charge = CheckBoxFactory.newCheckBox("charge");

    private JCheckBox stereo = CheckBoxFactory.newCheckBox("stereochemical bonds");
    // output

    private JTextArea output = new JTextArea(7, 40);

    private JLabel label = new JLabel();


    public CompareReconstruction(JFrame frame,
                                 TargetedUpdate updater,
                                 ReportManager messages,
                                 SelectionController controller,
                                 UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");
        setDefaultLayout();
    }


    @Override
    public JPanel getOptions() {

        JPanel panel = super.getOptions();

        CellConstraints cc = new CellConstraints();

        panel.setLayout(new FormLayout("p, 4dlu, p, 4dlu, p",
                                       ViewUtilities.goodiesFormHelper(6, 4, false)));
        panel.add(LabelFactory.newLabel("Reconstructions"), cc.xyw(1, 1, 5));
        panel.add(recon1, cc.xy(1, 3));
        panel.add(recon2, cc.xy(3, 3));
        panel.add(recon3, cc.xy(5, 3));
        panel.add(LabelFactory.newLabel("Include"), cc.xyw(1, 5, 5));
        panel.add(hydrogen, cc.xyw(1, 7, 5));
        panel.add(stereo, cc.xyw(1, 9, 5));
        panel.add(charge, cc.xyw(1, 11, 5));

        return panel;

    }


    @Override
    public void setVisible(boolean visible) {

        recon1.setModel(new DefaultComboBoxModel(ReconstructionManager.getInstance().getProjects().toArray()));
        recon2.setModel(new DefaultComboBoxModel(ReconstructionManager.getInstance().getProjects().toArray()));
        recon3.setModel(new DefaultComboBoxModel(ReconstructionManager.getInstance().getProjects().toArray()));

        ((DefaultComboBoxModel) recon3.getModel()).addElement("-");
        recon3.setSelectedItem("-");

        super.setVisible(visible);

    }


    @Override
    public void process() {

        Reconstruction reconA = (Reconstruction) recon1.getSelectedItem();
        Reconstruction reconB = (Reconstruction) recon2.getSelectedItem();
        Reconstruction reconC = (Reconstruction) (recon3.getSelectedItem() == "-" ? null : recon3.getSelectedItem());

        venn = null;

        Set<AtomSeed> methods = SeedFactory.getInstance().getSeeds(BondOrderSumSeed.class,
                                                                   AtomicNumberSeed.class,
                                                                   ConnectedAtomSeed.class);
        if (stereo.isSelected()) {
            methods.add(SeedFactory.getInstance().getSeed(StereoSeed.class));
        }
        if (charge.isSelected()) {
            methods.add(SeedFactory.getInstance().getSeed(ChargeSeed.class));
        }

        c = null;
        if (reconC == null) {
            c = new ReconstructionComparison(methods, hydrogen.isSelected(), reconA, reconB);

            double[] data = new double[]{
                c.getMetaboliteTotal(reconA),
                c.getMetaboliteTotal(reconB),
                c.getMetaboliteInstersect(reconA, reconB)};
            double[] copy = Arrays.copyOf(data, data.length);
            Arrays.sort(copy);
            venn = GCharts.newVennDiagram(100 * (data[0] / copy[copy.length - 1]),
                                          100 * (data[1] / copy[copy.length - 1]),
                                          0,
                                          100 * (data[2] / copy[copy.length - 1]),
                                          0,
                                          0,
                                          0);
            venn.setCircleLegends(reconA.getAccession(), reconB.getAccession(), "-");
            venn.setSize(540, 540);
            label.setText("<html>" + reconA.getAccession() + ": " + c.getMetaboliteTotal(reconA) + "<br>"
                          + reconB.getAccession() + ": " + c.getMetaboliteTotal(reconB) + "<br>"
                          + reconA.getAccession() + " ‚à© " + reconB.getAccession() + " = " + c.getMetaboliteInstersect(
                    reconA, reconB) + "<br></html>");
//            output.setText(
//                    reconA.getAccession() + ": " + c.getMetaboliteTotal(reconA) + "\n"
//                    + reconB.getAccession() + ": " + c.getMetaboliteTotal(reconB) + "\n"
//                    + reconA.getAccession() + "+" + reconB.getAccession() + ": " + c.getMetaboliteInstersect(reconA, reconB) + "\n");
        } else {
            c = new ReconstructionComparison(methods, hydrogen.isSelected(), reconA, reconB, reconC);

            int ab = c.getMetaboliteInstersect(reconA, reconB);
            int bc = c.getMetaboliteInstersect(reconB, reconC);
            int ac = c.getMetaboliteInstersect(reconA, reconC);
            int abc = c.getMetaboliteInstersect(reconA, reconB, reconC);

            double[] data = new double[]{
                c.getMetaboliteTotal(reconA),
                c.getMetaboliteTotal(reconB),
                c.getMetaboliteTotal(reconC),
                ab,
                bc,
                ac,
                abc};

            for (double d : data) {
                System.out.println(d);
            }
            double[] copy = Arrays.copyOf(data, data.length);
            Arrays.sort(copy);

            for (double d : copy) {
                System.out.println(d + ":" + d / copy[copy.length - 1]);
            }
            venn = GCharts.newVennDiagram(Math.min(100 * (data[0] / copy[copy.length - 1]), 100),
                                          Math.min(100 * (data[1] / copy[copy.length - 1]), 100),
                                          Math.min(100 * (data[2] / copy[copy.length - 1]), 100),
                                          Math.min(100 * (data[3] / copy[copy.length - 1]), 100),
                                          Math.min(100 * (data[4] / copy[copy.length - 1]), 100),
                                          Math.min(100 * (data[5] / copy[copy.length - 1]), 100),
                                          Math.min(100 * (data[6] / copy[copy.length - 1]), 100));

            venn.setCircleLegends(reconA.getAccession(), reconB.getAccession(), reconC.getAccession());

            venn.setSize(540, 540);
            label.setText("<html>" + reconA.getAccession() + ": " + c.getMetaboliteTotal(reconA) + "<br>"
                          + reconB.getAccession() + ": " + c.getMetaboliteTotal(reconB) + "<br>"
                          + reconC.getAccession() + ": " + c.getMetaboliteTotal(reconC) + "<br>"
                          + reconA.getAccession() + " and " + reconB.getAccession() + " = " + c.getMetaboliteInstersect(
                    reconA, reconB) + " "
                          + reconA.getAccession() + " and " + reconC.getAccession() + " = " + c.getMetaboliteInstersect(
                    reconA, reconC) + " "
                          + reconB.getAccession() + " and " + reconC.getAccession() + " = " + c.getMetaboliteInstersect(
                    reconB, reconC) + " "
                          + reconA.getAccession() + " and " + reconB.getAccession() + " and " + reconC.getAccession() + " = " + c.getMetaboliteInstersect(
                    reconA, reconB, reconC) + " "
                          + "<br></html>");

        }





    }

    private ReconstructionComparison c;


    @Override
    public boolean update() {

        if (c == null || venn == null) {
            return false;
        }

        try {
            final URL url = new URL(venn.toURLString());
            final MetaboliteComparison metComp = new MetaboliteComparison(c);

            BufferedImage img = ViewUtilities.convertRenderedImage(JAI.create("url", url));
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLayout(new FormLayout("p", "p, 4dlu, p, 4dlu,  p, 4dlu, p"));
            CellConstraints cc = new CellConstraints();

            final JScrollPane pane = new JScrollPane();
            final JComboBox box = new JComboBox(MetaboliteComparison.TableData.values());
            pane.setViewportView(metComp.getComparisconTable(MetaboliteComparison.TableData.PRESENCE));

            box.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    JTable table = (JTable) pane.getViewport().getView();
                    int[] selections = table.getSelectedRows();
                    JTable newTable = metComp.getComparisconTable(
                            (MetaboliteComparison.TableData) box.getSelectedItem());
                    for (int i : selections) {
                        newTable.addRowSelectionInterval(i, i);
                    }
                    pane.setViewportView(newTable);
                    pane.repaint();
                    pane.revalidate();
                }
            });


            pane.setViewportView(metComp.getComparisconTable(MetaboliteComparison.TableData.PRESENCE));

            frame.add(new JLabel(new ImageIcon(img)), cc.xy(1, 1));
            frame.add(label, cc.xy(1, 3));
            frame.add(box, cc.xy(1, 5));
            frame.add(pane, cc.xy(1, 7));
            frame.pack();
            frame.setVisible(true);
        } catch (IOException ex) {
            LOGGER.info("IO Exception when reading stream");
        }


        return true;
    }
}