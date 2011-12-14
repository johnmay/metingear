/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import java.io.IOException;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.main.MainView;

/**
 * SaveAsProjectAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class SaveProjectAction
        extends FileChooserAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SaveProjectAction.class);

    public SaveProjectAction() {
        super("SaveProject");
    }

    @Override
    public void activateActions() {
        ReconstructionManager manager = ReconstructionManager.getInstance();
        try {
            manager.getActive().save();
        } catch (IOException ex) {
            MainView.getInstance().getMessageManager().addMessage(new ErrorMessage(ex.getMessage()));
        }
    }
}
