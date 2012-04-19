/**
 * DeleteEntitiesEdit.java
 *
 * 2011.10.20
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
package uk.ac.ebi.mnb.dialog.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.entities.EntityCollection;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.edit.DeleteEntitiesEdit;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;


/**
 *          DeleteEntitiesEdit - 2011.10.20 <br>
 *          An action class that removes selected items from the reconstruction
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class DeleteEntities extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(DeleteEntities.class);


    public DeleteEntities(MainController controller) {
        super(DeleteEntities.class.getSimpleName(), controller);
    }


    public void actionPerformed(ActionEvent e) {
        EntityCollection selection = getSelection();
        Reconstruction recon = ReconstructionManager.getInstance().getActive();

        EntityCollection collectionCopy = new EntityMap(DefaultEntityFactory.getInstance());
        collectionCopy.addAll(selection.getEntities()) ;

        DeleteEntitiesEdit edit = new DeleteEntitiesEdit(recon, collectionCopy);
        edit.redo(); // use the edit to do the action
        getController().getUndoManager().addEdit(edit);

        selection.clear();
        getController().getViewController().getActiveView().update();

    }
}
