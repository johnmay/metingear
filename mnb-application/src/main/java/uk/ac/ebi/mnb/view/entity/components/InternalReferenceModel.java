/**
 * InternalReferenceModel.java
 *
 * 2011.12.14
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
package uk.ac.ebi.mnb.view.entity.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.AnnotatedEntity;

/**
 *          InternalReferenceModel - 2011.12.14 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class InternalReferenceModel
        implements ListModel {

    private static final Logger LOGGER = Logger.getLogger(InternalReferenceModel.class);
    private Set<ListDataListener> listeners = new HashSet<ListDataListener>();
    private List<AnnotatedEntity> entities = new ArrayList<AnnotatedEntity>();

    public void setEntities(Collection<? extends AnnotatedEntity> entities) {

        this.entities.clear();
        this.entities.addAll(entities);

        ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, entities.size());
        for (ListDataListener l : listeners) {
            l.contentsChanged(event);
        }

    }

    public void clear() {
        entities.clear();
    }

    public int getSize() {
        return entities.size();
    }

    public Object getElementAt(int index) {
        return entities.get(index);
    }

    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
}
