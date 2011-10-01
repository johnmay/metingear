
/**
 * SetActiveProject.java
 *
 * 2011.09.07
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.menu.popup;

import java.awt.event.ActionEvent;
import javax.swing.JPopupMenu;
import uk.ac.ebi.core.ReconstructionManager;
import org.apache.log4j.Logger;
import org.omg.PortableServer.POA;
import uk.ac.ebi.chemet.render.source.ReconstructionSourceItem;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.mnb.core.GeneralAction;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.mnb.main.MainFrame;


/**
 *          SetActiveProject – 2011.09.07 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class CloseProject extends GeneralAction implements ContextSensitiveAction {

    private static final Logger LOGGER = Logger.getLogger(CloseProject.class);
    private Reconstruction reconstruction;


    public CloseProject() {
        super("Popup.CloseProject");
    }


    public void actionPerformed(ActionEvent e) {
        LOGGER.info("TODO: Offer save suggestion before close");
        ReconstructionManager.getInstance().removeProject(reconstruction);
        MainFrame.getInstance().update();
        long start = System.currentTimeMillis();
        System.gc();
        long end = System.currentTimeMillis();
        LOGGER.info("Suggested grabarge collection took " + (end-start) + " (ms)");
    }


    /**
     * @inheritDoc
     */
    public void setContext(Object entity) {
        setEnabled(entity instanceof ReconstructionSourceItem);
        if( isEnabled() ) {
            reconstruction = ((ReconstructionSourceItem) entity).getEntity();
        }

    }


}

