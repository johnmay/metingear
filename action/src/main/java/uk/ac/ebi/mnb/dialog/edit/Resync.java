package uk.ac.ebi.mnb.dialog.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;

/**
 * Simple util that will force an update to all views
 * @author John May
 */
public class Resync extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(Resync.class);

    public Resync(MainController controller) {
        super(Resync.class.getSimpleName(), controller);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultReconstructionManager.getInstance().getActive().getReactome().rebuildMaps();
        getController().update();
    }
}
