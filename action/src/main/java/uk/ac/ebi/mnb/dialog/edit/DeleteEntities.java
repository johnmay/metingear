/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
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

package uk.ac.ebi.mnb.dialog.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.edit.DeleteEntitiesEdit;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * DeleteEntitiesEdit - 2011.10.20 <br> An action class that removes selected
 * items from the reconstruction
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class DeleteEntities extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(DeleteEntities.class);


    public DeleteEntities(MainController controller) {
        super(DeleteEntities.class.getSimpleName(), controller);
    }


    public void actionPerformed(ActionEvent e) {

        Reconstruction recon = DefaultReconstructionManager.getInstance().getActive();
        EntityCollection selection = getSelection();

        EntityCollection collection = new EntityMap(DefaultEntityFactory.getInstance());
        collection.addAll(selection.getEntities());

        // ergh bit of hack but stops a bug for now
        boolean showWarning = Boolean.FALSE;


        // removes metabolites contained in reactions as it's tricky to delete these at
        // the moment
        List<Metabolite> metabolites = new ArrayList<Metabolite>(collection.get(Metabolite.class));
        for (Metabolite metabolite : metabolites) {
            if (!recon.getReactome().getReactions(metabolite).isEmpty()) {
                showWarning = Boolean.TRUE;
                collection.remove(metabolite);
            }
        }

        DeleteEntitiesEdit edit = new DeleteEntitiesEdit(recon, collection);
        edit.redo(); // use the edit to do the action
        getController().getUndoManager().addEdit(edit);

        selection.clear();
        getController().getViewController().getActiveView().update();

        if (showWarning) {
            String mesg = "Some metabolites were not deleted as they were referenced in" +
                    " reactions. Please remove the metabolites from the reactions that use them or" +
                    " delete the entire reaction first.";
            getController().getMessageManager().addReport(new WarningMessage(mesg));
        }

    }
}
