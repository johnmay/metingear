/**
 * SelectionMap.java
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
package uk.ac.ebi.mnb.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import uk.ac.ebi.core.ProteinProduct;
import uk.ac.ebi.core.RNAProduct;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.GeneProduct;
import uk.ac.ebi.mnb.interfaces.SelectionManager;

/**
 * @name    SelectionMap - 2011.10.14 <br>
 *          Class effectively wraps a guava multimap to provide some convenience
 *          methods for handling exceptions
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class SelectionMap implements SelectionManager {

    private Multimap<Class, AnnotatedEntity> map = ArrayListMultimap.create();

    /**
     * @inheritDoc
     */
    public Collection<AnnotatedEntity> getSelection() {
        return map.values();
    }

    /**
     * @inheritDoc
     */
    public boolean add(AnnotatedEntity entity) {
        return map.put(entity.getClass(), entity);
    }

    /**
     * @inheritDoc
     */
    public boolean addAll(Collection<AnnotatedEntity> entities) {
        return map.putAll(entities.getClass(), entities);
    }

    /**
     * @inheritDoc
     */
    public void clear() {
        map.clear();
    }

    /**
     * @inheritDoc
     */
    public <T> Collection<T> get(Class<T> type) {
        return (Collection<T>) map.get(type);
    }

    /**
     * @inheritDoc
     */
    public Collection<GeneProduct> getGeneProducts() {
        
        Collection<ProteinProduct> proteins = get(ProteinProduct.class);
        Collection<RNAProduct> rnas = get(RNAProduct.class);

        Collection<GeneProduct> products = new ArrayList();
        products.addAll(proteins);
        products.addAll(rnas);

        return products;

    }
}
