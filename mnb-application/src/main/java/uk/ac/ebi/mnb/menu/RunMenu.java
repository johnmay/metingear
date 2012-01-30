/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;


import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.entities.EntityCollection;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import uk.ac.ebi.mnb.core.TaskManager;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.build.RunTasksAction;

/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class RunMenu extends ContextMenu {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( RunMenu.class );
    private RunTasksAction runTasksAction;

    public RunMenu() {

        super( "Run", MainView.getInstance() );

        runTasksAction = new RunTasksAction();

        add(runTasksAction, new ContextResponder() {
            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return ! TaskManager.getInstance().getQueuedTasks().isEmpty();
            }
        });

      
    }


    public RunTasksAction getRunTasksAction() {
        return runTasksAction;
    }





}
