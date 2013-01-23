/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.io.tabular.preparse;

import java.util.Arrays;
import mnb.io.tabular.type.ReactionColumn;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johnmay
 */
public class PreparsedEntryTest {

    public PreparsedEntryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testAddValue() {
    }

    @Test
    public void testGetValue_Integer() {
    }

    @Test
    public void testGetValue_TableDescription() {
    }

    @Test
    public void testGetColumnSet() {
    }

    @Test
    public void testGetListMatcher() {
    }

    @Test
    public void testGetValues() {

        PreparsedReaction entry = new PreparsedReaction();
      
        entry.addValue(ReactionColumn.CLASSIFICATION, "EC:1.1.1.1;EC:2.2.1.2");
        assertArrayEquals(new String[]{"EC:1.1.1.1", "EC:2.2.1.2"}, entry.getClassifications());

        entry.addValue(ReactionColumn.LOCUS, "LOCI1+LOCI2|LOCI3");
        assertArrayEquals(new String[]{"LOCI1+LOCI2", "LOCI3"}, entry.getLoci());

    }
}
