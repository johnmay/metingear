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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view.entity.components.control;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;


/**
 *
 * @author johnmay
 */
public class AnnotationControlManagerTest {

    @Test
    public void testSomeMethod() {

        AnnotationControlManager controlManager = new AnnotationControlManager(null);

        Assert.assertEquals(ObservationBasedAnnotationControl.class, controlManager.getExplicitContol(EnzymeClassification.class).getClass());
        Assert.assertEquals(ObservationBasedAnnotationControl.class, controlManager.getExplicitContol(CrossReference.class).getClass());
        Assert.assertEquals(ChemicalStructureControl.class, controlManager.getExplicitContol(ChemicalStructure.class).getClass());

    }
}
