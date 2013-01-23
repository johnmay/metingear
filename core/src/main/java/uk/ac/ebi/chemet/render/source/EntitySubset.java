package uk.ac.ebi.chemet.render.source;

/**
 * EntitySubset.java
 *
 * 2012.01.30
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
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mnb.core.EntityMap;


/**
 *
 *          EntitySubset 2012.01.30
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *
 *          Class describes an extension to {@see EntityMap} that provides a 
 *          name and parent for the subset.
 *
 */
public class EntitySubset extends EntityMap {

    private static final Logger LOGGER = Logger.getLogger(EntitySubset.class);

    private String name;

    private Object parent;


    public EntitySubset() {
        this("Unamed Subset", null);
    }


    public EntitySubset(String name, Object parent) {
        super(DefaultEntityFactory.getInstance());
        this.name = name;
        this.parent = parent;
    }


    public String getName() {
        return name;
    }


    public Object getParent() {
        return parent;
    }
}
