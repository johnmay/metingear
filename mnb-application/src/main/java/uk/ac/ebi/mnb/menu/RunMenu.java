/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;


import uk.ac.ebi.mnb.menu.build.RunTasksAction;

/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class RunMenu extends ClearMenu {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( RunMenu.class );
    private RunTasksAction runTasksAction;
    private DynamicMenuItem items[] = new DynamicMenuItem[ 1 ];

    public RunMenu() {

        super( "Run" );

        runTasksAction = new RunTasksAction();

        items[0] = new DynamicMenuItem( runTasksAction );

        for ( DynamicMenuItem mnbMenuItem : items ) {
            add( mnbMenuItem );
            mnbMenuItem.reloadEnabled();
        }
    }

    public void setActiveDependingOnRequirements() {
        for ( DynamicMenuItem mnbMenuItem : items ) {
            mnbMenuItem.reloadEnabled();
        }
    }

    public RunTasksAction getRunTasksAction() {
        return runTasksAction;
    }





}
