/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.primitives.Doubles;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.Score;
import org.openscience.cdk.isomorphism.StereoCompatibility;
import org.openscience.cdk.isomorphism.StructureUtil;
import org.openscience.cdk.smiles.SmilesGenerator;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.ProteinProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;
import uk.ac.ebi.mdk.ui.render.molecule.MoleculeRenderer;
import uk.ac.ebi.metingear.tools.structure.StructureTable;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.dialog.tools.AddFlags;
import uk.ac.ebi.mnb.dialog.tools.AutomaticCrossReference;
import uk.ac.ebi.mnb.dialog.tools.ChokePoint;
import uk.ac.ebi.mnb.dialog.tools.CollapseStructures;
import uk.ac.ebi.mnb.dialog.tools.CompareReconstruction;
import uk.ac.ebi.mnb.dialog.tools.CuratedReconciliation;
import uk.ac.ebi.mnb.dialog.tools.DownloadStructuresDialog;
import uk.ac.ebi.mnb.dialog.tools.RemoveWorstStructures;
import uk.ac.ebi.mnb.dialog.tools.SequenceHomology;
import uk.ac.ebi.mnb.dialog.tools.TransferAnnotations;
import uk.ac.ebi.mnb.dialog.tools.compare.AlignReconstruction;
import uk.ac.ebi.mnb.dialog.tools.stoichiometry.CreateMatrix;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.openscience.cdk.isomorphism.Scorer.score;


/**
 * FileMenu.java
 *
 * @author johnmay @date Apr 28, 2011
 */
public class ToolsMenu extends ContextMenu {

    private static final Logger logger = Logger.getLogger(ToolsMenu.class);

    private       GapAnalysis gapMenu;
    private final ContextMenu annotation;

    private boolean developer = Boolean.getBoolean("metingear.developer");

    public ToolsMenu() {

        super("Tools", MainView.getInstance());

        MainView view = MainView.getInstance();

        // sub-menus
        annotation = new ContextMenu("Annotation", MainView.getInstance());
        gapMenu = new GapAnalysis(view);

        add(annotation);

        annotation
                .add(create(AutomaticCrossReference.class), new ContextResponder() {

                    public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                        return selection.hasSelection(Metabolite.class);
                    }
                });

        annotation.add(new AddFlags(view), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return !selection.isEmpty();
            }
        });

        annotation
                .add(create(DownloadStructuresDialog.class), new ContextResponder() {
                    public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                        return selection.hasSelection(Metabolite.class);
                    }
                });


        annotation.add(new JSeparator());

        annotation.add(new CuratedReconciliation(view), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection.hasSelection(Metabolite.class);
            }
        });

        annotation.add(new JSeparator());

        add(new JSeparator());

        add(new ChokePoint(view), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection.hasSelection(MetabolicReaction.class);
            }
        });

        //add(new AssignReactions(view));

        add(new JSeparator());

        /**
         * *********************
         * Sequence annotation *
         **********************
         */
        add(create(SequenceHomology.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection.hasSelection(ProteinProduct.class);
            }
        });
        add(create(TransferAnnotations.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection.hasSelection(ProteinProduct.class);
            }
        });

        add(new JSeparator());


        add(create(CollapseStructures.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection.hasSelection(Metabolite.class);

            }
        });
        add(new RemoveWorstStructures(MainView.getInstance()), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection.hasSelection(Metabolite.class);
            }
        });
        add(new JSeparator());
        add(create(CreateMatrix.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection
                        .hasSelection(MetabolicReactionImpl.class) || (active != null && active
                        .reactome().isEmpty() == false);
            }
        });


        if (developer) {
            add(gapMenu);
            add(new JSeparator());
            add(create(CompareReconstruction.class), new ContextResponder() {

                public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                    return reconstructions.reconstructions().size() > 1;
                }
            });
            add(create(AlignReconstruction.class), new ContextResponder() {

                public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                    return active != null;
                }
            });
            add(new JSeparator());
            add(new ControllerAction("Find", MainView.getInstance()) {
                    @Override public void actionPerformed(ActionEvent e) {

                        try {
                            Metabolite m = (Metabolite) getSelection().getFirstEntity();
                            ReconstructionManager manager = DefaultReconstructionManager.getInstance();

                            Collection<ChemicalStructure> css = m.getStructures();

                            if (css.isEmpty() || css.size() > 1) return;

                            IAtomContainer query = css.iterator().next().getStructure();

                            List<IAtomContainer> structures = new ArrayList<IAtomContainer>();

                            for (Metabolite n : manager.active().metabolome()) {
                                if (m != n) {
                                    for (ChemicalStructure cs : n.getStructures()) {

                                        IAtomContainer unsuppressed = cs.getStructure();
                                        IAtomContainer suppressed = StructureUtil.suppressHydrogens(cs.getStructure());
                                        Score score = score(query, suppressed);
                                        if (score == Score.MIN_VALUE)
                                            continue;

                                        unsuppressed.setProperty("Name",
                                                                 n.getName());
                                        unsuppressed.setProperty("Id",
                                                                 n.getIdentifier());
                                        unsuppressed.setProperty("Score",
                                                                 score);
                                        unsuppressed.setProperty("Score.Value",
                                                                 score.toDouble());
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
                                            int[] ids = new int[unsuppressed.getAtomCount()];
                                            StereoCompatibility[] compatibilities = score.compatibilities();
                                            for (int i = 0; i < ids.length; i++) {
                                                switch (compatibilities[i]) {
                                                    case Matched:
                                                        ids[i] = 1;
                                                    case Missing:
                                                        ids[i] = 2;
                                                    case Mismatched:
                                                        ids[i] = 3;
                                                }
                                            }
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
                            JTable table = StructureTable.tableOf(structures, Arrays.asList("Name", "Id", "Score", "Score.Value", "Stereo.Comp", "SMILES"));
                            table.setSelectionBackground(new Color(0xCCCCCC));
                            table.setSelectionForeground(new Color(0x444444));
                            BufferedImage img = MoleculeRenderer.getInstance().getImage(query, new Rectangle(0, 0, 256, 256));
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
                },
                new ContextResponder() {
                    @Override public boolean getContext(ReconstructionManager reconstructions, Reconstruction
                            active, EntityCollection selection) {
                        return active != null && selection.hasSelection(Metabolite.class) && selection.getEntities().size() == 1;
                    }
                }
               );
            add(new AbstractAction("Amino Acids Table") {
                @Override public void actionPerformed(ActionEvent e) {
                    try {
                        final JFrame frame = new JFrame();
                        JTable table = StructureTable.aminoAcids();
                        frame.add(new JScrollPane(table));
                        frame.pack();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() {
                                frame.setVisible(true);
                            }
                        });
                    } catch (InvalidSmilesException e1) {
                        System.err.println(e1.getMessage());
                    }
                }
            });
        }

    }


    @Override
    public void updateContext() {
        super.updateContext();
        gapMenu.updateContext();
    }
}
