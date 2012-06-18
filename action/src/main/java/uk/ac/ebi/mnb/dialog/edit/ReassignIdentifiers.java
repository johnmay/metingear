package uk.ac.ebi.mnb.dialog.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.*;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.identifier.basic.*;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;

/**
 * @author John May
 */
public class ReassignIdentifiers extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(ReassignIdentifiers.class);

    public ReassignIdentifiers(MainController controller) {
        super(ReassignIdentifiers.class.getSimpleName(), controller);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        for (Entity entity : getSelection().get(Metabolite.class)) {
            entity.setIdentifier(BasicChemicalIdentifier.nextIdentifier());
        }
        for (Entity entity : getSelection().get(MetabolicReaction.class)) {
            entity.setIdentifier(BasicReactionIdentifier.nextIdentifier());
        }
        for (Entity entity : getSelection().get(Gene.class)) {
            entity.setIdentifier(BasicGeneIdentifier.nextIdentifier());
        }
        for (Entity entity : getSelection().get(ProteinProduct.class)) {
            entity.setIdentifier(BasicProteinIdentifier.nextIdentifier());
        }
        for (Entity entity : getSelection().get(RNAProduct.class)) {
            entity.setIdentifier(BasicRNAIdentifier.nextIdentifier());
        }

        update(getSelection());

    }

}
