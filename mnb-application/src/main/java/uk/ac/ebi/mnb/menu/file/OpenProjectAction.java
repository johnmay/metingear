/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import com.hp.hpl.jena.graph.query.Expression.Application;
import java.io.File;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.visualisation.ViewUtils;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.io.FileFilterManager;
import uk.ac.ebi.mnb.io.ProjectFilter;
import uk.ac.ebi.mnb.main.SourceController;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.mnb.menu.FileMenu;

/**
 * OpenProjectAction.java
 *
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class OpenProjectAction
        extends FileChooserAction {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
            OpenProjectAction.class);
    private FileMenu menu;
    private File fixedFile;

    public OpenProjectAction(FileMenu menu) {
        super("OpenProject");
        this.menu = menu;
    }

    public OpenProjectAction(FileMenu menu, File file) {
        super("");
        this.menu = menu;
        this.fixedFile = file;
        putValue(Action.NAME, file.toString());
    }
    ProjectFilter projFilter = FileFilterManager.getInstance().getProjectFilter();

    @Override
    public void activateActions() {

        // all projects will be directories MyProject.mnb
        // getChooser().setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        File choosenFile = fixedFile;

        // show file chooser if there is no fixed file (from open recent)
        if (choosenFile == null) {
            getChooser().addChoosableFileFilter(projFilter);
            getChooser().setFileView(new MNBFileView());
            getChooser().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            choosenFile = getFile(showOpenDialog());
        }

        if (choosenFile != null) {
            try {
                {
                    long start = System.currentTimeMillis();
                    Reconstruction recon = Reconstruction.load(choosenFile);
                    long end = System.currentTimeMillis();
                    logger.info("Loaded project data in " + (end - start) + " (ms)");
                    ReconstructionManager.getInstance().setActiveReconstruction(recon);
                }

                SourceController controller = MainView.getInstance().getSourceListController();
                MainView.getInstance().getViewController().update();
                {
                    long start = System.currentTimeMillis();
                    controller.update();
                    long end = System.currentTimeMillis();
                    logger.info("Update source list in " + (end - start) + " (ms)");

                }

                // fire signal at the open recent items menu
                menu.rebuildRecentlyOpen();


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
                return ViewUtils.icon_16x16;
            }
            return super.getIcon(f);
        }
    }
}
