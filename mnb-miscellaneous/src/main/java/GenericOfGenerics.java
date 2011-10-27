
/**
 * GenericOfGenerics.java
 *
 * 2011.10.20
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

/**
 *          GenericOfGenerics - 2011.10.20 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class GenericOfGenerics {
    public static void main(String[] args) {
        Aggregator agg = new Aggregator<One>(new One<String>("Hello"));
        String type = (String) agg.getObjFromM(String.class);
        System.out.println(type);
    }
}

class Aggregator<E extends One> {

    private E agg;

    public Aggregator(E agg) {
        this.agg = agg;
    }

    public Object getObjFromM() {
        return agg.getObj();
    }

    public <T> T getObjFromM(Class<T> type) {
        return (T) agg.getObj();
    }
}

class One<M> {

    private M obj;

    public One(M obj) {
        this.obj = obj;
    }

    public M getObj() {
        return obj;
    }
}