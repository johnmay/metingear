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

import de.erichseifert.vectorgraphics2d.EPSGraphics2D;
import de.erichseifert.vectorgraphics2d.PDFGraphics2D;
import net.sf.jniinchi.INCHI_RET;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import uk.ac.ebi.caf.utility.font.EBIIcon;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.ui.render.molecule.AtomContainerIcon;
import uk.ac.ebi.mdk.ui.render.molecule.Coloring;
import uk.ac.ebi.metingear.AppliableEdit;
import uk.ac.ebi.metingear.util.JChemPaintEditor;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;
import uk.ac.ebi.mnb.edit.RemoveAnnotationEdit;
import uk.ac.ebi.mnb.edit.ReplaceAnnotationEdit;
import uk.ac.ebi.mnb.interfaces.StructureEditor;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A self contained widget for viewing and editing structure annotations.
 *
 * @author John May
 */
final class StructureViewWidget {

    private final View       view;
    private final Controller controller;

    StructureViewWidget(UndoableEditListener undoableEditListener) {
        this.controller = new Controller(new Model(null,
                                                   Collections.<ChemicalStructure>emptyList()),
                                         undoableEditListener
        );
        this.view = new View(controller);
        this.controller.addView(view);
    }

    /**
     * Access the Swing component.
     *
     * @return swing component
     */
    JComponent component() {
        return view.component;
    }

    /**
     * Set the metabolite to view.
     *
     * @param mtbl a metabolite
     */
    void setMtbl(Metabolite mtbl) {
        this.controller.setModel(Model.createForMetabolite(mtbl));
    }

