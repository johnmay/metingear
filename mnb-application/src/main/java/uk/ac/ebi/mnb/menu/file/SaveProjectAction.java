/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.core.FileChooserAction;

/**
 * SaveAsProjectAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class SaveProjectAction extends FileChooserAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( SaveProjectAction.class );

    public SaveProjectAction() {
        super( "SaveProject" );
    }

    @Override
    public void activateActions() {
        ReconstructionManager manager = ReconstructionManager.getInstance();
        manager.getActiveReconstruction().save();
    }
}
