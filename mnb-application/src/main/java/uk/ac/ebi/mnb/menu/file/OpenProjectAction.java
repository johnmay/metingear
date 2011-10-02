/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.file;

import com.hp.hpl.jena.graph.query.Expression.Application;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.io.FileFilterManager;
import uk.ac.ebi.mnb.io.ProjectFilter;
import uk.ac.ebi.mnb.main.SourceController;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.mnb.settings.Settings;


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


    public OpenProjectAction() {
        super("OpenProject");
    }


    ProjectFilter projFilter = FileFilterManager.getInstance().getProjectFilter();


    @Override
    public void activateActions() {

        // all projects will be directories MyProject.mnb
        // getChooser().setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        getChooser().addChoosableFileFilter(projFilter);
        getChooser().setFileView(new MNBFileView());
        getChooser().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);


        File choosenFile = getFile(showOpenDialog());

        if( choosenFile != null ) {
            try {
                {
                    long start = System.currentTimeMillis();
                    Reconstruction recon = Reconstruction.load(choosenFile);
                    long end = System.currentTimeMillis();
                    logger.info("Loaded project data in " + (end - start) + " (ms)");
                    ReconstructionManager.getInstance().setActiveReconstruction(recon);
                }

                SourceController controller = MainFrame.getInstance().getSourceListController();
                MainFrame.getInstance().getProjectPanel().update();
                {
                    long start = System.currentTimeMillis();
                    controller.update();
                    long end = System.currentTimeMillis();
                    logger.info("Update source list in " + (end - start) + " (ms)");

                }


            }  catch( IOException ex ) {
                MainFrame.getInstance().addErrorMessage(
                  "Unable to load project " + ex.getStackTrace().toString().replaceAll("\n", "<br>"));
                ex.printStackTrace();
            } catch( ClassNotFoundException ex ) {
                ex.printStackTrace();
                MainFrame.getInstance().addErrorMessage(
                  "Unable to load project: " +
                  ex.getStackTrace().toString().replaceAll("\n", "<br>"));
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
            if( projFilter.accept(f) ) {
                return ViewUtils.icon_16x16;
            }
            return super.getIcon(f);
        }


    }


}

