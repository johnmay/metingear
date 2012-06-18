/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.core;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.MetaboliteImpl;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;


/**
 *
 * @author johnmay
 */
public class EntityMapTest {

    public EntityMapTest() {
    }


    @BeforeClass
    public static void setUpClass() throws Exception {
    }


    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    @Test
    public void testMetabolite() {

        EntityCollection collection = new EntityMap(DefaultEntityFactory.getInstance());

        collection.add(DefaultEntityFactory.getInstance().newInstance(Metabolite.class));

        Assert.assertFalse(collection.hasSelection(MetaboliteImpl.class));
        Assert.assertTrue(collection.hasSelection(Metabolite.class));

    }
}
