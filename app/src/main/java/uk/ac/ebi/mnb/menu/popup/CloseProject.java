/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.menu.popup;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.chemet.render.source.ReconstructionSourceItem;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.metingeer.interfaces.menu.ContextAction;
import uk.ac.ebi.mnb.main.MainView;

import java.awt.event.ActionEvent;

/**
 *          SetActiveProject – 2011.09.07 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class CloseProject extends GeneralAction implements ContextAction {

    private static final Logger LOGGER = Logger.getLogger(CloseProject.class);
    private Reconstruction reconstruction;
    private boolean active = false;

    public CloseProject() {
        super("CloseProject");
    }

    /**
     * Tells the action to close the active project
     * @param active
     */
    public CloseProject(boolean active) {
        super("CloseProject");
        this.active = active;
    }

    public void actionPerformed(ActionEvent e) {
        LOGGER.info("TODO: Offer save suggestion before close");
        ReconstructionManager manager = DefaultReconstructionManager.getInstance();
        if(active){
            manager.remove(manager.active());
        } else {
            manager.remove(reconstruction);
        }
        if(!manager.isEmpty()){
            manager.activate(manager.reconstructions().iterator().next());
        }
        MainView.getInstance().update();
    }

    /**
     * @inheritDoc
     */
    public boolean getContext(Object entity) {
        setEnabled(entity instanceof ReconstructionSourceItem);
        if (isEnabled()) {
            reconstruction = ((ReconstructionSourceItem) entity).getEntity();
            return true;
        }
        return false;
    }


}
