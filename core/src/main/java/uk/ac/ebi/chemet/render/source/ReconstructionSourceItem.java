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

import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListModel;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.mdk.domain.entity.AbstractAnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;


/**
 *          MetaboliteSourceItem – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReconstructionSourceItem
        extends EntitySourceItem {

    private static final Logger LOGGER = Logger.getLogger(ReconstructionSourceItem.class);


    public ReconstructionSourceItem(AnnotatedEntity entity, Object container) {
        super(entity, container);
        setIcon(ResourceUtility.getIcon("/uk/ac/ebi/chemet/render/images/networkbuilder_16x16.png"));
    }


    @Override
    public Reconstruction getEntity() {
        return (Reconstruction) super.getEntity();
    }


    /**
     * @inheritDoc
     */
    @Override
    public void update() {
        ReconstructionManager manger = DefaultReconstructionManager.getInstance();
        Reconstruction active = manger.active();
        setText(getEntity().getAccession() + (getEntity() == active ? " (active)" : ""));
    }


    @Override
    public void remove(SourceListModel model) {
        model.removeItemFromCategory(this, (SourceListCategory) super.container);
    }
}
