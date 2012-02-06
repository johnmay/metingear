/**
 * NameMatcherMain.java
 *
 * Version $Revision$
 *
 * 2011.07.28
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
package uk.ac.ebi.chemet.execs;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import uk.ac.ebi.chebi.webapps.chebiWS.model.Entity;
import org.apache.commons.cli.Option;
import org.apache.log4j.Logger;
import uk.ac.ebi.chebi.webapps.chebiWS.model.DataItem;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StarsCategory;
import uk.ac.ebi.metabolomes.execs.CommandLineMain;
import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
import uk.ac.ebi.metabolomes.webservices.util.CandidateEntry;
import uk.ac.ebi.metabolomes.webservices.util.ChemicalNameEntryDecider;

/**
 * @name    NameMatcherMain
 * @date    2011.07.28
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class NameMatcherMain
        extends CommandLineMain {

    private static final Logger LOGGER = Logger.getLogger( NameMatcherMain.class );

    public static void main( String[] args ) {
//        AminoAcidCompoundSet aminoacids = AminoAcidCompoundSet.getAminoAcidCompoundSet();
//        for ( AminoAcidCompound aa : aminoacids.getAllCompounds() ) {
//            System.out.println( aa.getLongName() + "\t" + aa.getDescription() );
//        }
//
//        //  CompoundSet new AminoAcidCompound().getCompoundSet();
        new NameMatcherMain( args ).process();
    }

    private NameMatcherMain( String[] args ) {
        super( args );
    }

    @Override
    public void setupOptions() {

        // add options here
        add( new Option( "i" , "input" , true , "input file (tsv)" ) );
        add( new Option( "c" , "compound-index" , true , "index of compound name" ) );
        add( new Option( "f" , "formula-index" , true , "index of formula" ) );


    }

    @Override
    public void process() {
        // add processing here
        File input = getCmd().hasOption( "i" ) ? new File( getCmd().getOptionValue( "i" ) ) : null;

        ChEBIWebServiceConnection chebi = new ChEBIWebServiceConnection( StarsCategory.THREE_ONLY , 20 );

        Pattern genericHydrogens = Pattern.compile( "H(\\d+)" );

        if ( input == null ) {
            printHelp();
            System.exit( 1 );
        }

        try {
            CSVReader reader = new CSVReader( new FileReader( input ) , '\t' , '\0' );
            CSVWriter writer = new CSVWriter( new FileWriter( new File( "compound-search-fast.tsv" ) ) , '\t' , '\0' );
            String[] row;
            ChemicalNameEntryDecider decider = new ChemicalNameEntryDecider();


            Integer directMatches = 0;
            Integer nondirectMatches = 0;
            int completed_count = 0;
            while ( ( row = reader.readNext() ) != null ) {
                try {
                    ArrayList<String> mutableRow = new ArrayList<String>( Arrays.asList( row ) );
                    Set<String> chebiIdentifers = new HashSet<String>();
                    String compoundName = row[1];
                    String keggId = row[2];
                    String formula = row[4];

                    if (keggId.isEmpty() == Boolean.FALSE){
                      //  chebi
                    }


                    //    if ( compoundName.contains( "L-Lysine" ) ) {

                    Map<String , String> candidates = new HashMap<String , String>();
                    Set<String> candidateSet = new HashSet<String>();

                    Map<String , String> chebiResults = chebi.search( compoundName );

                    List<String> chebiEntriesIds = new ArrayList<String>( chebiResults.keySet() );

                    // add the formula search results
                    if ( formula.isEmpty() == Boolean.FALSE ) {
// todo search different protonation states
//                            Matcher formulaMathcer = genericHydrogens.matcher( formula );
//                            formulaMathcer.find();
//                            Integer h_count = Integer.parseInt( formulaMathcer.group( 1 ) );
                        Set<String> formualSearchIds = chebi.searchByFormula( formula ).keySet();
                        //   System.out.println( formualSearchIds );
                        chebiEntriesIds.addAll( formualSearchIds );
                    }
                    ArrayList<Entity> entries = chebi.getCompleteEntities( chebiEntriesIds );

                    // build a list of possible names
                    for ( Entity entry : entries ) {

                        if ( entry != null ) {

                            candidates.put( entry.getChebiAsciiName() , entry.getChebiId() );
                            candidateSet.add( entry.getChebiAsciiName() );


                            for ( DataItem synonym : entry.getSynonyms() ) {
                                if ( candidateSet.contains( synonym.getData() ) == false ) {
                                    candidates.put( synonym.getData() , entry.getChebiId() );
                                    candidateSet.add( synonym.getData() );
                                }
                            }
                        }

                    }

                    List<CandidateEntry> sortedCandidates =
                                         new ArrayList<CandidateEntry>( decider.getOrderedCandidates( compoundName ,
                                                                                                      candidateSet ) );


                    Console c = System.console();


                    //  String chebiIdentifiers = Util.join( new ArrayList( chebiIdentifers ) );
                    //  String cactusNames = Util.join( CactusChemical.getInstance().getNames( compoundName ) );
                    if ( sortedCandidates.isEmpty() ) {
                        mutableRow.add( "" );
                        mutableRow.add( "" );
                        mutableRow.add( "-1" );
                    } else {
                        CandidateEntry bestEntry = sortedCandidates.get( 0 );


                        if ( bestEntry.getDistance() > 0 ) {

                            int i = 0;
                            if ( sortedCandidates.get( i ).getDistance() < 10 ) {
                                System.out.print( compoundName + ": " );

                                while ( bestEntry.getDistance() < 10 ) {
                                    System.out.printf( "\n\t[%2s] %-12s %s" , i + 1 , candidates.get(
                                            bestEntry.getDesc() ) ,
                                                       bestEntry.getDesc() );

                                    if ( ++i < sortedCandidates.size() ) {
                                        bestEntry = sortedCandidates.get( i );
                                    }

                                }
                                if ( i != 0 && c != null ) {
                                    System.out.print( "\nPlease select (None = 0, default): " );
                                    Integer choice = 0;
                                    try {
                                        choice = Integer.parseInt( c.readLine() );
                                    } catch ( NumberFormatException ex ) {
                                    }
                                    if ( choice != 0 ) {
                                        bestEntry = sortedCandidates.get( choice - 1 );
                                        mutableRow.add( candidates.get( bestEntry.getDesc() ) );
                                        mutableRow.add( bestEntry.getDesc() );
                                        mutableRow.add( bestEntry.getDistance().toString() );
                                    } else {
                                        mutableRow.add( "" );
                                        mutableRow.add( "" );
                                        mutableRow.add( "-1" );
                                    }
                                } else {
                                    System.out.print( " Console missing or no viable choice\n" );
                                }
                            }
                        } else {
                            mutableRow.add( candidates.get( bestEntry.getDesc() ) );
                            mutableRow.add( bestEntry.getDesc() );
                            mutableRow.add( bestEntry.getDistance().toString() );
                        }
                    }
                    writer.writeNext( mutableRow.toArray( new String[ 0 ] ) );

                    //  }

                } catch ( Exception e ) {
                    e.printStackTrace();
                } catch ( java.lang.ExceptionInInitializerError e ) {
                    System.err.println( "ExceptionInInitializerError: " + e );
                } catch ( NoClassDefFoundError e ) {
                    System.err.println( "NoClassDefFoundError: " + e );
                }
            }

            reader.close();

            writer.close();
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }



    }
}
