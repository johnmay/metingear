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
package uk.ac.ebi.mnb.interfaces;

import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;

import javax.swing.undo.AbstractUndoableEdit;
import java.util.Collection;

/**
 *          UndoableEntityEdit - 2011.11.22 <br>
 *          Provides access to the entities that an edit affects. Can avoid
 *          redrawing the whole UI.
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class UndoableEntityEdit extends AbstractUndoableEdit {

    /**
     * Access the entities that this edit affects
     * @return
     */
    public abstract Collection<AnnotatedEntity> getEntities();
}
