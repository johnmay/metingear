
/**
 * AutomatedReconciler.java
 *
 * 2011.09.23
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
package mnb.io.resolve;

import com.google.common.collect.Multimap;
import mnb.io.tabular.preparse.PreparsedEntry;
import mnb.io.tabular.preparse.PreparsedMetabolite;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.core.AbstractAnnotatedEntity;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.metabolomes.webservices.util.CandidateFactory;
import uk.ac.ebi.metabolomes.webservices.util.SynonymCandidateEntry;
import uk.ac.ebi.resource.chemical.BasicChemicalIdentifier;
import uk.ac.ebi.resource.chemical.KEGGCompoundIdentifier;


/**
 *          AutomatedReconciler – 2011.09.23 <br>
 *          Automated reconciler assign description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AutomatedReconciler
  implements EntryReconciler {

    private static final Logger LOGGER = Logger.getLogger(AutomatedReconciler.class);
    private CandidateFactory factory;
    private Identifier template;


    public AutomatedReconciler(CandidateFactory factory, Identifier factoryIdClass) {
        this.factory = factory;
        this.template = factoryIdClass;
    }


    /**
     * @param entry
     * @return
     * @inheritDoc
     */
    public AbstractAnnotatedEntity resolve(PreparsedEntry entry) {
        if( entry instanceof PreparsedMetabolite ) {
            return resolve((PreparsedMetabolite) entry);
        }
        return null;
    }


    private static int ticker = 0;


    /**
     * Automatically resolves
     * @param entry
     * @return
     */
    public Metabolite resolve(PreparsedMetabolite entry) {

        String name = entry.getName();

        Multimap<Integer, SynonymCandidateEntry> map = factory.getSynonymCandidates(name);
        Metabolite metabolite = new Metabolite();

        // add the annotations to a new metabolite
        metabolite.setIdentifier(new BasicChemicalIdentifier("Met" + ++ticker));
        metabolite.setAbbreviation(entry.getAbbreviation());
        metabolite.setName(name);


        // contains a candidate with a score of 0
        if( map.containsKey(0) ) {
            for( SynonymCandidateEntry candidate : map.get(0) ) {
                Identifier id = template.newInstance();
                id.setAccession(candidate.getId());
                metabolite.addAnnotation(new CrossReference(id));
            }
        }

        // molecula formula
        if( entry.getFormula() != null ) {
            metabolite.addAnnotation(new MolecularFormula(entry.getFormula()));
        }

        // adds the kegg xref
        if( entry.getKEGGXREF() != null ) {
            KEGGCompoundIdentifier keggId = new KEGGCompoundIdentifier(entry.getKEGGXREF());
            metabolite.addAnnotation(new CrossReference<KEGGCompoundIdentifier>(keggId));
        }


        return metabolite;

    }


}

