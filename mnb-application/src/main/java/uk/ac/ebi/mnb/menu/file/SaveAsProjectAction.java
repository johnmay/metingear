/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import javax.swing.JOptionPane;
import uk.ac.ebi.caf.utility.preference.type.IntegerPreference;
import uk.ac.ebi.caf.utility.version.Version;
import uk.ac.ebi.core.CorePreferences;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.io.core.DefaultReconstructionOutputStream;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.io.FileFilterManager;


/**
 * SaveAsProjectAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class SaveAsProjectAction
        extends FileChooserAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SaveAsProjectAction.class);


    public SaveAsProjectAction() {
        super("SaveAsProject");
    }


    @Override
    public void activateActions() {

        ReconstructionManager manager = ReconstructionManager.getInstance();

        if (manager.getActive() == null) {
            JOptionPane.showMessageDialog(getChooser(), "No active project define");
        } else {

            // get the name to choose
            getChooser().setSelectedFile(manager.getActive().getContainer());

            getChooser().addChoosableFileFilter(FileFilterManager.getInstance().getProjectFilter());
            File f = getFile(showSaveDialog());

            if (f != null) {
                Reconstruction recon = manager.getActive();
                recon.setContainer(f);
                f.mkdir();


                try {
                    IntegerPreference bufferPref = CorePreferences.getInstance().getPreference("BUFFER_SIZE");

                    OutputStream out = new GZIPOutputStream(new FileOutputStream(new File(f, "data")),
                                                            bufferPref.get());
                    DefaultReconstructionOutputStream ros = new DefaultReconstructionOutputStream(out, new Version(0, 8, 5, 0), DefaultEntityFactory.getInstance());


                    long start = System.currentTimeMillis();
                    ros.write(recon);
                    long end = System.currentTimeMillis();

                    logger.info("Wrote reconstruction in " + (end - start) + " ms");

                    ros.close();
                } catch (IOException ex) {
                    Logger.getLogger(SaveAsProjectAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


        }
    }
}
