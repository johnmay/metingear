/**
 * FilterUniqueIsomers.java
 *
 * Version $Revision$
 *
 * 2011.07.26
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
package uk.ac.ebi.execs;

/**
 * @name   FilterUniqueIsomers
 * @date    2011.07.26
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *          Converts a CSV with InChI, InChIKey and AuxInfo columns and write molecules to
 *          'cdk-generated.mol' file. Note each value should be prefixed with InChI=, InChIKey= and AuxInfo=
 *
 */
public class InChICSV2MDL {
//        extends CommandLineMain {
//
//    private static final Logger LOGGER = Logger.getLogger( InChICSV2MDL.class );
//
//    public static void main( String[] args ) {
//        new InChICSV2MDL( args ).process();
//    }
//
//    private InChICSV2MDL( String[] args ) {
//        super( args );
//    }
//
//    @Override
//    public void setupOptions() {
//        // add options here
//        add( new Option( "i" , "input" , true , "an input file with inchis included" ) );
//    }
//
//    @Override
//    public void process() {
//        // add processing here
//        File input = super.getCmd().hasOption( "i" ) ? new File( getCmd().getOptionValue( "i" ) ) : null;
//        if ( input == null ) {
//            printHelp();
//            System.exit( 1 );
//        }
//
//        CSVReader reader = null;
//        try {
//            reader = new CSVReader( new BufferedReader( new FileReader( input ) ) , '=' , '\0' );
//            String[] row = null;
//
//            // we store each InChI, InChI-Key and AuxInfo in a HashMap which inturn is in
//            // an array list. A new list entry is added on empty lines
//            List<InChI> inchis = new ArrayList<InChI>();
//            inchis.add( new InChI( "" ) );
//
//            while ( ( row = reader.readNext() ) != null ) {
//                if ( row.length > 1 ) {
//                    // XXX Quick..
//                    if ( row[0].equals( "InChI" ) ) {
//                        inchis.get( inchis.size() - 1 ).setInchi( "InChI=" + row[1] );
//                    } else if ( row[0].equals( "InChI-Key" ) ) {
//                        inchis.get( inchis.size() - 1 ).setInchiKey( row[1] );
//                    } else if ( row[0].equals( "AuxInfo" ) ) {
//                        inchis.get( inchis.size() - 1 ).setAuxInfo( row[1] );
//                    }
//                } else if ( row[0].isEmpty() ) {
//                    inchis.add( new InChI( "" ) );
//                } else {
//                    LOGGER.error( "There was a problem when reading line: " + row +
//                                  ". please ensure each section starts with InChI=, InChI-Key= and AuxInfo=" );
//                }
//            }
//            reader.close();
//
//            InChIGeneratorFactory INCHI_FACTORY = InChIGeneratorFactory.getInstance();
//            MDLV2000Writer writer = new MDLV2000Writer( new FileOutputStream( "cdk-generated.mol" ) );
//
//
//            for ( InChI inchi : inchis ) {
//                InChIToStructure i2s = INCHI_FACTORY.getInChIToStructure( inchi.toString() + "\nAuxInfo=" + inchi.getAuxInfo()
//                        , DefaultChemObjectBuilder.
//                        getInstance() );
//                if ( i2s.getReturnStatus() == INCHI_RET.OKAY ) {
//                    IAtomContainer atomContainer = i2s.getAtomContainer();
//                    writer.write( atomContainer );
//                } else {
//                    LOGGER.error("There was a problem generating structure for InChI-Key: " + inchi.getInchiKey());
//                }
//            }
//
//        } catch ( CDKException ex ) {
//            ex.printStackTrace();
//        } catch ( IOException ex ) {
//        } finally {
//            try {
//                reader.close();
//            } catch ( IOException ex ) {
//            }
//        }
//
//    }
}