    /**
     * Create a SMILES string for the given container.
     *
     * @param container CDK structure
     * @return SMILES string
     */
    private static String toSmi(IAtomContainer container) {
        try {
            return SmilesGenerator.isomeric().create(container);
        } catch (CDKException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private static final class Model {
        private final List<ChemicalStructure> structures;
        private final Metabolite              mtbl;
        private int position = 0;

        private Model(Metabolite mtbl, List<ChemicalStructure> structures) {
            this.mtbl = mtbl;
            this.structures = structures;
        }

        int index() {
            if (structures.isEmpty()) return 0;
            return Math.abs(position % structures.size());
        }

        IAtomContainer selectedAtomContainer() {
            if (structures.isEmpty())
                return null;
            return structures.get(index()).getStructure();
        }

        ChemicalStructure selectedChemicalStructure() {
            if (structures.isEmpty())
                return null;
            return structures.get(index());
        }

        String selectedAbbreviation() {
            if (mtbl != null)
                return mtbl.getAbbreviation();
            return "unk";
        }

        void select(ChemicalStructure ann) {
            position = structures.indexOf(ann);
        }

        void add(ChemicalStructure ann) {
            structures.add(ann);
        }

        void remove(ChemicalStructure ann) {
            structures.remove(ann);
        }

        void replace(ChemicalStructure org, ChemicalStructure rep) {
            for (int i = 0; i < structures.size(); i++) {
                // deliberate ref equality
                if (structures.get(i) == org) {
                    structures.set(i, rep);
                }
            }
        }

        static Model createForMetabolite(Metabolite mtbl) {
            if (mtbl == null)
                return new Model(null, Collections.<ChemicalStructure>emptyList());

            List<ChemicalStructure> structures = new ArrayList<ChemicalStructure>(mtbl.getStructures());

            // ensure structures don't randomly flip in viewer on reload, we don't
            // need to use canonical SMILES so the regenerating as needed is fast
            // enough
            Collections.sort(structures, new Comparator<ChemicalStructure>() {
                @Override public int compare(ChemicalStructure a, ChemicalStructure b) {
                    String aSmi = toSmi(a.getStructure());
                    String bSmi = toSmi(b.getStructure());
                    if (aSmi == null && bSmi == null)
                        return 0;
                    if (aSmi == null)
                        return +1;
                    if (bSmi == null)
                        return -1;
                    return aSmi.compareTo(bSmi);
                }
            });

            return new Model(mtbl, structures);
        }
    }

    private static final class Controller {

        Model model;
        final UndoableEditListener editListener;
        final List<View>           views;

        private Controller(Model model, UndoableEditListener editListener) {
            this.model = model;
            this.editListener = editListener;
            this.views = new ArrayList<View>(2);
        }

        void setModel(Model model) {
            this.model = model;
            updateViews();
        }

        void addView(View view) {
            this.views.add(view);
        }

        void removeView(View view) {
            this.views.remove(view);
        }

        void next() {
            model.position++;
            updateViews();
        }

        void prev() {
            model.position--;
            updateViews();
        }

        void updateViews() {
            for (View view : views)
                view.update();
        }

        void remove(Metabolite mtbl, ChemicalStructure annotation) {
            RemoveAnnotationEdit edit = new RemoveAnnotationEdit(mtbl, annotation);
            editListener.undoableEditHappened(new UndoableEditEvent(this, edit));

            // apply edit
            edit.apply();
            model.remove(annotation);
        }

        void add() {
            StructureEditor editor = JChemPaintEditor.INSTANCE;
            Metabolite mtbl = model.mtbl;
            IAtomContainer newStr = editor.edit(new AtomContainer());
            if (newStr != null && newStr.getAtomCount() > 0) {
                AtomContainerAnnotation annotation = new AtomContainerAnnotation(newStr);
                AppliableEdit edit = new AddAnnotationEdit(mtbl, annotation);

                editListener.undoableEditHappened(new UndoableEditEvent(this, edit));

                // apply edits
                edit.apply();
                model.add(annotation);
                model.select(annotation);
                updateViews();
            }
        }

        void editSelected() {
            StructureEditor editor = JChemPaintEditor.INSTANCE;
            Metabolite mtbl = model.mtbl;
            ChemicalStructure orgAnn = model.selectedChemicalStructure();
            if (orgAnn == null || orgAnn.getStructure() == null)
                return;

            // create the new annotation
            IAtomContainer edited = editor.edit(orgAnn.getStructure());

            if (edited == null) {
                return;
            }
            else if (edited.getAtomCount() == 0) {
                remove(mtbl, orgAnn);
            }
            else {
                ChemicalStructure newAnn = (ChemicalStructure) orgAnn.newInstance();
                newAnn.setStructure(edited);

                AppliableEdit edit = new ReplaceAnnotationEdit(mtbl, orgAnn, newAnn);
                editListener.undoableEditHappened(new UndoableEditEvent(this, edit));

                // apply the edit + replace in the model
                edit.apply();
                model.replace(orgAnn, newAnn);
                model.select(newAnn);
            }
            updateViews();
        }

        void removeSelected() {
            if (model.selectedAtomContainer() == null)
                return;
            remove(model.mtbl, model.selectedChemicalStructure());
            updateViews();
        }

        void setClipboard(String content) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(content), null);
        }

        void copyAsMolfile() {
            IAtomContainer container = model.selectedAtomContainer();
            if (container == null) return;
            StringWriter sw = new StringWriter();
            MDLV2000Writer writer = new MDLV2000Writer(sw);
            try {
                writer.write(container);
                setClipboard(sw.toString());
            } catch (CDKException e) {
                Logger.getLogger(getClass()).error(e);
            }
        }

        void saveAsPDF(File file, Coloring coloring) {
            IAtomContainer container = model.selectedAtomContainer();
            if (container == null) return;

            if (file == null || file.isDirectory())
                return;
            if (!file.getName().endsWith(".pdf"))
                file = new File(file.getPath() + ".pdf");

            AtomContainerIcon icon = new AtomContainerIcon(container,
                                                           coloring);

            Rectangle2D rect = icon.bounds();
            rect.setRect(0, 0, rect.getWidth() * 100, rect.getHeight() * 100);
            PDFGraphics2D g2 = new PDFGraphics2D(0, 0, rect.getWidth(), rect.getHeight());
            g2.setColor(coloring.bgColor());
            g2.fill(rect);
            icon.render(g2, new Rectangle((int) rect.getWidth(), (int) rect.getHeight()));
            g2.dispose();

            String content = g2.toString();
            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
                fw.write(content);
            } catch (IOException e) {
                Logger.getLogger(getClass()).error(e);
            } finally {
                try {
                    if (fw != null) fw.close();
                } catch (IOException e) {
                    Logger.getLogger(getClass()).error(e);
                }
            }
        }

