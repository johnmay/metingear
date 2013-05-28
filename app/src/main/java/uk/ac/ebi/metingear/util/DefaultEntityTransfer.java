/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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

package uk.ac.ebi.metingear.util;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.metingear.edit.entity.AddEntitiesEdit;
import uk.ac.ebi.mnb.main.MainView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles entity transfer between reconstructions.
 *
 * @author John May
 */
public enum DefaultEntityTransfer implements EntityTransfer {

    INSTANCE;

    private final Logger logger = Logger.getLogger(getClass());


    @Override
    public void moveTo(Reconstruction src, Reconstruction dest, Collection<AnnotatedEntity> entities) {
        AddEntitiesEdit edit = new AddEntitiesEdit(dest, entities);
        MainView.getInstance().getUndoManager().addEdit(edit);
        edit.apply();
        MainView.getInstance().update();
    }

    @Override
    public void copyTo(Reconstruction src, Reconstruction dest, Collection<AnnotatedEntity> entities) {
        List<AnnotatedEntity> copies = new ArrayList<AnnotatedEntity>(
            entities.size());
        for (AnnotatedEntity e : entities) {
            copies.add(Duplicator.shallow(e));
        }
        moveTo(src, dest, copies);
    }
}
