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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.ProteinProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import uk.ac.ebi.mnb.dialog.tools.AddFlags;
import uk.ac.ebi.mnb.dialog.tools.AutomaticCrossReference;
import uk.ac.ebi.mnb.dialog.tools.ChokePoint;
import uk.ac.ebi.mnb.dialog.tools.CollapseStructures;
import uk.ac.ebi.mnb.dialog.tools.CompareReconstruction;
import uk.ac.ebi.mnb.dialog.tools.CuratedReconciliation;
import uk.ac.ebi.mnb.dialog.tools.DownloadStructuresDialog;
import uk.ac.ebi.mnb.dialog.tools.MergeLoci;
import uk.ac.ebi.mnb.dialog.tools.RemoveWorstStructures;
import uk.ac.ebi.mnb.dialog.tools.SequenceHomology;
import uk.ac.ebi.mnb.dialog.tools.TransferAnnotations;
import uk.ac.ebi.mnb.dialog.tools.compare.AlignReconstruction;
import uk.ac.ebi.mnb.dialog.tools.stoichiometry.CreateMatrix;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;


/**
 * FileMenu.java
 *
 * @author johnmay @date Apr 28, 2011
 */
public class ToolsMenu extends ContextMenu {

    private static final Logger logger = Logger.getLogger(ToolsMenu.class);

    private GapAnalysis gapMenu;
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

        /**
         * *********************
         * Merging *
         **********************
         */
        add(new JMenuItem(new MergeLoci(MainView.getInstance())));

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
                        .getReactome().isEmpty() == false);
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
        }

    }


    @Override
    public void updateContext() {
        super.updateContext();
        gapMenu.updateContext();
    }
}
