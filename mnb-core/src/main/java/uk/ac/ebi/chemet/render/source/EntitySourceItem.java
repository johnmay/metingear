
/**
 * EntitySourceItem.java
 *
 * 2011.09.30
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
package uk.ac.ebi.chemet.render.source;

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.mnb.settings.SourceItemDisplayType;


/**
 *          EntitySourceItem – 2011.09.30 <br>
 *          Base class for source item trees
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class EntitySourceItem
  extends SourceListItem {

    private static final Logger LOGGER = Logger.getLogger(EntitySourceItem.class);
    private final AnnotatedEntity entity;
    private static Map<SourceItemDisplayType, Accessor> map = getMap();
    protected  final Object container;


    public EntitySourceItem(final AnnotatedEntity entity, final Object container) {
        super("");
        this.entity = entity;
        this.container = container;
        setText(entity.toString());
    }


    public AnnotatedEntity getEntity() {
        return entity;
    }


    /**
     * Updates the source list item
     */
    public void update(SourceItemDisplayType type) {
        setText(map.get(type).access(entity));
    }


    private static Map<SourceItemDisplayType, Accessor> getMap() {
        Map map = new EnumMap(SourceItemDisplayType.class);
        map.put(SourceItemDisplayType.NAME, new NameAccessor());
        map.put(SourceItemDisplayType.ACCESSION, new AccessionAccessor());
        map.put(SourceItemDisplayType.ABBREVIATION, new AbbreviationAccessor());
        return map;
    }


    public abstract void update();

    /**
     * Removes item from the model
     * @param model
     */
    public abstract void remove(SourceListModel model);


}

