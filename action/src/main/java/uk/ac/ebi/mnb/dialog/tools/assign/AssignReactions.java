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

package uk.ac.ebi.mnb.dialog.tools.assign;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.IdentifierReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipantImplementation;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.Participant;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.identifier.KEGGCompoundIdentifier;
import uk.ac.ebi.mdk.domain.identifier.classification.ECNumber;
import uk.ac.ebi.mdk.service.DefaultServiceManager;
import uk.ac.ebi.mdk.service.KEGGReactionService;
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
 * @deprecated not currently used
 */
@Deprecated
public class AssignReactions extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(AssignReactions.class);

    public AssignReactions(MainController controller) {
        super("Assign Reactions", controller);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        EntityCollection selection = getSelection();
        ReconstructionManager manager = DefaultReconstructionManager.getInstance();
        Reconstruction recon = manager.active();

        Collection<GeneProduct> products = selection.getGeneProducts();

        try {
            KEGGReactionService reactionService = new KEGGReactionService(DefaultEntityFactory.getInstance());
            PreferredNameService nameService = DefaultServiceManager.getInstance().getService(KEGGCompoundIdentifier.class, PreferredNameService.class);

            for (GeneProduct product : products) {
                for (CrossReference xref : product.getAnnotationsExtending(CrossReference.class)) {
                    Identifier id = xref.getIdentifier();
                    if (!(id instanceof ECNumber)) {
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

                        recon.associate(product, rxn);
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
