/**
 * ItemSelector.java
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
package uk.ac.ebi.mnb.interfaces;

import java.util.Collection;
import uk.ac.ebi.interfaces.AnnotatedEntity;

/**
 * @name    ItemSelector - 2011.10.03 <br>
 *          Interface description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public interface SelectionController extends Updatable {

    /**
     * Returns the selection from the current context
     * @return
     */
    public SelectionManager getSelection();

    /**
     * Sets the selection on the current context
     * @param selection
     * @return
     */
    public boolean setSelection(SelectionManager selection);
    
}
