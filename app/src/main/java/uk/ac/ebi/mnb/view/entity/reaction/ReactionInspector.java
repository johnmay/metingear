/**
 * ReactionInspector.java
 *
 * 2011.09.26
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
package uk.ac.ebi.mnb.view.entity.reaction;

import com.jgoodies.forms.layout.CellConstraints;
import uk.ac.ebi.mnb.view.entity.AbstractEntityInspector;
import org.apache.log4j.Logger;

/**
 *          ReactionInspector – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReactionInspector
        extends AbstractEntityInspector {

    private static final Logger LOGGER = Logger.getLogger(ReactionInspector.class);
    private CellConstraints cc = new CellConstraints();

    public ReactionInspector() {
        super(new ReactionPanel());

    }

    

    @Override
    public void store() {
        super.store();
    }

    
}
