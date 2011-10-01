/**
 * ExcelXLSImporter.java
 *
 * 2011.07.31
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
package uk.ac.ebi.mnb.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @name    ExcelXLSImporter
 * @date    2011.07.31
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class ExcelXLSImporter extends ExcelImporter {

    private static final Logger LOGGER = Logger.getLogger( ExcelXLSImporter.class );
    private HSSFWorkbook workbook;

    /**
     * Constructor the importer from an input stream
     * @param stream
     * @throws IOException
     */
    public ExcelXLSImporter( InputStream stream ) throws IOException {
        this.workbook = new HSSFWorkbook( stream );
    }

    public List<String> getSheetNames() {
        List<String> sheetNames = new ArrayList<String>();
        for ( int i = 0; i < workbook.getNumberOfSheets(); i++ ) {
            sheetNames.add( workbook.getSheetName( i ) );
        }
        return sheetNames;
    }

    /**
     * Checks sheet names are returns indices that may be reaction sheets
     * @return
     */
    public List<Integer> getReactionSheetIndices() {

        List<Integer> indices = new ArrayList<Integer>();

        for ( int i = 0; i < workbook.getNumberOfSheets(); i++ ) {
            if ( super.nameMatchesReactionSheet( workbook.getSheetName( i ) ) ) {
                indices.add( i );
            }
        }

        return indices;
    }

    public List<Integer> getMetaboliteSheetIndices() {

        List<Integer> indices = new ArrayList<Integer>();

        for ( int i = 0; i < workbook.getNumberOfSheets(); i++ ) {
            if ( super.nameMatchesMetaboliteSheet( workbook.getSheetName( i ) ) ) {
                indices.add( i );
            }
        }

        return indices;
    }

    public String[][] loadReactionSheet( HSSFSheet sheet ) {


        int maxRow = sheet.getLastRowNum();
        int colNumber = INTIAL_COLUMN_NUMBER;
        String[][] data = new String[ maxRow ][ colNumber ];

        List<String> block_xy1 = new ArrayList<String>();
        List<String> block_xy2 = new ArrayList<String>();

        int prevLastFilledColumn = -1;


        for ( int i = 0; i < maxRow; i++ ) {

            HSSFRow row = sheet.getRow( i );

            // resize if needed
            if ( row.getLastCellNum() >= colNumber ) {
                LOGGER.info( "resizing.." );
                int new_size = row.getLastCellNum() * 2;
                for ( int row_i = 0; row_i < maxRow; row_i++ ) {
                    data[row_i] = Arrays.copyOf( data[row_i] , new_size );
                    for ( int row_j = 0; row_j < data[row_i].length; row_j++ ) {
                        data[row_i][row_j] = "";
                    }
                }
                colNumber = new_size;
            }

            int lastFilledColumn = -1;


            for ( int j = 0; j < row.getLastCellNum(); j++ ) {

                // check for empty rows
                data[i][j] = row.getCell( j ) == null ? "" : row.getCell( j ).getStringCellValue().trim();

                if ( data[i][j].isEmpty() == Boolean.FALSE ) {
                    lastFilledColumn = j;
                    if ( super.hasReactionOperator( data[i][j] ) ) {
                        //  System.out.println( j );
                    }
                }

            }
            for ( int j = row.getLastCellNum(); j < colNumber; j++ ) {
                if ( j != -1 ) {
                    data[i][j] = "";
                }
            }

            if ( lastFilledColumn == -1 ) {
                if ( prevLastFilledColumn != -1 ) {
                    // empty row
                    block_xy2.add( ( i - 1 ) + "," + prevLastFilledColumn );
                }
            } else {
                if ( block_xy2.size() == block_xy1.size() ) {
                    block_xy1.add( i + "," + 0 );
                }
            }

            prevLastFilledColumn = lastFilledColumn;


        }

        for ( int i = 0; i < block_xy2.size(); i++ ) {
            System.out.println( block_xy1.get( i ) + " to " + block_xy2.get( i ) );

        }

        return data;

    }

    public HSSFWorkbook getWorkbook() {
        return workbook;
    }

    public static void main( String[] args ) {
        File bacterialModels = new File( "/Users/johnmay/Desktop/organisms/bacteria" );
        File[] xlsFiles = bacterialModels.listFiles( new FilenameFilter() {

            public boolean accept( File dir , String name ) {
                return name.endsWith( ".xls" );
            }
        } );
        for ( File file : new File[]{ xlsFiles[0] } ) {
            ExcelXLSImporter modelImporter;
            try {
                modelImporter = new ExcelXLSImporter( new FileInputStream( file ) );

                List<Integer> indices = modelImporter.getReactionSheetIndices();

                if ( indices.size() == 1 ) {

                    String[][] data = modelImporter.loadReactionSheet(
                            modelImporter.getWorkbook().getSheetAt( indices.get( 0 ) ) );


                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                    frame.setSize( 800 , 600 );
                    System.out.println( Arrays.asList( data[0] ) );
                    frame.add( new JScrollPane( new JTable( data , data[0] ) ) );
                    frame.setVisible( true );

                } else {
                    for ( Integer i : indices ) {
                        System.out.println( modelImporter.getWorkbook().getSheetName( i ) );
                    }
                    for ( Integer i : new Integer[]{ 0 , 1 , 2 } ) {
                        System.out.println( modelImporter.getWorkbook().getSheetName( i ) );
                    }

                }

            } catch ( IOException ex ) {
                System.out.println( "Could not read " + file + " : " + ex.getMessage() );
            }


        }
    }

    @Override
    public String[][] getTableHead( Integer sheetIndex , Integer rowCount ) {

        HSSFSheet sheet = workbook.getSheetAt( sheetIndex );
        Integer colCount = 0;


        List[] data = new List[ rowCount ];

        for ( Integer i = 0; i < rowCount; i++ ) {
            HSSFRow row = sheet.getRow( i );
            data[i] = new ArrayList();
            for ( Integer j = 0; j < row.getLastCellNum(); j++ ) {
                HSSFCell cell = row.getCell( j );
                if ( cell != null && ! cell.toString().trim().isEmpty() ) {
                    data[i].add( cell.toString() );
                    colCount = j > colCount ? j : colCount;
                } else {
                    data[i].add( "" );
                }
            }
        }

        String[][] fixedData = new String[ rowCount ][ colCount ];
        for ( int i = 0; i < rowCount; i++ ) {
            // make sure we have the correct length
            while ( data[i].size() < colCount ) {
                data[i].add( "" );
            }
            fixedData[i] = ( String[] ) data[i].subList( 0 , colCount ).toArray( new String[ 0 ] );
        }

        return fixedData;
    }
}
