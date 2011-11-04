/**
 * UserReconciler.java
 *
 * 2011.10.31
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import mnb.io.tabular.preparse.PreparsedEntry;
import mnb.io.tabular.preparse.PreparsedMetabolite;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.Synonym;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.annotation.crossreference.KEGGCrossReference;
import uk.ac.ebi.core.AbstractAnnotatedEntity;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.metabolomes.webservices.util.CandidateFactory;
import uk.ac.ebi.metabolomes.webservices.util.SynonymCandidateEntry;
import uk.ac.ebi.resource.chemical.BasicChemicalIdentifier;
import uk.ac.ebi.resource.chemical.KEGGCompoundIdentifier;
import uk.ac.ebi.visualisation.molecule.MoleculeTable;

/**
 *          UserReconciler - 2011.10.31 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ListSelectionReconciler implements EntryReconciler {

    private static final Logger LOGGER = Logger.getLogger(AutomatedReconciler.class);
    private CandidateFactory factory;
    private Identifier template;
    private JFrame frame;
    private CandidateSelector dialog;

    public ListSelectionReconciler(JFrame frame, CandidateFactory factory, Identifier factoryIdClass) {
        this.factory = factory;
        this.template = factoryIdClass;
        this.frame = frame;
        dialog = new CandidateSelector(frame);
    }

    /**
     * @param entry
     * @return
     * @inheritDoc
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
     * @param entry
     * @return
     */
    public Metabolite resolve(PreparsedMetabolite entry) {

        String[] names = entry.getNames();

        String name = names.length > 0 ? names[0] : "Unamed metabolite";
        Metabolite metabolite = new Metabolite();

        for (int i = 1; i < names.length; i++) {
            metabolite.addAnnotation(new Synonym(names[i]));
        }

        // add the annotations to a new metabolite
        metabolite.setIdentifier(BasicChemicalIdentifier.nextIdentifier());
        metabolite.setAbbreviation(entry.getAbbreviation());
        metabolite.setName(name);

        try {
            Multimap<Integer, SynonymCandidateEntry> map = factory.getSynonymCandidates(name);

            // contains a candidate with a score of 0
            if (map.containsKey(0)) {
                for (SynonymCandidateEntry candidate : map.get(0)) {
                    Identifier id = template.newInstance();
                    id.setAccession(candidate.getId());
                    metabolite.addAnnotation(new CrossReference(id));
                }
            } else {
                map = factory.getFuzzySynonymCandidates(name);
                Collection<SynonymCandidateEntry> candidates = new ArrayList();
                List<Integer> scores = new ArrayList<Integer>(map.keySet());
                Collections.sort(scores);
                for (Integer score : scores) {
                    candidates.addAll(map.get(score));
                }
                if (!candidates.isEmpty()) {
                    dialog.setNameAndCandidates(name, candidates);
                    dialog.setVisible(true);
                    Collection<Metabolite> selected = dialog.getSelected();
                    for (Metabolite m : selected) {
                        metabolite.addAnnotations(m.getAnnotations());
                        m.addAnnotation(new CrossReference(m.getIdentifier()));
                    }
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
