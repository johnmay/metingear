
/**
 * EntryReconciler.java
 *
 * 2011.09.23
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
package mnb.io.resolve;

import mnb.io.tabular.preparse.PreparsedEntry;
import uk.ac.ebi.core.AbstractAnnotatedEntity;


/**
 *          EntryReconciler – 2011.09.23 <br>
 *          Interface to allow reconciliation through different means such as console, swing or
 *          automated
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public interface EntryReconciler {

    public AbstractAnnotatedEntity resolve(PreparsedEntry entry);

}

