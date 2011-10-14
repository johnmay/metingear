/**
 * FormulaSearch.java
 *
 * 2011.10.04
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

import java.awt.event.ActionEvent;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.AuthorAnnotation;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.annotation.crossreference.ChEBICrossReference;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
import uk.ac.ebi.mnb.core.ContextAction;
import uk.ac.ebi.mnb.core.Utilities;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.resource.chemical.ChEBIIdentifier;

/**
 * @name    FormulaSearch - 2011.10.04 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class FormulaSearch extends ContextAction {

    private static final Logger LOGGER = Logger.getLogger(FormulaSearch.class);
    private ChEBIWebServiceConnection chebi = null;

    ;

    public FormulaSearch(MainController controller) {
        super(FormulaSearch.class.getSimpleName(), controller);
    }

    public void actionPerformed(ActionEvent ae) {

        chebi = chebi == null ? new ChEBIWebServiceConnection() : chebi;

        for (Metabolite m : Utilities.getMetabolites(getSelection().getEntities())) {

            MolecularFormula annotation = m.getAnnotations(MolecularFormula.class).iterator().next();

            for (Entry<String, String> e : chebi.searchByFormula(annotation.toString()).entrySet()) {
                m.addAnnotation(new ChEBICrossReference(new ChEBIIdentifier(e.getKey())));
            }

        }

        addMessage(new WarningMessage("Completing formula search"));

        update();

    }
}
