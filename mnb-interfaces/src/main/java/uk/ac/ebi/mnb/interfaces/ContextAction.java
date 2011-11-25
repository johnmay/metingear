/**
 * ContextAction.java
 *
 * 2011.11.25
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

/**
 *          ContextAction - 2011.11.25 <br>
 *          Context action defines a way to test whether an action is available
 *          based on project contents/selection
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public interface ContextAction {

    /**
     * Sets the context based on properties of the project or current selection
     * (if available)
     * @return
     */
    public boolean setContext();

    /**
     * Sets the context given a specific object
     * @param obj
     * @return 
     */
    public boolean setContext(Object obj);
}
