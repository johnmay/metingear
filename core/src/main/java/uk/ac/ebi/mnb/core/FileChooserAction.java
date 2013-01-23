/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.core;

import uk.ac.ebi.caf.action.DelayedBuildAction;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * FileChooserAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public abstract class FileChooserAction extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( FileChooserAction.class );
    private JFileChooser chooser;

    public FileChooserAction( String command ) {
        super( command );
    }

    @Override
    public void buildComponents() {
        chooser = new JFileChooser();
        chooser.setName( getName() );
    }

    public JFileChooser getChooser() {
        return chooser;
    }

    public int showOpenDialog() {
        return getChooser().showOpenDialog( null );
    }

    public int showSaveDialog() {
        return getChooser().showSaveDialog( null);
    }

    public File getFile( int returnValue ) {
        if ( JFileChooser.APPROVE_OPTION == returnValue ) {
            return chooser.getSelectedFile();
        }
        return null;
    }
}
