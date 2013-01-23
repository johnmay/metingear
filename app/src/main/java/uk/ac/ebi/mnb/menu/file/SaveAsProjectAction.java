/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.ui.component.ReconstructionFileChooser;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;


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

            // add suffix if missing
            if (!f.getName().endsWith(".mr")) {
                f = new File(f.getPath() + ".mr");
            }

            Reconstruction reconstruction = manager.getActive();
            try {
                ReconstructionIOHelper.write(reconstruction, f);
            } catch (IOException e) {
                MainView.getInstance().addErrorMessage("unable to save reconstruction: " + e.getMessage());
            }

        }


    }
}
