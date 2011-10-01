/*
 *  DeadEndDetector.java
 *
 *  CheMet Version 1.0
 *
 *  2011.07.11
 *
 *  This file is part of the ChemMet library
 *
 *  The ChemMet library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ChemMet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with ChemMet.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.metabolomes.optimise;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import org.apache.log4j.Logger;
import uk.ac.ebi.metabolomes.core.reaction.matrix.*;

import java.io.*;
import java.util.*;

/**
 * DeadEndDetector
 * @author  John May
 * @author  $Author$ – $LastChangedDate$ (this version)
 * @date    2011.06.24
 * @version $Revision$
 * @brief   Locates various types of gaps in metabolic network. The class
 *          identifies root non-production and downstream non-production
 *          metabolites as well as terminal non-consumption and upstream
 *          non-consumption. Upstream and down-stream are identified using
 *          Mixed Integer Linear Programming using CPLEX ILOG library
 */
public class DeadEndDetector {

    private static final Logger LOGGER = Logger.getLogger( DeadEndDetector.class );
    private IloCplex cplex;
    private StoichiometricMatrix s; // Stoichiometric matrix
    private IloIntVar[] v; // flux vector (size = n reactions)
    private IloIntVar[] xnp; // binary variable for maximising
    private IloIntVar[][] w; // binary variable for whether reaction j produces metabolite i (1) or not (0)
    private IloAddable[] posMassBalance;
    private IloAddable[] negMassBalance;

    /**
     *
     * @param s Matrix of stoichiometries, a column per reaction
     */
    public DeadEndDetector( StoichiometricMatrix s ) {
        // todo handle our custom stoichiometric
        this.s = s;

        try {
            cplex = new IloCplex();
            cplex.setOut( null );
            setupConstraints();
        } catch ( IloException ex ) {
            LOGGER.error( "Could no create instance of IloCplex: " , ex );
        }
    }

    /**
     * @brief Sets up the constraints of the problem
     * @throws IloException
     */
    private void setupConstraints()
            throws IloException {
        // value to optimize
        xnp = cplex.intVarArray( s.getMoleculeCount() ,
                                 0 ,
                                 1 );
        // flux can take any value between 0 and 100
        // LB ≤ v ≤ UB , j ∈ Model
        v = cplex.intVarArray( s.getReactionCount() ,
                               0 ,
                               100 );

        // objective function
        cplex.addMaximize( cplex.sum( xnp ) );

        // add the binary constraints
        binaryConstraints();

        // production constraints min and
        // production constrains max
        productionConstraints();

        // build mass constraints
        posMassBalance = nonProductionMassBalanceConstraint();
        negMassBalance = nonConsumtionMassBalanceConstraint();
    }

    /**
     * @brief  Add the binary constraints of \f[ W_{ij}  \f]
     * @throws IloException
     */
    private void binaryConstraints()
            throws IloException {
        w = new IloIntVar[ s.getMoleculeCount() ][ s.getReactionCount() ];

        for ( int i = 0; i < s.getMoleculeCount(); i++ ) {
            w[i] =
            cplex.intVarArray( s.getReactionCount() ,
                               0 ,
                               1 );

            IloLinearIntExpr term = cplex.linearIntExpr();

            for ( int j = 0; j < s.getReactionCount(); j++ ) {
                if ( s.get( i , j ) > 0 ) {
                    term.addTerm( s.get( i , j ).intValue() ,
                                  w[i][j] );
                }
            }

            cplex.addEq( term , xnp[i] ).setName( "Binary Constraints" );
        }
    }

