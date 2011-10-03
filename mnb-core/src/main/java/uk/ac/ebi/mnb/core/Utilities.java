/**
 * Utilities.java
 *
 * 2011.10.03
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
package uk.ac.ebi.mnb.core;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.Metabolite;

/**
 * @name    Utilities - 2011.10.03 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class Utilities {

    private static final Logger LOGGER = Logger.getLogger(Utilities.class);

    /**
     * Access only metabolites from an annotated entity collection
     * @param entities
     * @return
     */
    public static Collection<Metabolite> getMetabolites(Collection<AnnotatedEntity> entities) {

        Collection<Metabolite> metabolites = new ArrayList();

        for (AnnotatedEntity entity : entities) {
            if (entity instanceof Metabolite) {
                metabolites.add((Metabolite) entity);
            }
        }

        return metabolites;

    }

    /**
     * Access only reactions from an annotated entity collection
     * @param entities
     * @return
     */
    public static Collection<MetabolicReaction> getReactions(Collection<AnnotatedEntity> entities) {

        Collection<MetabolicReaction> reactions = new ArrayList();

        for (AnnotatedEntity entity : entities) {
            if (entity instanceof MetabolicReaction) {
                reactions.add((MetabolicReaction) entity);
            }
        }

        return reactions;

    }
}
