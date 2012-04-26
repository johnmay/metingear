/**
 * ProjectEntityResolver.java
 *
 * 2011.12.05
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
package mnb.io.tabular;

import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.resource.basic.BasicChemicalIdentifier;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.Metabolite;

import java.util.HashMap;
import java.util.Map;


/**
 * ProjectEntityResolver - 2011.12.05 <br>
 * Resolves entities (from the current project) using the name
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class NamedEntityResolver implements EntityResolver {

    private static final Logger LOGGER = Logger.getLogger(NamedEntityResolver.class);

    private Map<String, Metabolite> nameMap = new HashMap<String, Metabolite>();

    private Map<String, Metabolite> created = new HashMap<String, Metabolite>();


    public NamedEntityResolver() {

        for (Metabolite m : DefaultReconstructionManager.getInstance().getActive().getMetabolome()) {

            if (nameMap.containsKey(m.getName())) {
                LOGGER.error("Name clash! " + m.getName());
            }

            nameMap.put(m.getName(), m);

        }

    }


    public Metabolite getNonReconciledMetabolite(String name) {

        if (!created.containsKey(name)) {
            Metabolite m = DefaultEntityFactory.getInstance().newInstance(Metabolite.class,
                                                                          new BasicChemicalIdentifier(),
                                                                          name,
                                                                          name);
            created.put(name, m);
        }

        return created.get(name);

    }


    public Metabolite getReconciledMetabolite(String name) {
        return nameMap.get(name);
    }
}
