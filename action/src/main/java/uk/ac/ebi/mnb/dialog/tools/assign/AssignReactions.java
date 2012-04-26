package uk.ac.ebi.mnb.dialog.tools.assign;

import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.chemet.resource.chemical.KEGGCompoundIdentifier;
import uk.ac.ebi.chemet.resource.classification.ECNumber;
import uk.ac.ebi.chemet.service.KEGGReactionService;
import uk.ac.ebi.chemet.service.query.LuceneServiceManager;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.DefaultReconstructionManager;
import uk.ac.ebi.core.reaction.MetabolicParticipantImplementation;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.IdentifierReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.Participant;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.service.query.name.PreferredNameService;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Collection;

/**
 * AssignReactions - 07.03.2012 <br/>
 * <p/>
 * Class descriptions.
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class AssignReactions extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(AssignReactions.class);

    public AssignReactions(MainController controller) {
        super("Assign Reactions", controller);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        EntityCollection selection = getSelection();
        DefaultReconstructionManager manager = DefaultReconstructionManager.getInstance();
        Reconstruction recon = manager.getActive();

        Collection<GeneProduct> products = selection.getGeneProducts();

        try {
            KEGGReactionService reactionService = new KEGGReactionService(DefaultEntityFactory.getInstance());
            PreferredNameService nameService = LuceneServiceManager.getInstance().getService(KEGGCompoundIdentifier.class, PreferredNameService.class);

            for (GeneProduct product : products) {
                for (CrossReference xref : product.getAnnotationsExtending(CrossReference.class)) {
                    Identifier id = xref.getIdentifier();
                    if(!(id instanceof ECNumber)){
                        continue;
                    }
                    ECNumber ec = (ECNumber) id;

                    for (IdentifierReaction<KEGGCompoundIdentifier> idrxn : reactionService.getReaction(ec)) {

                        MetabolicReaction rxn = DefaultEntityFactory.getInstance().newInstance(MetabolicReaction.class);
                        rxn.setName("fetched reaction");
                        rxn.setAbbreviation(idrxn.getAbbreviation());
                        rxn.setIdentifier(idrxn.getIdentifier());


                        for (Participant<KEGGCompoundIdentifier, Double> p : idrxn.getReactants()) {

                            Metabolite m = DefaultEntityFactory.getInstance().newInstance(Metabolite.class);
                            m.setIdentifier(p.getMolecule());
                            m.setName(nameService.getPreferredName(p.getMolecule()));
                            m.setAbbreviation(p.getMolecule().getAccession());

                            rxn.addReactant(new MetabolicParticipantImplementation(m, p.getCoefficient()));

                        }

                        for (Participant<KEGGCompoundIdentifier, Double> p : idrxn.getProducts()) {

                            Metabolite m = DefaultEntityFactory.getInstance().newInstance(Metabolite.class);
                            m.setIdentifier(p.getMolecule());
                            m.setName(nameService.getPreferredName(p.getMolecule()));
                            m.setAbbreviation(p.getMolecule().getAccession());

                            rxn.addProduct(new MetabolicParticipantImplementation(m, p.getCoefficient()));

                        }

                        rxn.addModifier(product);

                        recon.addReaction(rxn);

                    }


                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        update();

    }

}
