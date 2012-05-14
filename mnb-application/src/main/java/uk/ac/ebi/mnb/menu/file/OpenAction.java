/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.caf.utility.preference.type.IntegerPreference;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.DomainPreferences;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.ui.component.ReconstructionFileChooser;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.FileMenu;

import javax.swing.*;
import java.io.File;
import java.io.IOException;


/**
 * OpenProjectAction.java
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class OpenAction
        extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
                    OpenAction.class);

    private static final IntegerPreference BUFFER_SIZE = DomainPreferences.getInstance().getPreference("BUFFER_SIZE");

    private FileMenu menu;

    private File fixedFile;

    private ReconstructionFileChooser chooser;


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
        chooser = new ReconstructionFileChooser();
    }

    @Override
    public void activateActions() {

        // show file chooser if there is no fixed file (from open recent)
        File file = (fixedFile == null) ? getFile() : fixedFile;

        if (file != null) {
            try {

                Reconstruction reconstruction = ReconstructionIOHelper.read(file);

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


    public File getFile() {
        int choice = chooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile();
        return null;
    }
}
