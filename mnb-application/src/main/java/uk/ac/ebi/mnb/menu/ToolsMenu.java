/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import javax.swing.JFrame;
import javax.swing.JSeparator;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.core.ContextDialogItem;
import uk.ac.ebi.mnb.core.ControllerDialogItem;
import uk.ac.ebi.mnb.dialog.tools.AutomaticCrossReference;
import uk.ac.ebi.mnb.dialog.tools.ChokePoint;
import uk.ac.ebi.mnb.dialog.tools.FormulaSearch;
import uk.ac.ebi.mnb.dialog.tools.SequenceHomology;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.reconciliation.AddCrossReference;
import uk.ac.ebi.mnb.menu.reconciliation.DownloadStructures;

/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class ToolsMenu extends ClearMenu {

    private static final Logger logger = Logger.getLogger(ToolsMenu.class);

    public ToolsMenu() {

        super("Tools");

        MainView view = MainView.getInstance();

        add(new DynamicMenuItem(new AddCrossReference()));
        add(new ContextDialogItem(view,
                view.getViewController(),
                AutomaticCrossReference.class));
        add(new DynamicMenuItem(new DownloadStructures()));

        add(new JSeparator());

        add(new ChokePoint(view));
        add(new JSeparator());
        add(new ControllerDialogItem(view, SequenceHomology.class));





    }
}
