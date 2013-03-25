/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
package mnb.io.tabular;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicChemicalIdentifier;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.Metabolite;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


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
    private static Pattern clean = Pattern.compile("[^A-z0-9]+");
    private static Pattern split = Pattern.compile("\\s+|\\-+");
    private Map<String, Metabolite> nameMap = new HashMap<String, Metabolite>();

    private Map<String, Metabolite> created = new HashMap<String, Metabolite>();


    public NamedEntityResolver() {

        for (Metabolite m : DefaultReconstructionManager.getInstance().active().getMetabolome()) {

            if (nameMap.containsKey(m.getName())) {
                LOGGER.error("Name clash! " + m.getName());
            }

            nameMap.put(m.getName(), m);

        }

    }


    public Metabolite getNonReconciledMetabolite(String name) {

        if (!created.containsKey(name)) {
            Metabolite m = DefaultEntityFactory.getInstance().newInstance(Metabolite.class,
                                                                          BasicChemicalIdentifier.nextIdentifier(),
                                                                          name,
                                                                          createAbbreviation(name, 4));
            created.put(name, m);
        }

        return created.get(name);

    }

    public static String createAbbreviation(String text, int target) {

        if (text.length() < target) {
            return text.toLowerCase(Locale.ENGLISH);
        }

        StringBuilder builder = new StringBuilder(6);

        String[] chunks = split.split(text.trim());

        int extra = (target - chunks.length) / chunks.length;

        for (String chunk : chunks) {
            chunk = clean.matcher(chunk).replaceAll("");
            if (!chunk.isEmpty())
                builder.append(chunk.charAt(0));
            for (int i = 0; i < extra && chunk.length() > i + 1; i++)
                builder.append(chunk.charAt(i + 1));
        }

        return builder.toString().toLowerCase(Locale.ENGLISH);

    }


    public Metabolite getReconciledMetabolite(String name) {
        return nameMap.get(name);
    }
}
