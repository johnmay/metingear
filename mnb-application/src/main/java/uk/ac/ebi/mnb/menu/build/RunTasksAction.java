/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.build;

import java.awt.event.ActionEvent;
import mnb.view.old.TaskManager;
import uk.ac.ebi.mnb.core.GeneralAction;
import uk.ac.ebi.mnb.main.MainView;

/**
 * RunTasksAction.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class RunTasksAction extends GeneralAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( RunTasksAction.class );

    public RunTasksAction() {
        super("RunTasks");
    }

    public void actionPerformed( ActionEvent e ) {
        // start the task manager thread
        TaskManager tm = TaskManager.getInstance();
        Thread t = new Thread(tm);
        t.start();
        MainView.getInstance().update();
    }


}
