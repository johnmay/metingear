
/**
 * UnparsableReactionError.java
 *
 * 2011.08.31
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
package mnb.io.tabular.parser;

import org.apache.log4j.Logger;


/**
 *          UnparsableReactionError – 2011.08.31 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class UnparsableReactionError extends Exception {

    private static final Logger LOGGER = Logger.getLogger( UnparsableReactionError.class );


    public UnparsableReactionError( String message ) {
        super( message );
    }

    

}

