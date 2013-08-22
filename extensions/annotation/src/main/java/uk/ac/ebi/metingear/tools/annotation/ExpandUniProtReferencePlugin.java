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

package uk.ac.ebi.metingear.tools.annotation;

import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.ProteinProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.UniProtIdentifier;
import uk.ac.ebi.mdk.service.DefaultServiceManager;
import uk.ac.ebi.mdk.service.query.CrossReferenceService;
import uk.ac.ebi.metingear.view.ControlAction;
import uk.ac.ebi.metingear.view.PlugableAction;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;

import java.util.Arrays;
import java.util.List;

/** @author John May */
public final class ExpandUniProtReferencePlugin implements PlugableAction {

    @Override public List<String> getMenuPath() {
        return Arrays.asList("Tools", "Annotation");
    }

    @Override public Class<? extends ControlAction> action() {
        return ExpandUniProtReference.class;
    }

    @SuppressWarnings("unchecked")
    @Override public ContextResponder getContext() {
        return new ContextResponder() {
            @Override
            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                if (active == null || !selection.hasSelection(ProteinProduct.class))
                    return false;
                if (!DefaultServiceManager.getInstance()
                                          .hasService(UniProtIdentifier.class,
                                                      CrossReferenceService.class))
                    return false;
                for (GeneProduct p : selection.get(ProteinProduct.class)) {
                    for (CrossReference ref : p
                            .getAnnotations(CrossReference.class)) {
                        if (ref.getIdentifier() instanceof UniProtIdentifier)
                            return true;
                    }
                }
                return false;
            }
        };
    }
}
