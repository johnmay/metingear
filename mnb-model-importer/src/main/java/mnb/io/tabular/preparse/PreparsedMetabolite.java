
/**
 * PreparsedMetabolite.java
 *
 * 2011.08.30
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
package mnb.io.tabular.preparse;

import java.util.ArrayList;
import java.util.Collection;
import mnb.io.tabular.type.EntityColumn;
import org.apache.log4j.Logger;
import uk.ac.ebi.metabolomes.webservices.util.CandidateEntry;


/**
 *          PreparsedMetabolite – 2011.08.30 <br>
 *          Class to hold data of a metabolite entity prior to parsing. The class adds a collection
 *          of candidate entries which allow the parser to deside on the name
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class PreparsedMetabolite extends PreparsedEntry {

    private static final Logger LOGGER = Logger.getLogger(PreparsedMetabolite.class);
    private Collection<CandidateEntry> candidates = new ArrayList<CandidateEntry>();


    public PreparsedMetabolite() {
        super(EntityColumn.class);
    }


    public String getAbbreviation() {
        return getValue(EntityColumn.ABBREVIATION);
    }


    public String getName() {
        return getValue(EntityColumn.NAME);
    }


    public String getKEGGXREF() {
        return getValue(EntityColumn.KEGG_XREF);
    }


    public String getFormula() {
        return getValue(EntityColumn.FORMULA);
    }


    public boolean addCandidate(CandidateEntry candidate) {
        return candidates.add(candidate);
    }


    @Override
    public String toString() {
        return getName();
    }


}

