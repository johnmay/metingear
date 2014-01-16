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

package uk.ac.ebi.metingear.tools.structure;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.primitives.Doubles;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.Score;
import org.openscience.cdk.isomorphism.Scorer;
import org.openscience.cdk.isomorphism.StereoCompatibility;
import org.openscience.cdk.isomorphism.StructureUtil;
import org.openscience.cdk.renderer.generators.HighlightGenerator;
import org.openscience.cdk.smiles.SmilesGenerator;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.annotation.InChI;
import uk.ac.ebi.mdk.domain.annotation.SMILES;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.ui.render.list.DefaultRenderer;
import uk.ac.ebi.mdk.ui.render.molecule.MoleculeRenderer;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.view.ReconstructionChooser;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author John May */
public final class FindIdentical extends AbstractControlDialog {

    private Metabolite            metabolite              = null;
    private JLabel                selectedStructure       = new JLabel();
    private JComboBox             structureSelection      = new JComboBox();
    private JCheckBox             neutralise              = CheckBoxFactory.newCheckBox();
    private ReconstructionChooser reconstructionSelection = new ReconstructionChooser();

    @SuppressWarnings("unchecked")
    public FindIdentical(Window window) {
        super(window);
        DefaultRenderer<ChemicalStructure> renderer = new DefaultRenderer<ChemicalStructure>() {
            @Override public JLabel getComponent(JList list, ChemicalStructure value, int index) {
                setText(nameAnnotation(value));
                return this;
            }

            private String nameAnnotation(ChemicalStructure cs) {
                Class c = cs.getClass();
                if (c == AtomContainerAnnotation.class) {
                    return "CDK AtomContainer";
                }
                else if (c == SMILES.class) {
                    return "SMILES";
                }
                else if (c == InChI.class) {
                    return "InChI";
                }
                return "";
            }
        };
        structureSelection.setRenderer(renderer);
        structureSelection.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent event) {
                ChemicalStructure selected = (ChemicalStructure) structureSelection.getSelectedItem();
                if (selected == null)
                    return;
                showSelected(selected);
            }
        });
        selectedStructure.setPreferredSize(new Dimension(256, 256));
    }

    @Override public JComponent createForm() {
        JPanel panel = PanelFactory.createDialogPanel();
        panel.setLayout(new FormLayout("right:p, 4dlu, p:grow", "p, 4dlu, p, 4dlu, p, 4dlu, p"));
        panel.add(selectedStructure, new CellConstraints(1, 1, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
        panel.add(getLabel("structures"), new CellConstraints(1, 3));
        panel.add(structureSelection, new CellConstraints(3, 3));
        panel.add(getLabel("recons"), new CellConstraints(1, 5));
        panel.add(reconstructionSelection.getComponent(), new CellConstraints(3, 5));
        panel.add(getLabel("neu"), new CellConstraints(1, 7));
        panel.add(neutralise, new CellConstraints(3, 7));
        return panel;
    }

    @SuppressWarnings("unchecked")
    @Override public void prepare() {
        metabolite = getSelection(Metabolite.class).iterator().next();
        Collection<ChemicalStructure> structures = metabolite.getStructures();
        ChemicalStructure[] cs = structures.toArray(new ChemicalStructure[structures.size()]);
        structureSelection.setModel(new DefaultComboBoxModel<ChemicalStructure>(cs));
        structureSelection.setSelectedIndex(0);
        showSelected(cs[0]);
        structureSelection.setEnabled(cs.length > 1);
        pack();
    }

    @Override public void process() {
        try {

            Metabolite m = getSelection(Metabolite.class).iterator().next();
            ChemicalStructure queryStructure = ((ChemicalStructure) structureSelection.getSelectedItem());
            IAtomContainer queryContainer = queryStructure.getStructure();

            queryContainer = StructureUtil.suppressHydrogens(queryContainer);
            if (neutralise.isSelected()) {
                StructureUtil.neutralise(queryContainer);
            }

            List<IAtomContainer> structures = new ArrayList<IAtomContainer>();

            Reconstruction reference = reconstructionSelection.getSelected();

            for (Metabolite n : reference.metabolome()) {
                if (m != n) {
                    for (ChemicalStructure cs : n.getStructures()) {

                        IAtomContainer unsuppressed = cs.getStructure();
                        IAtomContainer suppressed = StructureUtil.suppressHydrogens(cs.getStructure());

                        if (neutralise.isSelected()) {
                            StructureUtil.neutralise(suppressed);
                        }
                        
                        Score score = Scorer.score(queryContainer, suppressed);
                        if (score == Score.MIN_VALUE)
                            continue;

                        unsuppressed.setProperty("Name",
                                                 n.getName());
                        unsuppressed.setProperty("Id",
                                                 n.getIdentifier());
                        unsuppressed.setProperty("Score", score);
                        unsuppressed.setProperty("Score.Value",
                                                 String.format("%.3f", score.toDouble()));
                        unsuppressed.setProperty("Stereo.Comp",
                                                 Arrays.toString(score.compatibilities()));
                        try {                           
                            unsuppressed.setProperty("SMILES",
                                                     SmilesGenerator.isomeric().create(cs.getStructure()));
                        } catch (CDKException ex) {
                            unsuppressed.setProperty("SMILES",
                                                     " " + ex.getMessage());
                        }

                        if (unsuppressed.getAtomCount() == suppressed.getAtomCount()) {
                            Map<IAtom, Integer> ids = new HashMap<IAtom, Integer>();
                            int[] mapping = score.mapping();
                            StereoCompatibility[] compatibilities = score.compatibilities();

                            for (int i = 0; i < mapping.length; i++) {
                                IAtom atom = unsuppressed.getAtom(mapping[i]);
                                switch (compatibilities[i].state()) {
                                    case Same:
                                        ids.put(atom, 0);
                                        break;
                                    case Unspecified:
                                        ids.put(atom, 1);
                                        break;
                                    case Different:
                                        ids.put(atom, 2);
                                        break;
                                }
                            }
                            unsuppressed.setProperty(HighlightGenerator.ID_MAP,
                                                     ids);
                        }
                        else if (unsuppressed.getAtomCount() > suppressed.getAtomCount()) {

                            // need substructure mapping without hydrogen to one with hydrogens
                            int[] mapping2 = Pattern.findSubstructure(suppressed)
                                                    .matchAll(unsuppressed)
                                                    .stereochemistry()
                                                    .first();

                            Map<IAtom, Integer> ids = new HashMap<IAtom, Integer>();
                            int[] mapping = score.mapping();
                            StereoCompatibility[] compatibilities = score.compatibilities();

                            for (int i = 0; i < mapping.length; i++) {
                                IAtom atom = unsuppressed.getAtom(mapping2[mapping[i]]);
                                switch (compatibilities[i].state()) {
                                    case Same:
                                        ids.put(atom, 0);
                                        break;
                                    case Unspecified:
                                        ids.put(atom, 1);
                                        break;
                                    case Different:
                                        ids.put(atom, 2);
                                        break;
                                }
                            }
                            unsuppressed.setProperty(HighlightGenerator.ID_MAP,
                                                     ids);
                        }

                        structures.add(cs.getStructure());
                    }
                }
            }

            Collections.sort(structures, new Comparator<IAtomContainer>() {
                @Override public int compare(IAtomContainer o1, IAtomContainer o2) {
                    Score s1 = o1.getProperty("Score", Score.class);
                    Score s2 = o2.getProperty("Score", Score.class);
                    return -Doubles.compare(s1.toDouble(), s2.toDouble());
                }
            });

            File f = File.createTempFile(m.getName(), ".csv");
            CSVWriter csv = new CSVWriter(new FileWriter(f), ',', '\0', '\0');
            for (IAtomContainer structure : structures) {
                String smi = structure.getProperty("SMILES");
                String name = structure.getProperty("Name");
                Score score = structure.getProperty("Score");
                csv.writeNext(new String[]{
                        String.format("%80s", smi),
                        String.format("%30s", name),
                        score.toString(),
                        String.format("%.2f", score.toDouble())
                });
            }
            csv.close();
            System.out.println("writen to: \n" + f.getAbsolutePath());

            final JFrame frame = new JFrame();
            JTable table = StructureTable.tableOf(structures, Arrays.asList("Name", "Id", "Score.Value"));
            table.setSelectionBackground(new Color(0xCCCCCC));
            table.setSelectionForeground(new Color(0x444444));
            BufferedImage img = MoleculeRenderer.getInstance().getImage(queryStructure.getStructure(), new Rectangle(0, 0, 256, 256));
            frame.add(new JLabel(new ImageIcon(img)), BorderLayout.WEST);
            frame.add(new JScrollPane(table));
            frame.pack();
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    frame.setVisible(true);
                }
            });
        } catch (CDKException ex) {
            System.err.println(ex);
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
        }
    }

    private void showSelected(ChemicalStructure structure) {
        try {
            selectedStructure.setText("");
            selectedStructure.setIcon(new ImageIcon(MoleculeRenderer.getInstance().getImage(structure.getStructure(),
                                                                                            new Rectangle(0, 0, 256, 256),
                                                                                            getBackground())));
        } catch (CDKException e) {
            selectedStructure.setText("Unable to display structure!");
            selectedStructure.setIcon(null);
        }
    }
}
