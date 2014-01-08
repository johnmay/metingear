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

import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
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
                                        cs.getStructure().setProperty("Name",
                                                                      n.getName());
                                        cs.getStructure().setProperty("Id",
                                                                      n.getIdentifier());
                                        cs.getStructure().setProperty("Score",
                                                                      score(query,
                                                                            cs.getStructure()));
                                        try {
                                            cs.getStructure().setProperty("SMILES",
                                                                          SmilesGenerator.isomeric().create(cs.getStructure()));
                                        } catch (CDKException ex) {
                                            cs.getStructure().setProperty("SMILES",
                                                                          " " + ex.getMessage());
                                        }

                                        structures.add(cs.getStructure());
                                    }
                                }
                            }

                            Collections.sort(structures, Collections.reverseOrder(new Comparator<IAtomContainer>() {
                                @Override public int compare(IAtomContainer o1, IAtomContainer o2) {
                                    Double s1 = o1.getProperty("Score");
                                    Double s2 = o2.getProperty("Score");
                                    return s1.compareTo(s2);
                                }
                            }));


                            final JFrame frame = new JFrame();
                            JTable table = StructureTable.tableOf(structures, Arrays.asList("Name", "Id", "Score", "SMILES"));
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
