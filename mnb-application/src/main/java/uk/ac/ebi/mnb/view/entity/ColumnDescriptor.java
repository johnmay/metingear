
/**
 * ColumnDescriptor.java
 *
 * 2011.09.06
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
package uk.ac.ebi.mnb.view.entity;

import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.Annotation;


/**
 *          ColumnDescriptor – 2011.09.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ColumnDescriptor {

    private static final Logger LOGGER = Logger.getLogger(ColumnDescriptor.class);
    private String name;
    private Class accessClass;
    private ColumnAccessType accessType;
    private Class dataClass;


    public ColumnDescriptor(Annotation annotation) {
        this.name = annotation.getShortDescription();
        this.accessClass = annotation.getClass();
        this.accessType = ColumnAccessType.ANNOTATION;
        this.dataClass = annotation.getClass();
    }


    public ColumnDescriptor(String name,
                            Class accessClass,
                            ColumnAccessType type,
                            Class dataClass) {
        this.name = name;
        this.accessClass = accessClass;
        this.accessType = type;
        this.dataClass = dataClass;
    }


    public Class getAccessClass() {
        return accessClass;
    }


    public String getName() {
        return name;
    }


    public ColumnAccessType getType() {
        return accessType;
    }


    public Class getDataClass() {
        return dataClass;
    }


}

