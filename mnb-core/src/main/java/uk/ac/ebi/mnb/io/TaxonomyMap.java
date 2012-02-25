
/**
 * TaxonomyMap.java
 *
 * 2011.10.01
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
package uk.ac.ebi.mnb.io;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import uk.ac.ebi.resource.organism.Kingdom;
import uk.ac.ebi.resource.organism.Taxonomy;


/**
 *          TaxonomyMap – 2011.10.01 <br>
 *          Loads and maps Taxon/Mneomic to Taxonomic Identifier
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
@Deprecated
public class TaxonomyMap {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      TaxonomyMap.class);
    private InputStream stream = getClass().getResourceAsStream(
      "flatfiles/uniprot-organism-info.tsv");
    private Map<String, Taxonomy> codeMap = new HashMap();
    private Map<Integer, Taxonomy> taxonMap = new HashMap();


    private TaxonomyMap() {
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(new BufferedInputStream(stream,10000)), '\t', '\0');
            reader.readNext();
            String[] row;
            while( (row = reader.readNext()) != null ) {
                int taxon = Integer.parseInt(row[0]);
                String code = row[1];
                String officialName = row[2];
                String commonName = row[3];
                String kingdomString = row[4];
                Kingdom kingdom = Kingdom.getKingdom(kingdomString);

                Taxonomy taxonomy = new Taxonomy(taxon,
                                                 code,
                                                 kingdom,
                                                 officialName,
                                                 commonName);
                codeMap.put(code, taxonomy);
                taxonMap.put(taxon, taxonomy);
            }
        } catch( IOException ex ) {
            ex.printStackTrace();
        }
    }


    public static TaxonomyMap getInstance() {
        return OrganismMnemoicMapHolder.INSTANCE;
    }


    private static class OrganismMnemoicMapHolder {

        private static final TaxonomyMap INSTANCE = new TaxonomyMap();
    }


    public Taxonomy get(String code) {
        return codeMap.get(code);
    }


    public Taxonomy get(Integer taxon) {
        return taxonMap.get(taxon);
    }


}

