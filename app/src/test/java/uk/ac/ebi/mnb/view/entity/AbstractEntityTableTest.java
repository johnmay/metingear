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

package uk.ac.ebi.mnb.view.entity;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.mnb.view.entity.AbstractEntityTable.intervals;

/**
 * @author John May
 */
public class AbstractEntityTableTest {

    @Test
    public void testIntervals_Empty() throws Exception {
        assertThat(intervals(new int[0]), is(new int[0][]));
    }

    @Test
    public void testIntervals_One() throws Exception {
        assertThat(intervals(new int[]{5}), is(new int[][]{{5, 5}}));
    }

    @Test
    public void testIntervals_Range() throws Exception {
        assertThat(intervals(new int[]{2, 3, 4}), is(new int[][]{{2, 4}}));
    }

    @Test
    public void testIntervals_MultiRange() throws Exception {
        assertThat(intervals(new int[]{2, 3, 4, 6, 7, 8}), is(new int[][]{{2, 4}, {6, 8}}));
    }

    @Test
    public void testIntervals_Spread() throws Exception {
        assertThat(intervals(new int[]{1, 3, 5, 7}), is(new int[][]{{1, 1}, {3, 3}, {5, 5}, {7, 7}}));
    }
}
