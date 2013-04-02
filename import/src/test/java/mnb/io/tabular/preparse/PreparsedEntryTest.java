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
