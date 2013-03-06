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
package uk.ac.ebi.mnb.view.entity.components.control;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Charge;
import uk.ac.ebi.mdk.domain.annotation.MolecularFormula;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.tool.domain.StructuralValidity;

import java.util.Collection;


/**
 *
 *          ChemicalStructureControl 2012.02.14
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *
 *          Class description
 *
 */
public class ChemicalStructureControl implements AnnotationTableControl {

    private static final Logger LOGGER = Logger.getLogger(ChemicalStructureControl.class);

    private Charge defaultCharge = new Charge(0d);


    public Object getController(Annotation annotation, AnnotatedEntity entity) {
        ChemicalStructure cs = (ChemicalStructure) annotation;

        Collection<MolecularFormula> mfs = entity.getAnnotations(MolecularFormula.class);
        Charge charge = entity.hasAnnotation(Charge.class) ? entity.getAnnotations(Charge.class).iterator().next() : defaultCharge;

        return StructuralValidity.getValidity(mfs, cs, charge);

    }
}
