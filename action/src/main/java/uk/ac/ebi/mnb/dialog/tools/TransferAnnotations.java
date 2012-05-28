/**
 * Annotate.java
 *
 * 2011.10.13
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
package uk.ac.ebi.mnb.dialog.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.DefaultIdentifierFactory;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.ProteinProductImpl;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.identifier.IdentifierSet;
import uk.ac.ebi.mdk.domain.identifier.SwissProtIdentifier;
import uk.ac.ebi.mdk.domain.observation.Observation;
import uk.ac.ebi.mdk.domain.observation.sequence.LocalAlignment;
import uk.ac.ebi.mdk.service.query.CrossReferenceService;
import uk.ac.ebi.mdk.service.query.crossreference.UniProtCrossReferenceService;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.util.Collection;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 * @name Annotate - 2011.10.13 <br>
 * Class description
 */
public class TransferAnnotations
        extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(TransferAnnotations.class);

    public TransferAnnotations(JFrame frame,
                               TargetedUpdate updater,
                               ReportManager messages,
                               SelectionController controller,
                               UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");
        setDefaultLayout();
    }

    @Override
    public void process() {

        CrossReferenceService<SwissProtIdentifier> service = new UniProtCrossReferenceService();
        service.startup();


        DefaultIdentifierFactory factory = DefaultIdentifierFactory.getInstance();
        for (GeneProduct product : getSelection().getGeneProducts()) {

            Multimap<Identifier, Observation> identifiers = HashMultimap.create();

            Collection<Observation> alignments = ((ProteinProductImpl) product).getObservationCollection().get(
                    LocalAlignment.class);
            for (Observation observation : alignments) {
                LocalAlignment alignment = (LocalAlignment) observation;
                IdentifierSet set = factory.resolveSequenceHeader(alignment.getSubject());
                for (Identifier identifier : set.getSubIdentifiers(SwissProtIdentifier.class)) {
                    for (Identifier xref : service.getCrossReferences((SwissProtIdentifier) identifier)) {
                        identifiers.put(xref, observation);
                    }
                }
            }

            for (Identifier identifier : identifiers.keySet()) {
                CrossReference xref = new CrossReference(identifier);
                xref.addObservations(identifiers.get(identifier));
                product.addAnnotation(xref);
            }

        }
    }

}
