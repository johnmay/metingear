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

import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.ProteinProduct;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.identifier.UniProtIdentifier;
import uk.ac.ebi.mdk.service.DefaultServiceManager;
import uk.ac.ebi.mdk.service.ProgressListener;
import uk.ac.ebi.mdk.service.ServiceManager;
import uk.ac.ebi.mdk.service.query.CrossReferenceService;
import uk.ac.ebi.metingear.AppliableEdit;
import uk.ac.ebi.metingear.view.AbstractControlAction;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;
import uk.ac.ebi.mnb.edit.RemoveAnnotationEdit;

import javax.swing.undo.CompoundEdit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/** @author John May */
public final class ExpandUniProtReference extends AbstractControlAction {

    /** the service manager. */
    private final ServiceManager services = DefaultServiceManager.getInstance();
    private final AnnotationFactory annotations = DefaultAnnotationFactory
            .getInstance();
    private static final long NANO_SECONDS_IN_A_SECOND = 1000000000L;
    private static final int ESTIMATE_THRESHOLD = 50;

    @Override public void process(final ProgressListener listener) {
        @SuppressWarnings("unchecked")
        CrossReferenceService<UniProtIdentifier> service = services
                .getService(UniProtIdentifier.class,
                            CrossReferenceService.class);

        long t0 = System.nanoTime();

        CompoundEdit compoundEdit = new CompoundEdit();
        Collection<ProteinProduct> selection = getSelection(ProteinProduct.class);
        int done = 0;
        for (final GeneProduct p : selection) {

            long elapsed = System.nanoTime() - t0;
            long avg = done == 0 ? elapsed : elapsed / done;
            long remaining = selection.size() - done;
            long estimate = remaining * avg / NANO_SECONDS_IN_A_SECOND;

            if (done > ESTIMATE_THRESHOLD) {
                listener.progressed("[" + estimate + " s left]: " + p
                        .getName() + "...");
            } else {
                listener.progressed("expanding " + p.getName() + "...");
            }

            Collection<CrossReference> refs = new ArrayList<CrossReference>(p.getAnnotations(CrossReference.class));

            // we remove the existing cross-references as to avoid duplicates (using a set)
            AppliableEdit removeEdit = new RemoveAnnotationEdit(p, refs);
            AppliableEdit addEdit = new AddAnnotationEdit(p, expand(service, refs));

            compoundEdit.addEdit(removeEdit);
            compoundEdit.addEdit(addEdit);

            // actually remove and add the annotations
            removeEdit.apply();
            addEdit.apply();

            done++;
        }
        compoundEdit.end();
        addEdit(compoundEdit);
    }

    private Collection<Annotation> expand(final CrossReferenceService<UniProtIdentifier> service,
                                          final Collection<CrossReference> refs) {
        Set<Annotation> expanded = new HashSet<Annotation>();
        expanded.addAll(refs);
        for (CrossReference ref : refs) {
            if (ref.getIdentifier() instanceof UniProtIdentifier) {
                UniProtIdentifier upid = (UniProtIdentifier) ref
                        .getIdentifier();
                for (final Identifier id : service.getCrossReferences(upid)) {
                    expanded.add(annotations.getCrossReference(id));
                }
            }
        }
        return expanded;
    }
}
