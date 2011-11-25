/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.core.ControllerDialogItem;
import uk.ac.ebi.mnb.dialog.tools.TransferAnnotations;
import uk.ac.ebi.mnb.dialog.tools.AutomaticCrossReference;
import uk.ac.ebi.mnb.dialog.tools.ChokePoint;
import uk.ac.ebi.mnb.dialog.tools.CollapseStructures;
import uk.ac.ebi.mnb.dialog.tools.DownloadStructuresDialog;
import uk.ac.ebi.mnb.dialog.tools.MergeLoci;
import uk.ac.ebi.mnb.dialog.tools.SequenceHomology;
import uk.ac.ebi.mnb.dialog.tools.stoichiometry.CreateMatrix;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.reconciliation.AddCrossReference;

/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class ToolsMenu extends JMenu {

    private static final Logger logger = Logger.getLogger(ToolsMenu.class);

    public ToolsMenu() {

        super("Tools");

        MainView view = MainView.getInstance();

        add(new DynamicMenuItem(new AddCrossReference()));
        add(new ControllerDialogItem(view,
                                     AutomaticCrossReference.class));
        add(new ControllerDialogItem(view, DownloadStructuresDialog.class));

        add(new JSeparator());

        add(new ChokePoint(view));
        add(new JSeparator());
        add(new ControllerDialogItem(view, SequenceHomology.class));
        add(new ControllerDialogItem(view, TransferAnnotations.class));
        add(new JSeparator());
        add(new JMenuItem(new MergeLoci(MainView.getInstance())));
        add(new ControllerDialogItem(view, CollapseStructures.class));
        add(new JSeparator());
        add(new ControllerDialogItem(view, CreateMatrix.class));


    }
}
