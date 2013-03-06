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
package uk.ac.ebi.mnb.xls.options;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @name    ImporterOptions
 * @date    2011.08.04
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   Class to store the options selected in the wizard
 *
 */
public class ImporterOptions {

    private static final Logger LOGGER = Logger.getLogger( ImporterOptions.class );
    // stores the locations of the sheets
    private Map<SheetType , Integer> sheetMap;
    // stores the locations of the columns for each sheet
    private Map<SheetType , Map<ExcelColumnType , List<Integer>>> colMaps;

    public ImporterOptions() {
        sheetMap = new EnumMap<SheetType , Integer>( SheetType.class );
        colMaps = new EnumMap<SheetType , Map<ExcelColumnType , List<Integer>>>( SheetType.class );
    }

    public Map<ExcelColumnType , List<Integer>> getColumnKey( SheetType sheet ) {
        return colMaps.containsKey( sheet ) ? colMaps.get( sheet ) : null;
    }

    public Boolean hasColumn( SheetType sheet , ExcelColumnType column ) {
        return colMaps.containsKey( sheet ) && colMaps.get( sheet ).containsKey( column );
    }

    public Integer getSheetIndex( SheetType sheet ) {
        return sheetMap.containsKey( sheet ) ? sheetMap.get( sheet ) : null;
    }

    // adder methods
    public void addSheet( SheetType sheet , Integer index ) {
        sheetMap.put( sheet , index );
    }

    public void addColumn( SheetType sheet , ExcelColumnType column , Integer index ) {

        // the map for this sheet does not exist add the sheet and column map
        if ( colMaps.containsKey( sheet ) == Boolean.FALSE ) {

            colMaps.put( sheet , new EnumMap<ExcelColumnType , List<Integer>>( ExcelColumnType.class ) );
            colMaps.get( sheet ).put( column , new ArrayList<Integer>() );
        } else if ( colMaps.get( sheet ).containsKey( column ) ) {

            // the sheet map exists but not for this column... add a new list
            colMaps.get( sheet ).put( column , new ArrayList<Integer>() );
        }

        // finally we add the index in the correct place
        colMaps.get( sheet ).get( column ).add( index );
    }
}
