/**
 * LabelPool.java
 *
 * 2011.10.04
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
package uk.ac.ebi.mnb.view.labels;

import org.apache.log4j.Logger;

/**
 * @name    LabelPool - 2011.10.04 <br>
 *          Singleton description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class LabelPool {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(LabelPool.class);

    private LabelPool() {
    }

    public static LabelPool getInstance() {
        return LabelPoolHolder.INSTANCE;
    }

    private static class LabelPoolHolder {
        private static final LabelPool INSTANCE = new LabelPool();
    }




}
