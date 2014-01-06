/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
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

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Defines a structure editor for CDK AtomContainer instances.
 *
 * @author John May
 */
public interface StructureEditor {

    /**
     * Open a structure editor for the specified CDK AtomContainer. If the
     * editor was not confirmed (i.e. a used canceled) then a null
     * structure it returned.
     *
     * @param input input structure
     * @return the structure draw by the input
     */
    IAtomContainer edit(IAtomContainer input);

}
