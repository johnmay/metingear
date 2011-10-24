/**
 * MergeLoci.java
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
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.Locus;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.core.product.ProductCollection;
import uk.ac.ebi.core.reaction.ReactionList;
import uk.ac.ebi.interfaces.GeneProduct;
import uk.ac.ebi.mnb.core.ContextAction;
import uk.ac.ebi.mnb.interfaces.MainController;

/**
 * @name    MergeLoci - 2011.10.13 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MergeLoci extends ContextAction {

    private static final Logger LOGGER = Logger.getLogger(MergeLoci.class);

    public MergeLoci(MainController controller) {
        super(MergeLoci.class.getSimpleName(), controller);
    }

    public void actionPerformed(ActionEvent e) {

        Reconstruction recon = ReconstructionManager.getInstance().getActiveReconstruction();

        Multimap<String, MetabolicReaction> map = HashMultimap.create();

        ReactionList rxns = recon.getReactions();
        for (MetabolicReaction rxn : rxns) {
            for (Locus locus : rxn.getAnnotations(Locus.class)) {
                map.put(locus.toString(), rxn);
            }
        }

        ProductCollection products = recon.getProducts();

        for (GeneProduct product : products) {
            String accession = product.getAccession();
            if (map.containsKey(accession)) {
                for (MetabolicReaction rxn : map.get(accession)) {
                    rxn.addModifier(product); // needs to be an add
                }
            } else {
                System.out.println("no match found: MergeLoci");
            }
        }

    }
}
