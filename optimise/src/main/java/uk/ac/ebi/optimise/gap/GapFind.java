/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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
package uk.ac.ebi.optimise.gap;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.matrix.StoichiometricMatrix;
import uk.ac.ebi.mdk.domain.matrix.BasicStoichiometricMatrix;
import uk.ac.ebi.optimise.SimulationUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * DeadEndDetector
 *
 * @author John May
 * @author $Author$ – $LastChangedDate: 2011-12-06 18:18:06 +0000
 * (Tue, 06 Dec 2011) $ (this version) @date 2011.06.24
 * @version $Revision$ @brief Locates various types of gaps in metabolic
 * network. The class identifies root non-production and downstream
 * non-production metabolites as well as terminal non-consumption and upstream
 * non-consumption. Upstream and down-stream are identified using Mixed Integer
 * Linear Programming using CPLEX ILOG library
 */
public class GapFind {

    private static final Logger LOGGER = Logger.getLogger(GapFind.class);

    private IloCplex cplex;

    private StoichiometricMatrix<?,?> s; // Stoichiometric matrix

    private IloNumVar[] v; // flux vector (size = n reactions)

    private IloIntVar[] xnp; // binary variable for maximising

    private IloIntVar[][] w; // binary variable for whether reaction j produces metabolite i (1) or not (0)

    private IloAddable[] posMassBalance;

    private IloAddable[] negMassBalance;


    public GapFind() throws Exception, UnsatisfiedLinkError{
        cplex = new IloCplex();
        cplex.setOut(null);
    }

    /**
     *
     * @param s Matrix of stoichiometries, a column per reaction
     *
     */
    public GapFind(StoichiometricMatrix s) throws Exception, UnsatisfiedLinkError {
        // todo handle our custom stoichiometric
        this.s = s;

        cplex = new IloCplex();
        cplex.setOut(null);
        setupConstraints();

    }


