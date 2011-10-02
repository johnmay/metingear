/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import uk.ac.ebi.mnb.core.DelayedBuildAction;

/**
 * NewProjectAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class NewProjectAction
    extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( NewProjectAction.class );
    private NewProject dialog;

    public NewProjectAction() {
        super( "NewProject" );
    }

    @Override
    public void activateActions() {
        dialog.setVisible( enabled );
    }

    @Override
    public void buildComponents() {
        dialog = new NewProject();
    }
}
