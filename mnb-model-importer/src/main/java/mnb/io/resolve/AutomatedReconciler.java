/**
 * AutomatedReconciler.java
 *
 * 2011.09.23
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet. If not, see <http://www.gnu.org/licenses/>.
 */
package mnb.io.resolve;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.IOException;
import java.util.Collection;
import mnb.io.tabular.preparse.PreparsedEntry;
import mnb.io.tabular.preparse.PreparsedMetabolite;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.Synonym;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.annotation.crossreference.KEGGCrossReference;
import uk.ac.ebi.core.AbstractAnnotatedEntity;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.metabolomes.webservices.util.CandidateFactory;
import uk.ac.ebi.metabolomes.webservices.util.SynonymCandidateEntry;
import uk.ac.ebi.resource.chemical.BasicChemicalIdentifier;
import uk.ac.ebi.resource.chemical.KEGGCompoundIdentifier;


/**
 * AutomatedReconciler – 2011.09.23 <br> Automated reconciler assign description
 *
 * @version $Rev$ : Last Changed $Date: 2011-11-19 10:15:40 +0000 (Sat, 19
 * Nov 2011) $
 * @author johnmay
 * @author $Author$ (this version)
 */
public class AutomatedReconciler
        implements EntryReconciler {

    private static final Logger LOGGER = Logger.getLogger(AutomatedReconciler.class);

    private CandidateFactory factory;

    private Identifier template;

    private Reconstruction recon;

    private Multimap<String, Metabolite> nameMap;


    public AutomatedReconciler(CandidateFactory factory, Identifier factoryIdClass) {
        this.factory = factory;
        this.template = factoryIdClass;

        recon =  ReconstructionManager.getInstance().getActive();
        nameMap = HashMultimap.create();
        if (recon != null && !recon.getMetabolites().isEmpty()) {
            for (Metabolite m : recon.getMetabolites()) {
                nameMap.put(m.getName(), m);
            }
        }

    }


    /**
     * @param entry
     * @return @inheritDoc
     */
    public AbstractAnnotatedEntity resolve(PreparsedEntry entry) {
        if (entry instanceof PreparsedMetabolite) {
            return resolve((PreparsedMetabolite) entry);
        }
        return null;
    }

    private static int ticker = 0;


    /**
     * Automatically resolves
     *
     * @param entry
     * @return
     */
    public Metabolite resolve(PreparsedMetabolite entry) {

        String[] names = entry.getNames();

        String name = names.length > 0 ? names[0] : "Unamed metabolite";

        if (nameMap.containsKey(name)) {
            Collection<Metabolite> candidates = nameMap.get(name);
            if (candidates.size() == 1) {
                return candidates.iterator().next();
            } else {
                LOGGER.error("Duplicate metabolites with same name!");
            }
        }

        Metabolite metabolite = new Metabolite(BasicChemicalIdentifier.nextIdentifier(),
                                               entry.getAbbreviation(), name);

        for (int i = 1; i < names.length; i++) {
            metabolite.addAnnotation(new Synonym(names[i]));
        }

        try {
            Multimap<Integer, SynonymCandidateEntry> map = factory.getSynonymCandidates(name);

            // contains a candidate with a score of 0
            if (map.containsKey(0)) {
                for (SynonymCandidateEntry candidate : map.get(0)) {
                    Identifier id = template.newInstance();
                    id.setAccession(candidate.getId());
                    metabolite.addAnnotation(new CrossReference(id));
                }
            }
        } catch (ExceptionInInitializerError ex) {
            LOGGER.info("Unable to resolve candidates: " + ex.getMessage());
        }

        // molecula formula
        if (entry.getFormula() != null) {
            metabolite.addAnnotation(new MolecularFormula(entry.getFormula()));
        }

        // adds the kegg xref
        for (String xref : entry.getKEGGXREFs()) {
            KEGGCompoundIdentifier keggId = new KEGGCompoundIdentifier(xref);
            metabolite.addAnnotation(new KEGGCrossReference(keggId));
        }


        return metabolite;

    }
}
