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
package uk.ac.ebi.mnb.dialog.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.Note;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;


/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 * @name FindChokePoints - 2011.10.03 <br> Performs an action on the given
 * context (selection)
 */
public class ChokePoint extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(ChokePoint.class);


    public ChokePoint(MainController controller) {
        super(ChokePoint.class.getSimpleName(), controller);
    }


    public void actionPerformed(ActionEvent ae) {

        int n = DefaultReconstructionManager.getInstance().active().metabolome().size();

        final Multimap<Metabolite, MetabolicReaction> reactants = HashMultimap.create(n, 5);
        final Multimap<Metabolite, MetabolicReaction> products = HashMultimap.create(n, 5);


        // count number of reactions producing and consuming each metabolite
        for (final MetabolicReaction rxn : getSelection().get(MetabolicReaction.class)) {

            for (final MetabolicParticipant reactant : rxn.getReactants()) {
                Metabolite metabolite = reactant.getMolecule();
                reactants.put(metabolite, rxn);
            }

            for (final MetabolicParticipant product : rxn.getProducts()) {
                Metabolite metabolite = product.getMolecule();
                products.put(metabolite, rxn);
            }

        }

        final List<MetabolicReaction> chokePoints = new ArrayList<MetabolicReaction>();

        // unique consumed metabolites
        for (Entry<Metabolite, Collection<MetabolicReaction>> entry : reactants.asMap().entrySet()) {
            if (entry.getValue().size() == 1) {
                Metabolite metabolite = entry.getKey();
                // use a better annotation
                Annotation note = new Note("Reaction uniquely consumes " + metabolite.getName() + " (" + metabolite.getIdentifier() + ")");
                entry.getValue().iterator().next().addAnnotation(note);
                chokePoints.addAll(entry.getValue());
            }
        }

        // unique produced metabolites
        for (Entry<Metabolite, Collection<MetabolicReaction>> entry : products.asMap().entrySet()) {
            if (entry.getValue().size() == 1) {
                Metabolite metabolite = entry.getKey();
                // use a better annotation
                Annotation note = new Note("Reaction uniquely produces " + metabolite.getName() + " (" + metabolite.getIdentifier() + ")");
                entry.getValue().iterator().next().addAnnotation(note);
                chokePoints.addAll(entry.getValue());
            }
        }


        LOGGER.debug("identified " + chokePoints.size() + " choke points");

        EntityMap map = new EntityMap(DefaultEntityFactory.getInstance());
        map.addAll(chokePoints);
        setSelection(map);

    }
}
