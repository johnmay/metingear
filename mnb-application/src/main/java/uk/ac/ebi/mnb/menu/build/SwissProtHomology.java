/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.build;

import uk.ac.ebi.caf.action.DelayedBuildAction;

/**
 * NewProjectAction.java
 *
 *
 * @author johnmay
 * @date Apr 27, 2011
 */
public class SwissProtHomology extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( SwissProtHomology.class );
    private SwissProtHomologyDialog dialog;

    public SwissProtHomology() {
        super( "SwissProtHomology" );
    }

    @Override
    public void activateActions() {
        // pack components, set location and
        // visible on every new project
        dialog.setVisible( true );
    }

    @Override
    public void buildComponents() {
        dialog = new SwissProtHomologyDialog();
    }
}
