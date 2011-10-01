
/**
 * SearchTableModel.java
 *
 * 2011.09.29
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
package uk.ac.ebi.mnb.view.entity.search;

import java.util.ArrayList;
import java.util.List;
import uk.ac.ebi.mnb.view.entity.ColumnAccessType;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.EntityTableModel;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.search.SearchManager;


/**
 *          SearchTableModel – 2011.09.29 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class SearchTableModel extends EntityTableModel {

    private static final Logger LOGGER = Logger.getLogger(SearchTableModel.class);


    public SearchTableModel() {
        super(new ArrayList<ColumnDescriptor>());
        addColumn(new ColumnDescriptor("Rank", Integer.class, ColumnAccessType.FIXED, Integer.class));
        addColumns(getDefaultColumns());
        addColumn(new ColumnDescriptor("Type", Integer.class, ColumnAccessType.FIXED, Integer.class));
    }


    @Override
    public void loadComponents() {
        List<AnnotatedEntity> entities = SearchManager.getInstance().getPreviousEntries();
        super.setEntities(entities);
    }


    @Override
    public Object getFixedType(AnnotatedEntity component, String name) {
        if( name.equals("Rank") ) {
            return indexOf(component) + 1;
        } else if( name.equals("Type") ) {
            return component.getClass().getSimpleName();
        }
        return "NA";
    }


}

