/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view.entity.components.control;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
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
