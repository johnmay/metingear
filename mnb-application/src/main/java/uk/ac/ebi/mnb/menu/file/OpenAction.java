/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import uk.ac.ebi.caf.utility.preference.type.IntegerPreference;
import uk.ac.ebi.caf.utility.version.Version;
import uk.ac.ebi.chemet.io.annotation.AnnotationDataInputStream;
import uk.ac.ebi.chemet.io.annotation.AnnotationInput;
import uk.ac.ebi.chemet.io.entity.EntityDataInputStream;
import uk.ac.ebi.chemet.io.entity.EntityInput;
import uk.ac.ebi.chemet.io.file.FileFilterManager;
import uk.ac.ebi.chemet.io.file.ProjectFilter;
import uk.ac.ebi.chemet.io.observation.ObservationDataInputStream;
import uk.ac.ebi.chemet.io.observation.ObservationInput;
import uk.ac.ebi.chemet.render.ViewUtilities;
import uk.ac.ebi.core.CorePreferences;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.entities.EntityFactory;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.FileMenu;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;


/**
 * OpenProjectAction.java
 *
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class OpenAction
        extends FileChooserAction {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
            OpenAction.class);

    private static final IntegerPreference BUFFER_SIZE = CorePreferences.getInstance().getPreference("BUFFER_SIZE");

    private FileMenu menu;

    private File fixedFile;


    public OpenAction(FileMenu menu) {
        super("OpenProject");
        this.menu = menu;
        getChooser().addChoosableFileFilter(projFilter);
        getChooser().setFileView(new MNBFileView());
        getChooser().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }


    public OpenAction(FileMenu menu, File file) {
        super("");
        this.menu = menu;
        this.fixedFile = file;
        putValue(Action.NAME, file.toString());
        getChooser().addChoosableFileFilter(projFilter);
        getChooser().setFileView(new MNBFileView());
        getChooser().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }

    ProjectFilter projFilter = FileFilterManager.getInstance().getProjectFilter();


    @Override
    public void activateActions() {

        // show file chooser if there is no fixed file (from open recent)
        File file =  (fixedFile == null) ? getFile(showOpenDialog()) : fixedFile;

        if (file != null) {
            try {

                File entities     = new File(file, "entities");
                File annotations  = new File(file, "entity-annotations");
                File observations = new File(file, "entity-observations");

                // open data input stream
                DataInputStream annotationStream  = new DataInputStream(new GZIPInputStream(new FileInputStream(annotations),  BUFFER_SIZE.get()));
                DataInputStream observationStream = new DataInputStream(new GZIPInputStream(new FileInputStream(observations), BUFFER_SIZE.get()));
                DataInputStream entityStream      = new DataInputStream(new GZIPInputStream(new FileInputStream(entities),     BUFFER_SIZE.get()));

                EntityFactory factory = DefaultEntityFactory.getInstance();
                Version version = new Version("0.9");

                ObservationInput observationInput = new ObservationDataInputStream(observationStream, version);
                AnnotationInput  annotationInput  = new AnnotationDataInputStream(annotationStream, version);
                EntityInput      entityInput      = new EntityDataInputStream(version, entityStream, factory, annotationInput, observationInput);


                long start = System.currentTimeMillis();
                Reconstruction reconstruction = entityInput.read();
                long end = System.currentTimeMillis();
                logger.info("Loaded project data in " + (end - start) + " (ms)");

                // set as the active reconstruction
                ReconstructionManager.getInstance().setActiveReconstruction(reconstruction);

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
                return ViewUtilities.icon_16x16;
            }
            return super.getIcon(f);
        }
    }
}