        void saveAsEPS(File file, Coloring coloring) {
            IAtomContainer container = model.selectedAtomContainer();
            if (container == null) return;

            if (file == null || file.isDirectory())
                return;
            if (!file.getName().endsWith(".eps"))
                file = new File(file.getPath() + ".eps");

            AtomContainerIcon icon = new AtomContainerIcon(container,
                                                           coloring);

            Rectangle2D rect = icon.bounds();
            rect.setRect(0, 0, rect.getWidth() * 100, rect.getHeight() * 100);
            EPSGraphics2D g2 = new EPSGraphics2D(0, 0, rect.getWidth(), rect.getHeight());
            g2.setColor(coloring.bgColor());
            g2.fill(rect);
            icon.render(g2, new Rectangle((int) rect.getWidth(), (int) rect.getHeight()));
            g2.dispose();

            String content = g2.toString();
            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
                fw.write(content);
            } catch (IOException e) {
                Logger.getLogger(getClass()).error(e);
            } finally {
                try {
                    if (fw != null) fw.close();
                } catch (IOException e) {
                    Logger.getLogger(getClass()).error(e);
                }
            }
        }

        void saveAsPNG(File file, Coloring coloring) {
            IAtomContainer container = model.selectedAtomContainer();
            if (container == null) return;

            if (file == null || file.isDirectory())
                return;
            if (!file.getName().endsWith(".png"))
                file = new File(file.getPath() + ".png");

            AtomContainerIcon icon = new AtomContainerIcon(container,
                                                           coloring);

            Rectangle2D rect = icon.bounds();
            rect.setRect(0, 0, rect.getWidth() * 100, rect.getHeight() * 100);
            BufferedImage img = new BufferedImage((int) rect.getWidth(),
                                                  (int) rect.getHeight(),
                                                  BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2 = img.createGraphics();
            g2.setColor(coloring.bgColor());
            g2.fill(rect);
            icon.render(g2, new Rectangle((int) rect.getWidth(), (int) rect.getHeight()));
            g2.dispose();

            try {
                ImageIO.write(img, "png", file);
            } catch (IOException e) {
                Logger.getLogger(getClass()).error(e);
            }
        }


        void saveAsMolfile(File file) {
            IAtomContainer container = model.selectedAtomContainer();
            if (container == null) return;

            if (file == null || file.isDirectory())
                return;
            if (!file.getName().endsWith(".mol"))
                file = new File(file.getPath() + ".mol");
            MDLV2000Writer writer = null;
            try {
                writer = new MDLV2000Writer(new FileWriter(file));
                writer.write(container);
            } catch (IOException e) {
                Logger.getLogger(getClass()).error(e);
            } catch (CDKException e) {
                Logger.getLogger(getClass()).error(e);
            } finally {

                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    Logger.getLogger(getClass()).error(e);
                }
            }
        }

        void saveAsCML(File file) {
            IAtomContainer container = model.selectedAtomContainer();
            if (container == null) return;
            if (file == null || file.isDirectory())
                return;
            if (!file.getName().endsWith(".cml"))
                file = new File(file.getPath() + ".cml");
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
                CMLWriter writer = new CMLWriter(fileWriter);
                writer.write(container);
            } catch (IOException e) {
                Logger.getLogger(getClass()).error(e);
            } catch (CDKException e) {
                Logger.getLogger(getClass()).error(e);
            } finally {
                try {
                    if (fileWriter != null)
                        fileWriter.close();
                } catch (IOException e) {
                    Logger.getLogger(getClass()).error(e);
                }
            }
        }

        void copyAsSmiles() {
            IAtomContainer container = model.selectedAtomContainer();
            if (container == null) return;
            String smi = toSmi(container);
            if (smi == null) return;
            setClipboard(smi);
        }

        void copyAsInChI() {
            IAtomContainer container = model.selectedAtomContainer();
            if (container == null) return;
            try {
                InChIGenerator igf = InChIGeneratorFactory.getInstance().getInChIGenerator(container);
                if (igf.getReturnStatus() != INCHI_RET.OKAY && igf.getReturnStatus() != INCHI_RET.WARNING)
                    return;
                String inchi = igf.getInchi();
                if (inchi == null) return;
                setClipboard(inchi);
            } catch (CDKException e) {
                System.err.println(e.getMessage());
            }

        }

        void copyAsInChIKey() {
            IAtomContainer container = model.selectedAtomContainer();
            if (container == null) return;
            try {
                InChIGenerator igf = InChIGeneratorFactory.getInstance().getInChIGenerator(container);
                if (igf.getReturnStatus() != INCHI_RET.OKAY && igf.getReturnStatus() != INCHI_RET.WARNING)
                    return;
                String inchiKey = igf.getInchiKey();
                if (inchiKey == null) return;
                setClipboard(inchiKey);
            } catch (CDKException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static final class View {

        final Controller controller;

        // UI components
        final JComponent component;
        final JLabel     selected, depiction, formula;
        final JButton tools, save, popout, zoom, prev, next;
        final JPopupMenu   toolMenu    = new JPopupMenu();
        final JPopupMenu   saveMenu    = new JPopupMenu();
        final JFileChooser fileChooser = new JFileChooser();

        // UI Options
        private Coloring coloring = Coloring.loadPreference();

        private View(final Controller controller) {
            this.controller = controller;
            this.component = Box.createVerticalBox();
            this.selected = new JLabel();
            this.formula = new JLabel();
            this.depiction = new JLabel();
            this.tools = transparentButton(null);
            this.save = transparentButton(null);
            this.popout = transparentButton(null);
            this.zoom = transparentButton(null);
            this.prev = transparentButton(null);
            this.next = transparentButton(null);

            // layout
            Box topBar = Box.createHorizontalBox();
            Box btmBar = Box.createHorizontalBox();
            JPanel depictionHolder = new JPanel();
            depictionHolder.setOpaque(false);
            depictionHolder.setLayout(new BorderLayout());

            component.add(topBar);
            component.add(depictionHolder);
            component.add(btmBar);
            depictionHolder.add(depiction);

            // layout (top bar)
            topBar.add(tools);
            topBar.add(Box.createHorizontalStrut(5));
            topBar.add(save);
            topBar.add(Box.createHorizontalGlue());
            topBar.add(selected);
            topBar.add(Box.createHorizontalGlue());
            topBar.add(popout);
            topBar.add(Box.createHorizontalStrut(5));
            topBar.add(zoom);

            // layout (bottom bar)
            btmBar.add(prev);
            btmBar.add(Box.createHorizontalGlue());
            btmBar.add(formula);
            btmBar.add(Box.createHorizontalGlue());
            btmBar.add(next);

            // styling
            setupStyle();
            selected.setFont(selected.getFont().deriveFont(Font.BOLD, 10));
            formula.setFont(formula.getFont().deriveFont(Font.BOLD, 10));
            depiction.setFont(depiction.getFont().deriveFont(Font.BOLD));
            formula.setHorizontalAlignment(SwingConstants.CENTER);
            depiction.setHorizontalAlignment(SwingConstants.CENTER);
            component.setOpaque(true);
            topBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            btmBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // tool tips
            tools.setToolTipText("Tools and Settings");
            save.setToolTipText("Export and copy structure representations");
            popout.setToolTipText("Pop out this view and uncouple from metabolite selection");
            zoom.setToolTipText("Zoom in this view and sync with metabolite selection");
            prev.setToolTipText("Select previous structure");
            next.setToolTipText("Select next structure");

            // install actions
            prev.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.prev();
                }
            });
            next.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.next();
                }
            });
            tools.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    toolMenu.show(tools, 10, 0);
                }
            });
            save.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    saveMenu.show(save, 10, 0);
                }
            });
            popout.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    final JDialog dialog = new JDialog();
                    Metabolite mtbl = controller.model.mtbl;
                    if (mtbl != null) {
                        dialog.setTitle("Structures of " + mtbl.getName() + " (" + mtbl.getAbbreviation() + ")");
                    }
                    dialog.setSize(512, 512);
                    final Controller myController = new Controller(Model.createForMetabolite(mtbl),
                                                                   controller.editListener);
                    final View zoomedView = new View(myController);
                    zoomedView.zoom.setEnabled(false);
                    zoomedView.popout.setEnabled(false);

                    myController.addView(zoomedView);

                    zoomedView.update();
                    dialog.add(zoomedView.component);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {
                            dialog.setVisible(true);
                        }
                    });
                    dialog.addComponentListener(new ComponentAdapter() {
                        @Override public void componentHidden(ComponentEvent e) {
                            myController.removeView(zoomedView);
                        }
                    });
                }
            });
            zoom.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(component));
                    dialog.setAlwaysOnTop(true);
                    Metabolite mtbl = controller.model.mtbl;
                    if (mtbl != null) {
                        dialog.setTitle("Structures of " + mtbl.getName() + " (" + mtbl.getAbbreviation() + ")");
                    }
                    dialog.setSize(512, 512);
                    final View zoomedView = new View(controller);
                    zoomedView.zoom.setEnabled(false);
                    zoomedView.popout.setEnabled(false);
                    controller.addView(zoomedView);
                    zoomedView.update();
                    dialog.add(zoomedView.component);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {
                            dialog.setVisible(true);
                        }
                    });
                    dialog.addComponentListener(new ComponentAdapter() {
                        @Override public void componentHidden(ComponentEvent e) {
                            controller.removeView(zoomedView);
                        }
                    });
                }
            });

            // popup menu actions
            toolMenu.add(new JMenuItem(new AbstractAction("Add Structure") {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.add();
                }
            }));
            toolMenu.add(new JMenuItem(new AbstractAction("Edit Structure") {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.editSelected();
                }
            }));
            toolMenu.add(new JMenuItem(new AbstractAction("Remove Structure") {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.removeSelected();
                }
            }));
            toolMenu.add(new JSeparator());
            // select view color
            JRadioButtonMenuItem bwItem = new JRadioButtonMenuItem("Black On White Coloring");
            JRadioButtonMenuItem wbItem = new JRadioButtonMenuItem("White On Black Coloring");
            JRadioButtonMenuItem cpkItem = new JRadioButtonMenuItem("CPK Coloring");
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(bwItem);
            buttonGroup.add(wbItem);
            buttonGroup.add(cpkItem);

            if (coloring == Coloring.BLACK) {
                bwItem.setSelected(true);
            }
            else if (coloring == Coloring.WHITE) {
                wbItem.setSelected(true);
            }
            else if (coloring == Coloring.CPK) {
                cpkItem.setSelected(true);
            }
            else {
                System.err.println("Unknown depiction coloring...?");
            }

            bwItem.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    coloring = Coloring.BLACK;
                    coloring.storePreference();
                    setupStyle();
                    update();
                    component.repaint();
                }
            });
            wbItem.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    coloring = Coloring.WHITE;
                    coloring.storePreference();
                    setupStyle();
                    update();
                    component.repaint();
                }
            });
            cpkItem.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    coloring = Coloring.CPK;
                    coloring.storePreference();
                    setupStyle();
                    update();
                    component.repaint();
                }
            });
            toolMenu.add(bwItem);
            toolMenu.add(wbItem);
            toolMenu.add(cpkItem);

            // save menu
            saveMenu.add(new JMenuItem(new AbstractAction("Save Image As PNG") {
                @Override public void actionPerformed(ActionEvent e) {
                    if (controller.model.selectedAtomContainer() == null)
                        return;
                    fileChooser.setSelectedFile(new File(controller.model.selectedAbbreviation() + ".png"));
                    int ret = fileChooser.showSaveDialog(component);
                    if (ret == JFileChooser.APPROVE_OPTION)
                        controller.saveAsPNG(fileChooser.getSelectedFile(), coloring);
                }
            }));
            saveMenu.add(new JMenuItem(new AbstractAction("Save Image As PDF") {
                @Override public void actionPerformed(ActionEvent e) {
                    if (controller.model.selectedAtomContainer() == null)
                        return;
                    fileChooser.setSelectedFile(new File(controller.model.selectedAbbreviation() + ".pdf"));
                    int ret = fileChooser.showSaveDialog(component);
                    if (ret == JFileChooser.APPROVE_OPTION)
                        controller.saveAsPDF(fileChooser.getSelectedFile(), coloring);
                }
            }));
            saveMenu.add(new JMenuItem(new AbstractAction("Save Image As EPS") {
                @Override public void actionPerformed(ActionEvent e) {
                    if (controller.model.selectedAtomContainer() == null)
                        return;
                    fileChooser.setSelectedFile(new File(controller.model.selectedAbbreviation() + ".eps"));
                    int ret = fileChooser.showSaveDialog(component);
                    if (ret == JFileChooser.APPROVE_OPTION)
                        controller.saveAsEPS(fileChooser.getSelectedFile(), coloring);
                }
            }));
            saveMenu.add(new JSeparator());
            saveMenu.add(new JMenuItem(new AbstractAction("Save As molfile") {
                @Override public void actionPerformed(ActionEvent e) {
                    if (controller.model.selectedAtomContainer() == null)
                        return;
                    fileChooser.setSelectedFile(new File(controller.model.selectedAbbreviation() + ".mol"));
                    int ret = fileChooser.showSaveDialog(component);
                    if (ret == JFileChooser.APPROVE_OPTION)
                        controller.saveAsMolfile(fileChooser.getSelectedFile());
                }
            }));
            saveMenu.add(new JMenuItem(new AbstractAction("Save As CML") {
                @Override public void actionPerformed(ActionEvent e) {
                    if (controller.model.selectedAtomContainer() == null)
                        return;
                    fileChooser.setSelectedFile(new File(controller.model.selectedAbbreviation() + ".cml"));
                    int ret = fileChooser.showSaveDialog(component);
                    if (ret == JFileChooser.APPROVE_OPTION)
                        controller.saveAsCML(fileChooser.getSelectedFile());
                }
            }));
            saveMenu.add(new JSeparator());
            saveMenu.add(new JMenuItem(new AbstractAction("Copy As SMILES") {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.copyAsSmiles();
                }
            }));
            saveMenu.add(new JMenuItem(new AbstractAction("Copy As InChI") {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.copyAsInChI();
                }
            }));
            saveMenu.add(new JMenuItem(new AbstractAction("Copy As InChIKey") {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.copyAsInChIKey();
                }
            }));
            saveMenu.add(new JMenuItem(new AbstractAction("Copy As molfile") {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.copyAsMolfile();
                }
            }));
        }

        void setupStyle() {
            tools.setIcon(EBIIcon.TOOL.create().color(coloring.fgColor()).icon());
            save.setIcon(EBIIcon.SAVE.create().color(coloring.fgColor()).icon());
            popout.setIcon(EBIIcon.EXTERNAL_LINK.create().color(coloring.fgColor()).icon());
            zoom.setIcon(EBIIcon.ZOOM_IN.create().color(coloring.fgColor()).icon());
            prev.setIcon(EBIIcon.PREVIOUS.create().color(coloring.fgColor()).icon());
            next.setIcon(EBIIcon.NEXT.create().color(coloring.fgColor()).icon());
            selected.setForeground(coloring.fgColor());
            depiction.setForeground(coloring.fgColor());
            formula.setForeground(coloring.fgColor());
            component.setBackground(coloring.bgColor());
        }

        void update() {
            selected.setText(selectedIndexText());
            formula.setText("");
            depiction.setText("No structure");
            depiction.setIcon(null);
            IAtomContainer container = controller.model.selectedAtomContainer();
            if (container != null) {

                // set formula
                IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula(container);
                formula.setText(formulaAsHTML(mf));
                formula.setPreferredSize(formula.getMinimumSize());

                // need to generate coordinates
                if (GeometryTools.get2DCoordinateCoverage(container) != GeometryTools.CoordinateCoverage.FULL) {
                    try {
                        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                        sdg.setMolecule(container, false);
                        sdg.setUseTemplates(false);
                        sdg.generateCoordinates();
                    } catch (CDKException e) {
                        Logger.getLogger(getClass()).error(e);
                        depiction.setIcon(EBIIcon.ALERT.create().color(coloring.fgColor()).icon());
                        depiction.setText("Diagram generation failed");
                        return;
                    }
                }

                depiction.setText(null);
                depiction.setIcon(new AtomContainerIcon(container, coloring));
            }
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

        private String selectedIndexText() {
            int idx = controller.model.index();
            if (controller.model.structures.isEmpty())
                return "0 of 0";
            return (idx + 1) + " of " + controller.model.structures.size();
        }

        private static JButton transparentButton(ImageIcon icon) {
            JButton btn = new JButton();
            btn.setIcon(icon);
            btn.setOpaque(false);
            btn.setBorder(BorderFactory.createEmptyBorder());
            return btn;
        }
    }
}   
