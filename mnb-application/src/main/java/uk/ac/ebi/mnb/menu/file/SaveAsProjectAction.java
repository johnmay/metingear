/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.caf.utility.preference.type.IntegerPreference;
import uk.ac.ebi.caf.utility.version.Version;
import uk.ac.ebi.mdk.domain.DomainPreferences;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.io.*;
import uk.ac.ebi.mdk.ui.component.ReconstructionFileChooser;

import javax.swing.*;
import java.io.*;
import java.util.Properties;


/**
 * SaveAsProjectAction.java
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class SaveAsProjectAction
        extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SaveAsProjectAction.class);

    private static final Logger LOGGER = Logger.getLogger(SaveAsProjectAction.class);
    private ReconstructionFileChooser chooser;

    public SaveAsProjectAction() {
        super("SaveAsProject");
    }

    @Override
    public void buildComponents() {
        chooser = new ReconstructionFileChooser();
    }

    @Override
    public void activateActions() {

        DefaultReconstructionManager manager = DefaultReconstructionManager.getInstance();

        // get the name to choose
        int choice = chooser.showSaveDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();

            Reconstruction reconstruction = manager.getActive();
            reconstruction.setContainer(f);
            f.mkdir();


            try {
                IntegerPreference bufferPref = DomainPreferences.getInstance().getPreference("BUFFER_SIZE");

                reconstruction.getContainer().mkdirs();

                File entities = new File(reconstruction.getContainer(), "entities");
                File annotations = new File(reconstruction.getContainer(), "entity-annotations");
                File observations = new File(reconstruction.getContainer(), "entity-observations");
                File info = new File(reconstruction.getContainer(), "info.properties");

                Version version = IOConstants.VERSION;

                Properties properties = new Properties();

                if (info.exists()) {
                    FileInputStream propInput = new FileInputStream(info);
                    properties.load(propInput);
                    propInput.close();
                    String value = properties.getProperty("chemet.version");
                    version = value == null ? version : new Version(value);
                }

                properties.put("chemet.version", version.toString());
                FileWriter writer = new FileWriter(info);
                properties.store(writer, "Project info");
                writer.close();

                DataOutputStream entityDataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(entities), bufferPref.get()));
                DataOutputStream annotationDataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(annotations), bufferPref.get()));
                DataOutputStream observationDataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(observations), bufferPref.get()));


                EntityFactory factory = DefaultEntityFactory.getInstance();

                AnnotationOutput annotationOutput = new AnnotationDataOutputStream(annotationDataOut, version);
                ObservationOutput observationOutput = new ObservationDataOutputStream(observationDataOut, version);
                EntityOutput entityOutput = new EntityDataOutputStream(version,
                                                                       entityDataOut,
                                                                       factory,
                                                                       annotationOutput,
                                                                       observationOutput);

                long start = System.currentTimeMillis();
                entityOutput.write(reconstruction);
                long end = System.currentTimeMillis();

                LOGGER.info("Wrote reconstruction in " + (end - start) + " ms");

                entityDataOut.close();
                annotationDataOut.close();
                observationDataOut.close();

            } catch (IOException ex) {
                LOGGER.error("Could not save project", ex);
            }
        }


    }
}
