/**
 * TargetedUpdate.java
 *
 * 2011.10.14
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
package uk.ac.ebi.mnb.interfaces;

import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;

/**
 * @name    TargetedUpdate - 2011.10.14 <br>
 *          Interface extends updatable by allow targeted updates
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public interface TargetedUpdate extends Updatable {

    /**
     * Targeted update of entities specified in the selection. The update will
     * not select the provided entities.
     * @param selection
     * @return
     */
    public boolean update(EntityCollection selection);
}
