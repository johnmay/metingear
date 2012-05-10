/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.caf.utility.preference.type.IntegerPreference;
import uk.ac.ebi.caf.utility.version.Version;
import uk.ac.ebi.mdk.domain.DomainPreferences;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.entity.ReconstructionImpl;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.io.*;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.FileMenu;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import java.io.*;
import java.util.Properties;


/**
 * OpenProjectAction.java
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class OpenAction
        extends FileChooserAction {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
                    OpenAction.class);

    private static final IntegerPreference BUFFER_SIZE = DomainPreferences.getInstance().getPreference("BUFFER_SIZE");

    private FileMenu menu;

    private File fixedFile;

    FileFilter projFilter = new FileFilter() {
        public boolean accept(File f) {

            if (!f.isDirectory()) {
                return false;
            }

            String path = f.getPath();
            int lastIndex = path.lastIndexOf(".");
            if (lastIndex != -1) {
                String extension = path.substring(lastIndex);
                if (extension.equals(".mnb")) {
                    return true;
                }
            }

            return false;

        }

        public String getDescription() {
            return "Metingear Project";
        }

    };


    public OpenAction(FileMenu menu) {
        super("OpenProject");
        this.menu = menu;
    }


    public OpenAction(FileMenu menu, File file) {
        super("");
        this.menu = menu;
        this.fixedFile = file;
        putValue(Action.NAME, file.toString());

    }

    @Override
    public void buildComponents() {
        super.buildComponents();
        getChooser().addChoosableFileFilter(projFilter);
        getChooser().setFileView(new MNBFileView());
        getChooser().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }

    @Override
    public void activateActions() {

        // show file chooser if there is no fixed file (from open recent)
        File file = (fixedFile == null) ? getFile(showOpenDialog()) : fixedFile;

        if (file != null) {
            try {

                File entities = new File(file, "entities");
                File annotations = new File(file, "entity-annotations");
                File observations = new File(file, "entity-observations");
                File info = new File(file, "info.properties");

                Properties properties = new Properties();
                FileInputStream in = new FileInputStream(info);
                properties.load(in);
                String value = properties.getProperty("chemet.version");
                in.close();

                Version version = new Version(value == null ? "1.2" : value);

                // open data input stream
                DataInputStream annotationStream = new DataInputStream(new BufferedInputStream(new FileInputStream(annotations), BUFFER_SIZE.get()));
                DataInputStream observationStream = new DataInputStream(new BufferedInputStream(new FileInputStream(observations), BUFFER_SIZE.get()));
                DataInputStream entityStream = new DataInputStream(new BufferedInputStream(new FileInputStream(entities), BUFFER_SIZE.get()));

                EntityFactory factory = DefaultEntityFactory.getInstance();

                ObservationInput observationInput = new ObservationDataInputStream(observationStream, version);
                AnnotationInput annotationInput = new AnnotationDataInputStream(annotationStream, version);
                EntityInput entityInput = new EntityDataInputStream(version, entityStream, factory, annotationInput, observationInput);


                long start = System.currentTimeMillis();
                ReconstructionImpl reconstruction = entityInput.read();
                long end = System.currentTimeMillis();
                logger.info("Loaded project data in " + (end - start) + " (ms)");

                entityStream.close();
                annotationStream.close();
                observationStream.close();

                // set as the active reconstruction
                DefaultReconstructionManager.getInstance().setActiveReconstruction(reconstruction);

                // update the view with the
                MainView.getInstance().update();

                // fire signal at the open recent items menu
                menu.rebuildRecentlyOpen();

                // update action context (i.e. we can now close a project)
                MainView.getInstance().getJMenuBar().updateContext();


            } catch (IOException ex) {
                MainView.getInstance().addErrorMessage(
                        "Unable to load project " + ex.getStackTrace().toString().replaceAll("\n", "<br>"));
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                MainView.getInstance().addErrorMessage(
                        "Unable to load project: "
                                + ex.getStackTrace().toString().replaceAll("\n", "<br>"));
            }
        }
    }


    private class MNBFileView
            extends FileView {

        @Override
        public Boolean isTraversable(File f) {
            return !projFilter.accept(f);
        }


        @Override
        public Icon getIcon(File f) {
            if (projFilter.accept(f)) {
                return ResourceUtility.getIcon("/uk/ac/ebi/chemet/render/images/networkbuilder_16x16.png");
            }
            return super.getIcon(f);
        }
    }
}
