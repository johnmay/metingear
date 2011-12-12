/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.ProteinProduct;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import uk.ac.ebi.metingeer.menu.ContextMenu;
import uk.ac.ebi.mnb.dialog.tools.*;
import uk.ac.ebi.mnb.dialog.tools.stoichiometry.CreateMatrix;
import uk.ac.ebi.mnb.interfaces.SelectionManager;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.reconciliation.AddCrossReferenceDialog;
import uk.ac.ebi.mnb.menu.tool.GapAnalysis;

/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class ToolsMenu extends ContextMenu {

    private static final Logger logger = Logger.getLogger(ToolsMenu.class);
    private GapAnalysis gapMenu;

    public ToolsMenu() {

        super("Tools", MainView.getInstance());

        MainView view = MainView.getInstance();

        add(create(AddCrossReferenceDialog.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, SelectionManager selection) {
                return selection.hasSelection() && selection.getEntities().size() == 1;
            }
        });

        add(create(AutomaticCrossReference.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, SelectionManager selection) {
                return selection.hasSelection(Metabolite.class);
            }
        });
        add(create(DownloadStructuresDialog.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, SelectionManager selection) {
                return selection.hasSelection(Metabolite.class);
            }
        });

        add(new JSeparator());

        add(new ChokePoint(view), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, SelectionManager selection) {
                return selection.hasSelection(MetabolicReaction.class);
            }
        });
        add(new JSeparator());

        /***********************
         * Sequence annotation *
         ***********************/
        add(create(SequenceHomology.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, SelectionManager selection) {
                return selection.hasSelection(ProteinProduct.class);
            }
        });
        add(create(TransferAnnotations.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, SelectionManager selection) {
                return selection.hasSelection(ProteinProduct.class);
            }
        });

        add(new JSeparator());

        /***********************
         * Merging             *
         ***********************/
        add(new JMenuItem(new MergeLoci(MainView.getInstance())));

        add(create(CollapseStructures.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, SelectionManager selection) {
                return selection.hasSelection(Metabolite.class);

            }
        });

        add(new JSeparator());
        /*************************
         * Stoichiometric Matrix *
         *************************/
        add(create(CreateMatrix.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, SelectionManager selection) {
                return selection.hasSelection(MetabolicReaction.class) || (active != null && active.getReactions().isEmpty() == false);
            }
        });

        gapMenu = new GapAnalysis(view);
        add(gapMenu);

        /*************************
         * Comparisson           *
         *************************/
        add(new JSeparator());

        add(create(CompareReconstruction.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, SelectionManager selection) {
                return reconstructions.size() > 1;
            }
        });        

    }

    @Override
    public void updateContext() {
        super.updateContext();
        gapMenu.updateContext();
    }
}
