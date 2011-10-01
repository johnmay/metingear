/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import java.io.File;
import javax.swing.JOptionPane;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.io.FileFilterManager;

/**
 * SaveAsProjectAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class SaveAsProjectAction extends FileChooserAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( SaveAsProjectAction.class );

    public SaveAsProjectAction() {
        super( "SaveAsProject" );
    }

    @Override
    public void activateActions() {

        ReconstructionManager manager = ReconstructionManager.getInstance();

        if ( manager.getActiveReconstruction() == null ) {
            JOptionPane.showMessageDialog( getChooser() , "No active project define" );
        } else {

            // get the name to choose
            getChooser().setSelectedFile( manager.getActiveReconstruction().getContainer() );

            getChooser().addChoosableFileFilter( FileFilterManager.getInstance().getProjectFilter() );
            File f = getFile( showSaveDialog() );

            if ( f != null ) {
               manager.getActiveReconstruction().saveAsProject( f );
            }

        }
    }
}
