/**
 * PreparsedReaction.java
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

import mnb.io.tabular.type.TableDescription;
import mnb.io.tabular.type.ReactionColumn;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *          PreparsedReaction – 2011.08.30 <br>
 *          Class to hold pre-parsed information on reactions
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class PreparsedReaction extends PreparsedEntry {

    private static final Logger LOGGER = Logger.getLogger(PreparsedReaction.class);

    public PreparsedReaction() {
        super(ReactionColumn.class);
    }

    /**
     *
     * Copy constructor
     *
     * @param entry
     */
    public PreparsedReaction(PreparsedEntry entry) {
        super(ReactionColumn.class);
        for (Entry<TableDescription, Integer> e : entry.getColumnSet()) {
            super.addValue(e.getKey(), entry.getValue(e.getKey()));
        }
    }

    public String getIdentifier() {
        return super.getValue(ReactionColumn.ABBREVIATION);
    }

    public String getDescription() {
        return super.getValue(ReactionColumn.DESCRIPTION);
    }

    public String[] getClassifications() {
        return super.getValues(ReactionColumn.CLASSIFICATION);
    }

    public String getEquation() {
        return super.getValue(ReactionColumn.EQUATION);
    }

    public String getSubsystem() {
        return super.getValue(ReactionColumn.SUBSYSTEM);
    }

    public String getDirection() {
        return super.getValue(ReactionColumn.DIRECTION);
    }

    public String[] getLoci() {
        return super.getValues(ReactionColumn.LOCUS);
    }

    @Override
    public String toString() {
        return getIdentifier() != null ? getIdentifier()
               : getDescription() != null ? getDescription() : "unamed reaction";
    }
}
