
/**
 * ExcelUtilities.java
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
package mnb.io.tabular.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.SheetUtil;


/**
 *          ExcelUtilities – 2011.08.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ExcelUtilities {

    private static final Logger LOGGER = Logger.getLogger( ExcelUtilities.class );
    private static final Pattern EXCEL_CELL_DESCRIPTOR = Pattern.compile( "([A-z]+)([0-9]+)" );


    /**
     *
     * Converts an excel character to it's index. For example
     * A=0, Z=25, AA=26 and AB=27
     * @return
     */
    public static Integer stringToIndex( String characters ) {
        Integer index = 0;
        for( int i = 0 ; i < characters.length() - 1 ; i++ ) {
            index += ( 1 + charToIndex( characters.charAt( i ) ) ) * 26;
        }
        index += charToIndex( characters.charAt( characters.length() - 1 ) );
        return index;
    }


    /**
     *
     * Returns the cell as programatic index where A1=0,0 B2=1,1 and C1=2,0
     *
     * @param excelCellLocation
     * @return
     *
     */
    public static Integer[] getIndices( String excelCellLocation ) {

        Matcher cellMatcher = EXCEL_CELL_DESCRIPTOR.matcher( excelCellLocation );

        if( cellMatcher.matches() ) {

            return new Integer[]{
                  stringToIndex( cellMatcher.group( 1 ) ), // x
                  ( Integer.parseInt( cellMatcher.group( 2 ) ) - 1 ) };  // y
        }

        throw new UnsupportedOperationException(
          excelCellLocation +
          " doesn't look like an excel cell location. Expected A1, F5 and AB26 etc." );

    }


    public static String indexToString( Integer index ) {
        throw new UnsupportedOperationException( "Does not work" );
//        StringBuilder representation = new StringBuilder( 2 );
//        while( index >= 25 ) {
//            index -= 25;
//            representation.append( "A" );
//        }
//        representation.append( indexToChar( index ) );
//        return representation.toString();
    }


    /**
     *
     * Returns the value of the Character A=0, Z=25
     *
     * @param c
     * @return
     */
    public static Integer charToIndex( Character c ) {
        return Character.getNumericValue( Character.toUpperCase( c ) ) - 10;
    }


    //XXX sure there's a neat way but counldn't find one atm
    private static Character[] alphabet = new Character[]{
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };


    public static Character indexToChar( Integer index ) {
        return alphabet[index];
    }


    public static void main( String[] args ) {
        System.out.println( Arrays.asList( getIndices( "A1" ) ) );
        System.out.println( Arrays.asList( getIndices( "B1" ) ) );
        System.out.println( Arrays.asList( getIndices( "C5" ) ) );
        System.out.println( Arrays.asList( getIndices( "AZ40" ) ) );
    }


}

