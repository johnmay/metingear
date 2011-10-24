/**
 * PreparsedEntry.java
 *
 * 2011.08.29
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package mnb.io.tabular.preparse;

import mnb.io.tabular.type.TableDescription;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *          PreparsedEntry – 2011.08.29 <br>
 *          Base class for a preparsed entry, the entry defines a uniform way to access the data
 *          but does not change the data
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class PreparsedEntry {

    private static final Logger LOGGER = Logger.getLogger(PreparsedEntry.class);
    private Map<TableDescription, Integer> columnTypeMap;
    private List<String> coloumnValues = new ArrayList<String>();
    private Pattern listPattern = Pattern.compile("\\s*(?:[|,;]|and|or|&)\\s*");

    public PreparsedEntry(Class<? extends TableDescription> clazz) {
        columnTypeMap = new EnumMap(clazz);
    }

    public void addValue(TableDescription column, String value) {


        // warn about clashing type
        if (columnTypeMap.containsKey(column)) {
            LOGGER.warn(String.format("Clashing column types %s. values: %s and %s",
                                      column,
                                      coloumnValues.get(columnTypeMap.get(column)),
                                      value));
        }
        columnTypeMap.put(column, coloumnValues.size());
        coloumnValues.add(value);

    }

    /**
     *
     * Direct access to the underlying column (integer 0..n)
     *
     * @param index
     * @return
     *
     */
    public String getValue(Integer index) {
        if (index == null) {
            return null;
        }
        return coloumnValues.get(index);
    }

    public String getValue(TableDescription column) {
        return getValue(columnTypeMap.get(column));
    }

    public Set<Entry<TableDescription, Integer>> getColumnSet() {
        return columnTypeMap.entrySet();
    }

    /**
     * Returns the compiled list pattern matcher for the given column.
     * @param column
     * @return 
     */
    public Matcher getListMatcher(TableDescription column) {
        String str = getValue(column);
        return listPattern.matcher(str);
    }

    /**
     * Returns multiple values split on list chars '|', ',' ';'
     * @param column
     * @return
     */
    public String[] getValues(TableDescription column){
        Matcher matcher = getListMatcher(column);
        if( matcher.find() ){
            return listPattern.split(getValue(column));
        }
        return new String[0];
    }
}
