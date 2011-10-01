//
///**
// * LoadTransportReactions.java
// *
// * Version $Revision$
// *
// * 2011.08.09
// *
// * This file is part of the CheMet library
// *
// * The CheMet library is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * CheMet is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
// */
//package uk.ac.ebi.execs;
//
//import au.com.bytecode.opencsv.CSVReader;
//import au.com.bytecode.opencsv.CSVWriter;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.Stack;
//import java.util.TreeMap;
//import org.apache.commons.cli.Option;
//import org.apache.log4j.Logger;
//import uk.ac.ebi.chebi.webapps.chebiWS.model.Entity;
//import uk.ac.ebi.chebi.webapps.chebiWS.model.StarsCategory;
//import uk.ac.ebi.chemet.entities.Compartment;
//import uk.ac.ebi.chemet.entities.reaction.InChIReaction;
//import uk.ac.ebi.chemet.entities.reaction.filter.InChIFilter;
//import uk.ac.ebi.metabolomes.execs.CommandLineMain;
//import uk.ac.ebi.metabolomes.identifier.GenericIdentifier;
//import uk.ac.ebi.metabolomes.identifier.InChI;
//import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
//import uk.ac.ebi.metabolomes.webservices.KeggCompoundWebServiceConnection;
//
//
///**
// * @name    LoadTransportReactions
// * @date    2011.08.09
// * @version $Rev$ : Last Changed $Date$
// * @author  johnmay
// * @author  $Author$ (this version)
// * @brief   ...class description...
// *
// */
//public class TransportReactionsLoader extends CommandLineMain {
//
//    private static final Logger LOGGER = Logger.getLogger( LoadTransportReactions.class );
//
//
//    public static void main( String[] args ) {
//        args = new String[]{ "-i" ,
//                             "/Users/johnmay/Desktop/transport-reactions/intial/tr-tagged.tsv" };
//        new TransportReactionsLoader( args ).process();
//    }
//
//
//    private TransportReactionsLoader( String[] args ) {
//        super( args );
//    }
//
//
//    @Override
//    public void setupOptions() {
//        // add options here
//        add( new Option( "i" , "input" , true , "an input file" ) );
//    }
//
//
//    @Override
//    public void process() {
//
//        File input = getFileOption("i");
//        ChEBIWebServiceConnection chebi = new ChEBIWebServiceConnection( StarsCategory.THREE_ONLY ,
//                                                                         20 );
//        KeggCompoundWebServiceConnection kegg = new KeggCompoundWebServiceConnection();
//
//
//        Map<String , Integer> columns = new HashMap<String , Integer>() {
//
//            {
//                put( "organism.name" , 0 );
//                put( "model.year" , 3 );
//
//                put( "reaction.id" , 4 );
//                put( "reaction.equation" , 8 );
//                put( "direction" , 13 );
//                put( "side" , 14 );
//                put( "coef" , 15 );
//                put( "db.id" , 16 );
//                put( "name" , 16 );
//                put( "compartment" , 18 );
//
//            }
//
//
//        };
//
//
//        Stack<InChIReaction> reactions = new Stack<InChIReaction>();
//
//        try {
//            CSVReader r = new CSVReader( new FileReader( input ) , '\t' , '\0' );
//            String[] row;
//            String prevEquation = "";
//            boolean missingInfo = false;
//            Integer chebiIdCount = 0, keggIdCount = 0;
//            Integer inchiCount = 0;
//
//            HashMap<String , Set<InChI>> modelCompoundMap = new HashMap<String , Set<InChI>>();
//            HashMap<String , Map<InChI , Set<InChIReaction>>> modelCompoundReactionMap =
//                                                              new HashMap<String , Map<InChI , Set<InChIReaction>>>();
//
//            HashMap<String , InChI> inchiCache = new HashMap<String , InChI>();
//            inchiCache.put( "-" , null );
//            InChIFilter filter = new InChIFilter();
//            filter.addRejection(
//              new InChI(
//              "InChI=1S/C10H16N5O13P3/c11-8-5-9(13-2-12-8)15(3-14-5)10-7(17)6(16)4(26-10)1-25-30(21,22)28-31(23,24)27-29(18,19)20/h2-4,6-7,10,16-17H,1H2,(H,21,22)(H,23,24)(H2,11,12,13)(H2,18,19,20)/t4-,6-,7-,10-/m1/s1" ) ); // ATP
//            filter.addRejection(
//              new InChI(
//              "InChI=1S/C10H15N5O10P2/c11-8-5-9(13-2-12-8)15(3-14-5)10-7(17)6(16)4(24-10)1-23-27(21,22)25-26(18,19)20/h2-4,6-7,10,16-17H,1H2,(H,21,22)(H2,11,12,13)(H2,18,19,20)/t4-,6-,7-,10-/m1/s1" ) ); // ADP
//            while ( ( row = r.readNext() ) != null ) {
//
//                String modelId = ( row[columns.get( "organism.name" )] + "-" + row[columns.get(
//                                  "model.year" )] ).toLowerCase().replace( " " , "-" );
//
//                String rid = row[columns.get( "reaction.id" )];
//                String equation = row[columns.get( "reaction.equation" )];
//
//
//                if ( !prevEquation.equals( equation ) ) {
//
////                    System.out.println( StringUtils.join( new String[]{ chebiIdCount.toString() , keggIdCount.toString() ,
////                                                                        inchiCount.toString() } , '|' ) );
//                    chebiIdCount = keggIdCount = inchiCount = 0;
//
//                    // pop if needed
//                    if ( missingInfo ) {
//                        reactions.pop();
//                        missingInfo = false;
//                    }
//
//                    // add the reactions to the model->compound->reaction map
//                    if ( reactions.isEmpty() == false ) {
//                        for ( InChI inchi : new HashSet<InChI>( reactions.peek().
//                          getAllReactionMolecules() ) ) {
//                            if ( modelCompoundMap.containsKey( modelId ) == false ) {
//                                modelCompoundReactionMap.put( modelId ,
//                                                              new HashMap<InChI , Set<InChIReaction>>() );
//                            }
//                            if ( modelCompoundReactionMap.get( modelId ).containsKey( inchi ) ==
//                                 false ) {
//                                modelCompoundReactionMap.get( modelId ).put( inchi ,
//                                                                             new HashSet<InChIReaction>() );
//                            }
//                            modelCompoundReactionMap.get( modelId ).get( inchi ).add(
//                              reactions.peek() );
//                        }
//                    }
//
//                    reactions.push( new InChIReaction( filter ) );
//                    reactions.peek().setIdentifier( new GenericIdentifier( rid ) );
////                    reactions.peek().addAnnotation(new UserAnnotation()); // add equation annotation
//                    prevEquation = equation;
//                }
//                if ( missingInfo == false ) {
//
//                    String dbid = row[columns.get( "db.id" )];
//                    InChI inchi = null;
//                    if ( inchiCache.containsKey( dbid ) ) {
//                        inchi = inchiCache.get( dbid );
//                    } else if ( dbid.contains( "CHEBI" ) ) {
//                        Entity chebiEntity = chebi.getCompleteEntities( Arrays.asList( dbid ) ).get(
//                          0 );
//                        inchi = new InChI( chebiEntity.getChebiAsciiName() , chebiEntity.getInchi() ,
//                                           "" , "" );
//                        chebiIdCount++;
//
//                    } else {
//
//                        if ( dbid.equals( "-" ) == false ) {
//                            keggIdCount++;
//                            inchi = new InChI( dbid , kegg.getInChIs( new String[]{ dbid } ).get(
//                              dbid ) , "" , "" );
//                        }
//                    }
//
//                    if ( inchi != null && inchi.getInchi().isEmpty() == false ) {
//
//                        inchiCount++;
//                        Double coef = Double.parseDouble( row[columns.get( "coef" )] );
//                        Compartment c = Compartment.getCompartment(
//                          row[columns.get( "compartment" )] );
//                        inchiCache.put( dbid , inchi );
//
//                        if ( modelCompoundMap.containsKey( modelId ) == false ) {
//                            modelCompoundMap.put( modelId , new HashSet<InChI>() );
//                            modelCompoundReactionMap.put( modelId ,
//                                                          new HashMap<InChI , Set<InChIReaction>>() );
//                        }
//
//                        modelCompoundMap.get( modelId ).add( inchi );
//
//
//                        if ( row[columns.get( "side" )].equals( "left" ) ) {
//                            reactions.peek().addReactant( inchi ,
//                                                          coef , c );
//                        } else {
//                            reactions.peek().addProduct( inchi ,
//                                                         coef , c );
//                        }
//                    } else {
//                        // skip to next
//                        missingInfo = true;
//                    }
//                }
//            }
//
//            // filter..
//            Set<InChIReaction> reactionSet = new HashSet<InChIReaction>( reactions );
//
//            System.out.println( reactions.size() + " -> " + reactionSet.size() );
//
//            Map<InChI , Integer> compoundMap = new HashMap<InChI , Integer>();
//            // which compounds cross the membrane
//            for ( InChIReaction reaction : reactionSet ) {
//                for ( InChI reactant : reaction.getReactantMolecules() ) {
//
//                    List<InChI> reactants = reaction.getReactantMolecules();
//                    List<InChI> products = reaction.getProductMolecules();
//
//                    Integer reactantIndex = reactants.indexOf( reactant );
//                    Integer productIndex = products.indexOf( reactant );
//
//                    if ( productIndex != -1 ) {
//                        if ( reaction.getReactantCompartments().get( reactantIndex ) !=
//                             reaction.getProductCompartments().get( productIndex ) ) {
//
//                            // molecule switched compartments
//                            compoundMap.put( reactant , compoundMap.containsKey( reactant ) ?
//                                                        compoundMap.get( reactant ) + 1 : 1 );
//                        }
//                    }
//                }
//            }
//            Map<InChI , Integer> sortedMap = new TreeMap<InChI , Integer>( new ValuesComparator(
//              compoundMap ) );
//            sortedMap.putAll( compoundMap );
//            FileWriter fw = new FileWriter( "/Users/johnmay/Desktop/model-transporters.tsv" );
//
//            Set<String> modelIdentifiers = modelCompoundMap.keySet();
//            fw.write( "Compound Name\tN\t" );
//            for ( String modelId : modelIdentifiers ) {
//                fw.write( modelId + "\t" );
//            }
//            fw.write( "\n" );
//            for ( Entry<InChI , Integer> e : sortedMap.entrySet() ) {
//                fw.write( e.getKey().getName() + "\t" + e.getValue() + "\t" );
//                for ( String modelId : modelIdentifiers ) {
//                    fw.write( ( modelCompoundMap.get( modelId ).contains( e.getKey() ) ? "1" : "" ) +
//                              "\t" );
//                }
//                fw.write( "\n" );
//            }
//            fw.close();
//
//
//
//
//            CSVWriter csv = new CSVWriter( new FileWriter(
//              "/Users/johnmay/Desktop/transport-compounds.tsv" ) , '\t' ,
//                                           '\0' );
//            FileWriter fw2 = new FileWriter(
//              "/Users/johnmay/Desktop/transport-compounds-expanded.tsv" );
//            int count = 0;
//            for ( Entry<InChI , Integer> e : sortedMap.entrySet() ) {
//                List<InChIReaction> reactionList = new ArrayList<InChIReaction>();
//                List<String> mid = new ArrayList<String>();
//                HashSet<InChI> uniqueCompounds = new HashSet<InChI>();
//
//                count++;
//                for ( String modelId : modelIdentifiers ) {
//                    InChI inchi = e.getKey();
//                    if ( modelCompoundReactionMap.get( modelId ).containsKey( inchi ) ) {
//                        for ( InChIReaction modelReaction : modelCompoundReactionMap.get( modelId ).
//                          get( inchi ) ) {
//
//                            Integer leftI = modelReaction.getReactantMolecules().indexOf( inchi );
//                            Integer rightI = modelReaction.getProductMolecules().indexOf( inchi );
//
//                            if ( leftI != -1 && rightI != -1 ) {
//
//                                // only print if the compound is transported
//                                if ( modelReaction.getReactantCompartments().get( leftI ) !=
//                                     modelReaction.getProductCompartments().get( rightI ) ) {
//                                    csv.writeNext(
//                                      new String[]{ modelId , e.getKey().toString() , e.getKey().
//                                          getName() , modelReaction.toString() , modelReaction.
//                                          getIdentifier().
//                                          toString() } );
//                                    reactionList.add( modelReaction );
//                                    mid.add( modelId );
//                                    uniqueCompounds.addAll( modelReaction.getAllReactionMolecules() );
//                                }
//
//                            }
//                        }
//                    }
//                }
//                // now have a trimmed set of reactions,
//                // sort all the compounds
//                List<InChI> compoundList = new ArrayList<InChI>( uniqueCompounds );
//                Collections.sort( compoundList );
//                for ( int i = 0 ; i < reactionList.size() ; i++ ) {
//                    InChIReaction re = reactionList.get( i );
//                    fw2.write( count + "\t" + mid.get( i ) + "\t" + e.getKey().getName() + "\t" );
//                    for ( InChI compound : compoundList ) {
//                        if ( re.getAllReactionMolecules().contains( compound ) ) {
//                            fw2.write( compound.getName() );
//                            // could also write as a html table with images
//                        } else {
//                        }
//                        fw2.write( "\t" );
//                    }
//                    fw2.write( "\n" );
//                }
//
//            }
//            csv.close();
//            fw2.close();
//
//            r.close();
//
//        } catch ( IOException ex ) {
//            System.out.println( "Error:" + ex.getMessage() );
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
//
//
//class ValuesComparator implements Comparator {
//
//    Map base;
//
//
//    public ValuesComparator( Map base ) {
//        this.base = base;
//    }
//
//
//    public int compare( Object a , Object b ) {
//
//        if ( ( Integer ) base.get( a ) < ( Integer ) base.get( b ) ) {
//            return 1;
//        } else {
//            return -1;
//        }
//    }
//
//
//}
//
