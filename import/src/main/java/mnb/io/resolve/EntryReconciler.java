
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
package mnb.io.resolve;

import mnb.io.tabular.preparse.PreparsedEntry;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;


/**
 *          EntryReconciler – 2011.09.23 <br>
 *          Interface to allow reconciliation through different means such as console, swing or
 *          automated
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public interface EntryReconciler {

    public AnnotatedEntity resolve(PreparsedEntry entry);

}

