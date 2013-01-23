
/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.chemet.render.source;

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.AbstractAnnotatedEntity;


/**
 *          MetaboliteSourceItem – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetaboliteSourceItem
  extends EntitySourceItem {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteSourceItem.class);


    public MetaboliteSourceItem(AbstractAnnotatedEntity entity, Object container) {
        super(entity, container);
    }

    @Override
    public void remove(SourceListModel model) {
        model.removeItemFromItem(this, (SourceListItem) super.container);
    }


}

