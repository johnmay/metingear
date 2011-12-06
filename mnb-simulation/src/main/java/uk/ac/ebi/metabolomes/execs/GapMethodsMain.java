//
//package uk.ac.ebi.metabolomes.execs;
//
//import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;
//import ilog.concert.IloException;
//import org.apache.xerces.impl.xpath.regex.RegularExpression;
//
//import uk.ac.ebi.metabolomes.biowh.BiowhConnection;
//import uk.ac.ebi.metabolomes.core.reaction.matrix.InChIStoichiometricMatrix;
//import uk.ac.ebi.metabolomes.gap.filling.GapFillingFactory;
//import uk.ac.ebi.metabolomes.gap.filling.GapFillingMethod;
//import uk.ac.ebi.metabolomes.identifier.InChI;
//import uk.ac.ebi.metabolomes.io.homology.ReactionMatrixIO;
//import uk.ac.ebi.optimise.gap.GapFind;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.List;
//import uk.ac.ebi.metabolomes.core.reaction.BiochemicalReaction;
//
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
///**
// * GapMethodsMain.java – Jul 6, 2011
// *
// * @author johnmay <johnmay@ebi.ac.uk>
// */
//public class GapMethodsMain {
//
//    private static final org.apache.log4j.Logger logger =
//                                                 org.apache.log4j.Logger.getLogger(
//      GapMethodsMain.class);
//
//
//    public static void main(String[] args)
//      throws IOException, SQLException, IloException {
//        BiowhConnection connection = new BiowhConnection();
//
//        ReactionMatrixIO.setSeparator('\t');
//        InChIStoichiometricMatrix s =
//                                  ReactionMatrixIO.readInChIStoichiometricMatrix(new FileReader(
//          "/Users/johnmay/Desktop/s.tsv"));
//        GapFind locator = new GapFind(s);
//        Integer[] nonConsumptionIs = locator.getTerminalNCMetabolites();
//        GapFillingMethod gapFiller = GapFillingFactory.getSingleExpansion(s);
//
//        InChI ref = new InChI("InChI=1S/C7H7NO2/c8-6-4-2-1-3-5(6)7(9)10/h1-4H,8H2,(H,9,10)/p-1");
//
//        int count = 0;
//
//        for( InChI molecule : s.getMolecules() ) {
//            RegularExpression regex = new RegularExpression("p[-+][[:digit:]]");
////            System.out.println( molecule + "\t" +  regex.matches( molecule.getInchi()) );
//            count += (regex.matches(molecule.getInchi()) ? 1 : 0);
//        }
//
//        System.out.println(count + " moleuces protonated");
//
//        // From looking in cytoscape 40 looks like a good candidate
//        for( int i = 0 ; i < nonConsumptionIs.length ; i++ ) {
//            InChI query = s.getMolecule(nonConsumptionIs[i]);
//            //System.out.println( ReactionLoader.getInstance(  ).getNameForInChI( query ) + ":" + query );
//
//            if( ref.equals(query) ) {
//                List<BiochemicalReaction> candidates = gapFiller.getFillingCandidates(i);
//                for( BiochemicalReaction biochemicalReaction : candidates ) {
//                    System.out.println(biochemicalReaction);
//                }
//            }
//        }
//    }
//
//
//}
//