    /**
     * @brief Set min and max production constraints for each molecule
     * @f[  S_{ij} v_{j} \geq \epsilon w_{ij} @f]
     * @f[  S_{ij} v_{j} \leq E w_{ij} @f]
     * @throws IloException
     */
    public void productionConstraints()
            throws IloException {
        for ( int i = 0; i < s.getMoleculeCount(); i++ ) {
            for ( int j = 0; j < s.getReactionCount(); j++ ) {
                if ( s.get( i , j ) > 0 ) { // and belongs to irreversible set
                    // min production limit
                    cplex.addGe( cplex.prod( s.get( i , j ).intValue() ,
                                             v[j] ) , // Sijvj
                                 cplex.prod( 0.001 , w[i][j] ) ); // εwij
                    //     // max production limit

                    cplex.addLe( cplex.prod( s.get( i , j ).intValue() ,
                                             v[j] ) , // Sijvj
                                 cplex.prod( 100 , w[i][j] ) ); // εwij
                }
            }
        }
    }

    /**
     * @brief Generates a mass balance constraint for non-production metabolites
     *       \f[ \sum{S_{ij} v_{j}} \geq 0 \quad | \quad \forall i \in N \f]
     *
     * @param  one a variable
     * @return Array of addable constraints
     * @throws IloException Exception is
     */
    public IloAddable[] nonProductionMassBalanceConstraint()
            throws IloException {
        IloAddable[] positiveFlux = new IloAddable[ s.getMoleculeCount() ];

        for ( int i = 0; i < s.getMoleculeCount(); i++ ) {
            IloNumExpr[] values = new IloNumExpr[ s.getReactionCount() ];

            for ( int j = 0; j < s.getReactionCount(); j++ ) {
                values[j] =
                cplex.prod( v[j] ,
                            s.get( i , j ) );
            }

            // The sum of the reaction flux for this metabolite should
            // be greater then zero
            positiveFlux[i] =
            cplex.ge( cplex.sum( values ) ,
                      0 );
        }

        return positiveFlux;
    }

    /**
     * Generates a mass balance constraint for non-consumption metabolites
     * \f[ \sum{S_{ij} v_{j}} \leq 0 \quad | \quad \forall i \in N \f]
     * @throws IloException
     */
    public IloAddable[] nonConsumtionMassBalanceConstraint()
            throws IloException {
        IloAddable[] negFlux = new IloAddable[ s.getMoleculeCount() ];

        for ( int i = 0; i < s.getMoleculeCount(); i++ ) {
            IloNumExpr[] values = new IloNumExpr[ s.getReactionCount() ];

            for ( int j = 0; j < s.getReactionCount(); j++ ) {
                values[j] =
                cplex.prod( v[j] ,
                            s.get( i , j ) );
            }

            negFlux[i] =
            cplex.le( cplex.sum( values ) ,
                      0 );
        }

        return negFlux;
    }

    public Integer[] findNonProductionMetabolites()
            throws IloException {
        cplex.remove( negMassBalance );
        cplex.add( posMassBalance );

        return solve();
    }

    public Integer[] findNonConsumptionMetabolites()
            throws IloException {
        cplex.remove( posMassBalance );
        cplex.add( negMassBalance );

        return solve();
    }

    /**
     * Finds root non-production metabolites using the topology of the matrix
     * @return indices of root non-production metabolites
     */
    public Integer[] getTerminalNCMetabolites() {
        List<Integer> rootNonProductionMetabolites = new ArrayList<Integer>();

        for ( int i = 0; i < s.getMoleculeCount(); i++ ) {
            int positiveStoichiometry = 0;
            int negitiveStoichiometry = 0;

            for ( int j = 0; j < s.getReactionCount(); j++ ) {
                if ( s.get( i , j ) > 0 ) {
                    positiveStoichiometry++;
                } else if ( s.get( i , j ) < 0 ) {
                    negitiveStoichiometry++;
                }
            }

            if ( ( positiveStoichiometry > 0 ) && ( negitiveStoichiometry == 0 ) ) {
                rootNonProductionMetabolites.add( i );
            }
        }

        return rootNonProductionMetabolites.toArray( new Integer[ 0 ] );
    }

