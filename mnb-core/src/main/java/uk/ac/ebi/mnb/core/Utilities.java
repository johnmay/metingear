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
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.MetaboliteImplementation;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.entities.GeneProduct;

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
    public static Collection<MetaboliteImplementation> getMetabolites(Collection<AnnotatedEntity> entities) {

        Collection<MetaboliteImplementation> metabolites = new ArrayList();

        for (AnnotatedEntity entity : entities) {
            if (entity instanceof MetaboliteImplementation) {
                metabolites.add((MetaboliteImplementation) entity);
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

    public static Collection<GeneProduct> getGeneProducts(Collection<AnnotatedEntity> entities) {

        Collection<GeneProduct> products = new ArrayList();

        for (AnnotatedEntity entity : entities) {
            if (entity instanceof GeneProduct) {
                products.add((GeneProduct) entity);
            }
        }

        return products;
    }
}
