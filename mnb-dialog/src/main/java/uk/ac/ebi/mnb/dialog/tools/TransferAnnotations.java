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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.core.IdentifierSet;
import uk.ac.ebi.core.ProteinProduct;
import uk.ac.ebi.interfaces.GeneProduct;
import uk.ac.ebi.interfaces.Observation;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.observation.sequence.LocalAlignment;
import uk.ac.ebi.resource.IdentifierFactory;
import uk.ac.ebi.resource.protein.UniProtIdentifier;
import uk.ac.ebi.io.xml.UniProtAnnoationLoader;

/**
 * @name    Annotate - 2011.10.13 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class TransferAnnotations extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(TransferAnnotations.class);

    public TransferAnnotations(JFrame frame, TargetedUpdate updater, MessageManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");
        setDefaultLayout();
    }

    @Override
    public void process() {
        UniProtAnnoationLoader loader = new UniProtAnnoationLoader();
        loader.load(); // load from local file


        IdentifierFactory factory = IdentifierFactory.getInstance();
        for (GeneProduct product : getSelection().getGeneProducts()) {

            Set<Identifier> identifiers = new HashSet();

            Collection<Observation> alignments = ((ProteinProduct) product).getObservationCollection().get(LocalAlignment.class);
            for (Observation observation : alignments) {
                LocalAlignment alignment = (LocalAlignment) observation;
                IdentifierSet set = factory.resolveSequenceHeader(alignment.getSubject());
                for (Identifier identifier : set.getSubIdentifiers(UniProtIdentifier.class)) {
                    if (loader.getMap().containsKey(identifier)) {
                        System.out.println(loader.getMap().get((UniProtIdentifier) identifier));
                        identifiers.addAll(loader.getMap().get((UniProtIdentifier) identifier));
                    } else {
                        System.out.println(loader.getMap().keySet());
                    }
                }
            }

            for (Identifier identifier : identifiers) {
                product.addAnnotation(new CrossReference(identifier));
            }

        }
    }
}
