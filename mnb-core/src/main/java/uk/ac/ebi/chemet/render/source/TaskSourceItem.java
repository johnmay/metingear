
/**
 * MetaboliteSourceItem.java
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

import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListModel;
import furbelow.SpinningDial;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AbstractAnnotatedEntity;
import uk.ac.ebi.chemet.io.external.RunnableTask;
import uk.ac.ebi.chemet.io.external.TaskStatus;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.settings.SourceItemDisplayType;
import uk.ac.ebi.mnb.view.ViewUtils;


/**
 *          MetaboliteSourceItem – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class TaskSourceItem
  extends EntitySourceItem {

    private static final Logger LOGGER = Logger.getLogger(TaskSourceItem.class);


    public TaskSourceItem(AbstractAnnotatedEntity entity, Object container) {
        super(entity, container);
    }


    @Override
    public RunnableTask getEntity() {
        return (RunnableTask) super.getEntity();
    }


    /**
     * @inheritDoc
     */
    @Override
    public void update() {

        Settings pref = Settings.getInstance();
        SourceItemDisplayType type = (SourceItemDisplayType) pref.get(Settings.VIEW_SOURCE_TASK);

        super.update(type);

        if( getEntity().getStatus() == TaskStatus.RUNNING ) {
            setIcon(new SpinningDial(16, 16, 12));
        } else if( getEntity().getStatus() == TaskStatus.ERROR ) {
            setIcon(ViewUtils.WARNING_ICON_16x16);
        } else {
            setIcon(null);
        }

    }


    @Override
    public void remove(SourceListModel model) {
        model.removeItemFromCategory(this, (SourceListCategory) super.container);
    }


}

