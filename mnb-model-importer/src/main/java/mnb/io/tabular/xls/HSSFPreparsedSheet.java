
/**
 * HSSFPreparsedSheet.java
 *
 * 2011.08.30
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
package mnb.io.tabular.xls;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import mnb.io.tabular.type.EntityColumn;
import mnb.io.tabular.EntityResolver;
import mnb.io.tabular.ExcelModelProperties;
import mnb.io.tabular.preparse.PreparsedMetabolite;
import mnb.io.tabular.type.TableDescription;
import mnb.io.tabular.preparse.PreparsedSheet;
import mnb.io.tabular.preparse.PreparsedEntry;
import mnb.io.tabular.preparse.PreparsedReaction;
import mnb.io.tabular.type.ReactionColumn;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.ss.format.CellFormatType;


/**
 *          HSSFPreparsedSheet – 2011.08.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class HSSFPreparsedSheet extends PreparsedSheet {

    private static final Logger LOGGER = Logger.getLogger( HSSFPreparsedSheet.class );
    private final HSSFSheet worksheet;
    private Class dataType;


    /**
     *
     *
     * @param worksheet
     * @param properties An instance of ExcelModelProperties describing the location of the data in
     *                   the excel workbook
     * @param header     Whether the bounding box includes a single header row. This will
     *                   be used as the column names in the PreparsedReaction. If the header
     *                   is false columns without names will not be loaded into the
     *                   PreparsedEntry
     *
     */
    public HSSFPreparsedSheet( HSSFSheet worksheet,
                               ExcelModelProperties properties,
                               TableDescription bounds ) {
        super( properties, bounds );
        dataType = bounds.getClass();
        this.worksheet = worksheet;
    }


    @Override
    public PreparsedEntry getEntry( int i, List<TableDescription> cols ) {

        PreparsedEntry entry = null;

        // otherwise we can't cast
        if( dataType == ReactionColumn.class ) {
            entry = new PreparsedReaction();
        } else if( dataType == EntityColumn.class ) {
            entry = new PreparsedMetabolite();
        }

        // add others

        HSSFRow row = worksheet.getRow( i );
        ExcelModelProperties properties = super.getProperties();

        // for the described columns add them to the preparsed entry
        for( TableDescription col : cols ) {
            int index = properties.getColumnIndex( col );
            HSSFCell cell = row.getCell( index );
            if( cell != null ) {
                int type = cell.getCellType();
                if( type == HSSFCell.CELL_TYPE_NUMERIC ) {
                    entry.addValue( col, Double.toString( cell.getNumericCellValue() ) );
                } else {
                    entry.addValue( col, cell.getStringCellValue() );
                }
            }
        }

        return entry;
    }


}

