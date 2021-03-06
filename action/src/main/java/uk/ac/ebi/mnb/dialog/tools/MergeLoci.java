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
import uk.ac.ebi.mdk.domain.annotation.Locus;
import uk.ac.ebi.mdk.domain.entity.MultimerImpl;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.Proteome;
import uk.ac.ebi.mdk.domain.entity.collection.Reactome;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;


/**
 * @name    MergeLoci - 2011.10.13 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MergeLoci extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(MergeLoci.class);


    public MergeLoci(MainController controller) {
        super(MergeLoci.class.getSimpleName(), controller);
    }


    public void actionPerformed(ActionEvent e) {

        Reconstruction recon = DefaultReconstructionManager.getInstance().active();

        Multimap<String, MetabolicReaction> monomeric = HashMultimap.create();
        Multimap<String, MetabolicReaction> multimeric = HashMultimap.create();

        Reactome reactome = recon.reactome();
        for (MetabolicReaction rxn : reactome) {
            for (Locus locus : rxn.getAnnotations(Locus.class)) {
                if (locus.containsMultiple()) {
                    multimeric.put(locus.toString(), rxn);
                } else {
                    monomeric.put(locus.toString(), rxn);
                }
            }
        }

        Proteome proteome = recon.proteome();
        throw new UnsupportedOperationException("deprecated");
        // monomeric
//        for (String locus : monomeric.keySet()) {
//            for (GeneProduct product : proteome.get(locus)) {
//                for (MetabolicReaction rxn : monomeric.get(locus)) {
//                    rxn.addModifier(product); // needs to be an add
//                }
//            }
//        }
//
//        throw new UnsupportedOperationException("deprecated");

//        // multimeric
//        for (String locusAnnotation : multimeric.keySet()) {
//
//            String[] loci = locusAnnotation.split("\\+");
//            GeneProduct[] subunits = new GeneProduct[loci.length];
//            for (int i = 0; i < loci.length; i++) {
//                subunits[i] = proteome.get(loci[i]).iterator().next();
//            }
//
//            GeneProduct product = new MultimerImpl(subunits);
//            recon.getProducts().add(product);
//
//            for (MetabolicReaction rxn : multimeric.get(locusAnnotation)) {
//                rxn.addModifier(product);
//            }
//        }

 //       update();

    }
}
