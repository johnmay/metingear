/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import java.io.*;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.utility.preference.type.IntegerPreference;
import uk.ac.ebi.caf.utility.version.Version;
import uk.ac.ebi.chemet.io.annotation.AnnotationDataOutputStream;
import uk.ac.ebi.chemet.io.annotation.AnnotationOutput;
import uk.ac.ebi.chemet.io.annotation.AnnotationOutputStream;
import uk.ac.ebi.chemet.io.entity.EntityDataOutputStream;
import uk.ac.ebi.chemet.io.entity.EntityOutput;
import uk.ac.ebi.chemet.io.observation.ObservationDataOutputStream;
import uk.ac.ebi.chemet.io.observation.ObservationOutput;
import uk.ac.ebi.chemet.io.observation.ObservationOutputStream;
import uk.ac.ebi.core.CorePreferences;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.entities.EntityFactory;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.chemet.io.file.FileFilterManager;


/**
 * SaveAsProjectAction.java
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class SaveAsProjectAction
        extends FileChooserAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SaveAsProjectAction.class);

    private static final Logger LOGGER = Logger.getLogger(SaveAsProjectAction.class);

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
                Reconstruction reconstruction = manager.getActive();
                reconstruction.setContainer(f);
                f.mkdir();


                try {
                    IntegerPreference bufferPref = CorePreferences.getInstance().getPreference("BUFFER_SIZE");

                    reconstruction.getContainer().mkdirs();

                    File entities = new File(reconstruction.getContainer(), "entities");
                    File annotations = new File(reconstruction.getContainer(), "entity-annotations");
                    File observations = new File(reconstruction.getContainer(), "entity-observations");

                    DataOutputStream entityDataOut = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(entities), bufferPref.get()));
                    DataOutputStream annotationDataOut = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(annotations), bufferPref.get()));
                    DataOutputStream observationDataOut = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(observations), bufferPref.get()));


                    Version version = new Version("0.9");
                    EntityFactory factory = DefaultEntityFactory.getInstance();

                    AnnotationOutput  annotationOutput  = new AnnotationDataOutputStream(annotationDataOut, version);
                    ObservationOutput observationOutput = new ObservationDataOutputStream(observationDataOut, version);
                    EntityOutput      entityOutput      = new EntityDataOutputStream(version,
                                                                                     entityDataOut,
                                                                                     factory,
                                                                                     annotationOutput,
                                                                                     observationOutput);

                    long start = System.currentTimeMillis();
                    entityOutput.write(reconstruction);
                    long end = System.currentTimeMillis();

                    LOGGER.info("Wrote reconstruction in " + (end - start) + " ms");

                    entityDataOut.close();

                } catch (IOException ex) {
                    LOGGER.error("Could not save project", ex);
                }
            }


        }
    }
}
