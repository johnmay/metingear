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

import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mnb.interfaces.Updatable;

import java.util.Collection;

/**
 * Interface to allow undo/edit.
 *
 * @author John May
 */
public interface EntityTransfer {

    /**
     * Move the entity from the source reconstruction to the destination.
     *
     * @param src    source
     * @param dest   destination
     * @param entity the entity to move
     */
    void moveTo(Reconstruction src, Reconstruction dest, Collection<AnnotatedEntity> entity);

    /**
     * Copy the entity from the source reconstruction to the destination.
     *
     * @param src    source
     * @param dest   destination
     * @param entity the entity to copy
     */
    void copyTo(Reconstruction src, Reconstruction dest, Collection<AnnotatedEntity> entity);

}