    /**
     * @brief Sets up the constraints of the problem
     *
     * @throws IloException
     */
    private void setupConstraints()
            throws IloException {
        // value to optimize
        xnp = cplex.intVarArray(s.getMoleculeCount(),
                                0,
                                1);
        // flux can take any value between 0 and 100
        // LB ≤ v ≤ UB , j ∈ Model
        v = cplex.numVarArray(s.getReactionCount(),
                              -100,
                              100);

        // objective function
        cplex.addMaximize(cplex.sum(xnp));

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
     * @brief Add the binary constraints of \f[ W_{ij} \f]
     *
     * @throws IloException
     */
    private void binaryConstraints()
            throws IloException {
        w = new IloIntVar[s.getMoleculeCount()][s.getReactionCount()];

        for (int i = 0; i < s.getMoleculeCount(); i++) {
            w[i] =
            cplex.intVarArray(s.getReactionCount(),
                              0,
                              1);

            IloLinearIntExpr term = cplex.linearIntExpr();

            for (int j = 0; j < s.getReactionCount(); j++) {
                if (s.get(i, j) > 0) {
                    term.addTerm(s.get(i, j).intValue(),
                                 w[i][j]);
                }
            }

            cplex.addEq(term, xnp[i]).setName("Binary Constraints");
        }
    }


    /**
     * @brief Set min and max production constraints for each molecule @f[
     * S_{ij} v_{j} \geq \epsilon w_{ij} @f] @f[ S_{ij} v_{j} \leq E w_{ij} @f]
     *
     * @throws IloException
     */
    public void productionConstraints()
            throws IloException {
        for (int i = 0; i < s.getMoleculeCount(); i++) {
            for (int j = 0; j < s.getReactionCount(); j++) {
                if (s.get(i, j) > 0) {

                    if (s.isReversible(j)) {


                        // min production limit (reversible)
                        cplex.addGe(cplex.prod(s.get(i, j).intValue(),
                                               v[j]), // Sijvj
                                    cplex.sum(0.001, cplex.negative(cplex.prod(100, cplex.sum(1, cplex.negative(w[i][j])))))); // ε-M(1-wij)

                        // max production limit (reversible)
                        // Sijvj >= Mwij
                        cplex.addGe(cplex.prod(s.get(i, j).intValue(),
                                               v[j]),
                                    cplex.prod(100, w[i][j]));
                    } else {

                        // min production limit
                        cplex.addGe(cplex.prod(s.get(i, j).intValue(),
                                               v[j]), // Sijvj
                                    cplex.prod(0.001, w[i][j])); // εwij
                        // max production limit
                        cplex.addLe(cplex.prod(s.get(i, j).intValue(),
                                               v[j]), // Sijvj
                                    cplex.prod(100, w[i][j])); // εwij
                    }
                }
            }
        }
    }


    /**
     * @brief Generates a mass balance constraint for non-production metabolites
     * \f[ \sum{S_{ij} v_{j}} \geq 0 \quad | \quad \forall i \in N \f]
     *

     * @return Array of addable constraints
     * @throws IloException Exception is
     */
    public IloAddable[] nonProductionMassBalanceConstraint()
            throws IloException {
        IloAddable[] positiveFlux = new IloAddable[s.getMoleculeCount()];

        for (int i = 0; i < s.getMoleculeCount(); i++) {
            IloNumExpr[] values = new IloNumExpr[s.getReactionCount()];

            for (int j = 0; j < s.getReactionCount(); j++) {
                values[j] =
                cplex.prod(v[j],
                           s.get(i, j));
            }

            // The sum of the reaction flux for this metabolite should
            // be greater then zero
            positiveFlux[i] =
            cplex.ge(cplex.sum(values),
                     0);
        }

        return positiveFlux;
    }


    /**
     * Generates a mass balance constraint for non-consumption metabolites \f[
     * \sum{S_{ij} v_{j}} \leq 0 \quad | \quad \forall i \in N \f]
     *
     * @throws IloException
     */
    public IloAddable[] nonConsumtionMassBalanceConstraint()
            throws IloException {
        IloAddable[] negFlux = new IloAddable[s.getMoleculeCount()];

        for (int i = 0; i < s.getMoleculeCount(); i++) {
            IloNumExpr[] values = new IloNumExpr[s.getReactionCount()];

            for (int j = 0; j < s.getReactionCount(); j++) {
                values[j] =
                cplex.prod(v[j],
                           s.get(i, j));
            }

            negFlux[i] =
            cplex.le(cplex.sum(values),
                     0);
        }

        return negFlux;
    }


    public Integer[] getUnproducedMetabolites()
            throws Exception {
        cplex.remove(negMassBalance);
        cplex.add(posMassBalance);

        return solve();
    }


    public Integer[] getUnconsumedMetabolites()
            throws Exception {
        cplex.remove(posMassBalance);
        cplex.add(negMassBalance);

        return solve();
    }


    /**
     * Finds root non-production metabolites using the topology of the matrix
     *
     * @return indices of root non-production metabolites
     */
    public Integer[] getRootUnproducedMetabolites() {
        List<Integer> unproduced = new ArrayList<Integer>();

        for (int i = 0; i < s.getMoleculeCount(); i++) {

            if (!isProduced(i)) {
                unproduced.add(i);
            }
        }

        return unproduced.toArray(new Integer[0]);
    }


    /**
     * Access the terminal non-consumption metabolites
     *
     * @return
     */
    public Integer[] getTerminalUnconsumpedMetabolites() {

        List<Integer> unconsumedMetabolites = new ArrayList<Integer>();

        for (int i = 0; i < s.getMoleculeCount(); i++) {
            if (!isConsumed(i)) {
                unconsumedMetabolites.add(i);
            }
        }

        return unconsumedMetabolites.toArray(new Integer[0]);

    }


    public boolean isProduced(int i) {
        for (int j = 0; j < s.getReactionCount(); j++) {
            if (s.get(i, j) > 0) {
                return true;
            } else if (s.isReversible(j)
                       && s.get(i, j) != 0) {
                return true;
            }
        }
        return false;
    }


    public boolean isConsumed(int i) {
        for (int j = 0; j < s.getReactionCount(); j++) {
            if (s.get(i, j) < 0) {
                return true;
            } else if (s.isReversible(j)
                       && s.get(i, j) != 0) {
                return true;
            }
        }
        return false;
    }


    private Integer[] solve()
            throws IloException {
        cplex.solve();

        List<Integer> problemMetabolites = new ArrayList<Integer>();
        double[] xSolutions = cplex.getValues(xnp);

        for (int i = 0; i < xSolutions.length; i++) {
            if (xSolutions[i] == 0.0d) {
                problemMetabolites.add(i);
            }
        }

        return problemMetabolites.toArray(new Integer[0]);
    }


    public static void main(String[] args)
            throws IloException, FileNotFoundException, Exception {

//        SimulationUtil.setCPLEXLibraryPath("/Users/johnmay/ILOG/CPLEX_Studio_AcademicResearch122/cplex/bin/x86-64_darwin9_gcc4.0");
        SimulationUtil.setup();

        BasicStoichiometricMatrix s = BasicStoichiometricMatrix.create();
        // internal reactions
        s.addReaction("A => B", false);
        s.addReaction("B => D", false);
        s.addReaction("D => E", false);
        s.addReaction("E => F", false);
        s.addReaction("G => C", false);

        // exchange reactions
        s.addReaction(new String[]{}, new String[]{"A"}, false);
        s.addReaction(new String[]{"C"}, new String[]{}, false);
        // print
        s.display(System.out, ' ', "0", 2, 2);
        // gap find



        System.out.println(Arrays.asList(new GapFind(s).getUnproducedMetabolites()));
        System.out.println(Arrays.asList(new GapFind(s).getUnconsumedMetabolites()));

        /**
         * USAGE:
         */
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
