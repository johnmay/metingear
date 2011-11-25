
/**
 * BioMartMain.java
 *
 * 2011.11.24
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
import org.apache.log4j.Logger;
import org.biomart.martservice.*;
import org.biomart.martservice.query.Attribute;
import org.biomart.martservice.query.Filter;
import org.biomart.martservice.query.Query;

/**
 *          BioMartMain - 2011.11.24 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class BioMartMain {

    private static final Logger LOGGER = Logger.getLogger(BioMartMain.class);
    private static final String location = "http://www.ebi.ac.uk/interpro/biomart/martservice";
    private static final MartService service = MartService.getMartService(location);

    public static void main(String[] args) throws MartServiceException, ResultReceiverException {

        MartDataset dataset = service.getDataset("default", "protein");
        MartQuery query = new MartQuery(service, dataset, "methodMatchQuery");

        // add filters
        query.addFilter(dataset.getName(), new Filter("protein_accession", "Q00987"));

        // add attributes
        query.addAttribute(dataset.getName(), new Attribute("protein_accession"));
        query.addAttribute(dataset.getName(), new Attribute("method_id"));


        Query q = query.getQuery();
        q.setUniqueRows(1);

        service.executeQuery(q, new ResultReceiver() {

            public void receiveResult(Object[] os, long l) throws ResultReceiverException {
                for (Object object : os) {
                    System.out.println(object.getClass());
                }
            }

            public void receiveError(String string, long l) throws ResultReceiverException {
                LOGGER.error(string);
            }
        });


    }
}
