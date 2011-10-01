
/**
 * Class.java
 *
 * 2011.09.12
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
package uk.ac.ebi.benchmark;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;


/**
 *          Class – 2011.09.12 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class Result
  implements Externalizable {

    private static final long serialVersionUID = 1L;
    private String name;
    private ResultAggregator parent;


    public Result() {
    }

    

    public Result(String name,
                  ResultAggregator parent) {
        this.name = name;
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
    }


}