    /**
     * Gets the terminal non-consumption metabolites
     * @return
     */
    public Integer[] getRootNPMetabolites() {
        List<Integer> rootNonProductionMetabolites = new ArrayList<Integer>();

        for ( int i = 0; i < s.getMoleculeCount(); i++ ) {
            int positiveStoichiometry = 0;
            int negitiveStoichiometry = 0;

            for ( int j = 0; j < s.getReactionCount(); j++ ) {
                if ( s.get( i , j ) > 0 ) {
                    positiveStoichiometry++;
                } else if ( s.get( i , j ) < 0 ) {
                    negitiveStoichiometry++;
                }
            }

            if ( ( positiveStoichiometry == 0 ) && ( negitiveStoichiometry > 0 ) ) {
                rootNonProductionMetabolites.add( i );
            }
        }

        return rootNonProductionMetabolites.toArray( new Integer[ 0 ] );
    }

    private Integer[] solve()
            throws IloException {
        cplex.solve();

        List<Integer> problemMetabolites = new ArrayList<Integer>();
        double[] xSolutions = cplex.getValues( xnp );

        for ( int i = 0; i < xSolutions.length; i++ ) {
            if ( xSolutions[i] == 0.0d ) {
                problemMetabolites.add( i );
            }
        }

        return problemMetabolites.toArray( new Integer[ 0 ] );
    }

    public static void main( String[] args )
            throws IloException , FileNotFoundException , IOException {
        System.out.println( System.getProperties().getProperty( "java.library.path" ) );

//        BasicStoichiometricMatrix s = new BasicStoichiometricMatrix();
//        // internal reactions
//        s.addReaction( "A => B" );
//        s.addReaction( "B => C" );
//        s.addReaction( "B => D" );
//        s.addReaction( "D => E" );
//        s.addReaction( "F => G" );
//        s.addReaction( "G => C" );
//
//        // exchange reactions
//        s.addReaction( new String[]{} , new String[]{ "A" } );
//        s.addReaction( new String[]{ "C" } , new String[]{} );
//        // print
//        s.display( System.out , ' ' , "0" , null , 2 , 2 );
        // gap find

        /** USAGE: */
//        BasicStoichiometricMatrix s = ReactionMatrixIO.readBasicStoichiometricMatrix(
//                new FileReader( "/Users/johnmay/Desktop/s.tsv" ) , '\t' );
//
//        DeadEndDetector deadEndDetector = new DeadEndDetector( s );
//
//        // NP: Non-production, NC: Non-consumption
//        HashSet<Integer> rootNPMetabolites = new HashSet( Arrays.asList( deadEndDetector.getRootNPMetabolites() ) );
//        HashSet<Integer> terminalNCMetabolites = new HashSet( Arrays.asList( deadEndDetector.getTerminalNCMetabolites() ) );
//
//
//        // Create a tab seperate value write using OpenCSV
//        String tsvPropertiesFile = "/Users/johnmay/Desktop/s_problemmetabolites.tsv";
//        BufferedWriter writer = new BufferedWriter( new FileWriter( tsvPropertiesFile ) );
//        char sep = '\t';        // coloumn delimiter
//        char quote = '\0';   // the quote character to use
//        CSVWriter tsv = new CSVWriter( writer , sep , quote );
//
//        // write the headers
//        tsv.writeNext( new String[]{ "Identifier" , "Problem" } );
//
//        // for each molecule write whehter it is NP or NC dead-end
//        for ( int i = 0; i < s.getMoleculeCount(); i++ ) {
//            String value = rootNPMetabolites.contains( i ) ? "2" :
//                           terminalNCMetabolites.contains( i ) ? "1" :
//                           "0";
//            String[] row = new String[]{ s.getMolecule( i ) , value };
//            tsv.writeNext( row );
//        }
//
//        // also right the reaction names with '0'
//        for ( int i = 0; i < s.getReactionCount(); i++ ) {
//            tsv.writeNext( new String[]{ s.getReaction( i ) , "0" } );
//        }
//
//        tsv.close();
    }
}
