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

package uk.ac.ebi.mnb.dialog.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.edit.DeleteEntitiesEdit;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;


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

        Reconstruction recon = DefaultReconstructionManager.getInstance()
                                                           .active();
        EntityCollection selection = getSelection();

        EntityCollection collection = new EntityMap(DefaultEntityFactory.getInstance());
        collection.addAll(selection.getEntities());

        // ergh bit of hack but stops a bug for now
        boolean showWarning = Boolean.FALSE;

        DeleteEntitiesEdit edit = new DeleteEntitiesEdit(recon, collection);
        getController().getUndoManager().addEdit(edit);

        for (Metabolite m : collection.get(Metabolite.class)) {
            recon.remove(m);
        }
        for (MetabolicReaction r : collection.get(MetabolicReaction.class)) {
            recon.remove(r);
        }
        for (Gene g : collection.get(Gene.class)) {
            recon.remove(g);
        }
        for (GeneProduct p : collection.getGeneProducts()) {
            recon.remove(p);
        }

        selection.clear();
        update();

        if (showWarning) {
            String mesg = "Some metabolites were not deleted as they were referenced in" +
                    " reactions. Please remove the metabolites from the reactions that use them or" +
                    " delete the entire reaction first.";
            getController().getMessageManager()
                    .addReport(new WarningMessage(mesg));
        }

    }
}
