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
package mnb.io.tabular.preparse;

import mnb.io.tabular.type.TableDescription;
import java.awt.Rectangle;
import java.util.List;
import mnb.io.tabular.ExcelModelProperties;
import org.apache.log4j.Logger;

/**
 *          PreparsedSheet – 2011.08.29 <br>
 *          Provides uniform access to the entries in either an XLS or XLSX file (currently only XLS
 *          is implement). The class is abstract allowing subclasses to implement the
 *          {@see getEntry(int,List)} method. The methods {@see hasNext()} and {@see next()} can
 *          then be used to navigate the pre-parsed sheet. Alternatively direct rows can be accessed
 *          with {@see getEntry(int,List)} method
 *
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class PreparsedSheet {

    private static final Logger LOGGER = Logger.getLogger(PreparsedSheet.class);
    private int rowIndex;
    private final int maxIndex;
    private ExcelModelProperties properties;
    private Rectangle bounds;
    private List<TableDescription> columns;

    public PreparsedSheet(ExcelModelProperties properties,
            TableDescription bounds) {

        this.properties = properties;
        this.bounds = properties.getDataBounds(bounds.getKey());
        this.rowIndex = this.bounds.y - 1;
        this.maxIndex = this.bounds.y + this.bounds.height;
        this.columns = this.properties.getDefinedColumns(bounds.getClass());

    }

    /**
     *
     * Accessor to the assign model properties that the PreparsedSheet is using
     *
     * @return
     *
     */
    public ExcelModelProperties getProperties() {
        return properties;
    }

    /**
     * Returns the number of rows
     * @return
     */
    public int getRowCount() {
        return maxIndex - this.bounds.y - 1;
    }

    /**
     *
     * Indicate whether the next method can be called
     *
     * @return
     *
     */
    public Boolean hasNext() {
        return rowIndex < maxIndex;
    }

    /**
     * Resets the index to the the first entry to be parsed
     * (as indicated in the ExcelModelProperties)
     */
    public void reset() {
        rowIndex = this.bounds.y - 1;
    }

    /**
     *
     * Returns the next entry in the pre-parsed sheet
     *
     * @return
     *
     */
    public PreparsedEntry next() {
        return getEntry(++rowIndex, columns);
    }

    /**
     *
     * Implementing methods should return a PreparsedEntry with the provided 'types' loaded
     *
     * @param i
     * @param types
     *
     * @return
     *
     */
    public abstract PreparsedEntry getEntry(int i, List<TableDescription> types);
}
