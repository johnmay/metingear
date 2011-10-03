/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu;

import uk.ac.ebi.mnb.core.SelectionMenuItem;
import uk.ac.ebi.mnb.dialog.tools.ChokePoint;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.menu.build.RunTasksAction;
import uk.ac.ebi.mnb.menu.reconciliation.DownloadStructures;


/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class ToolsMenu extends ClearMenu {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(ToolsMenu.class);


    public ToolsMenu() {

        super("Tools");

        add(new ReconciliationMenu());
        add(new ChokePoint(MainFrame.getInstance()));

      
    }


}

