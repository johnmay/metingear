/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.optimise.gap;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import java.util.*;
import uk.ac.ebi.metabolomes.core.reaction.matrix.BasicStoichiometricMatrix;
import uk.ac.ebi.metabolomes.core.reaction.matrix.StoichiometricMatrix;
import uk.ac.ebi.optimise.SimulationUtil;


/**
 *
 *
 * @author johnmay
 */
public class GapFillOld {

    private GapFillOld() throws IloException, UnsatisfiedLinkError {
        cplex = new IloCplex();
        cplex.setOut(System.out);
    }

    private StoichiometricMatrix s;

    private StoichiometricMatrix model;

    private Set<Integer> modelRxns;

    private IloCplex cplex;

    /**
     * Constraints
     *
     */
    private IloIntVar[][] w; // binary variable for whether reaction j produces metabolite i (1) or not (0)

    private IloIntVar[] y;

    private IloNumVar[] v;


    public void setup(StoichiometricMatrix database, StoichiometricMatrix model) {
        this.s = database;
        modelRxns = this.s.assign(model).values();
    }

    private IloIntVar[] toSolve;


    private void setupConstraints(int i)
            throws IloException {

        System.out.println("Setting up for " + s.getMolecule(i));

        y = new IloIntVar[s.getReactionCount()];
        // value to optimize
        List<IloIntVar> binvar = new ArrayList();

        for (int j = 0; j < s.getReactionCount(); j++) {
            y[j] = cplex.boolVar();
            if (!modelRxns.contains(j)) {
                binvar.add(y[j]);
            }
        }


        // reaction fluxes
        v = cplex.numVarArray(s.getReactionCount(), -100, 100);

        // flux can take any value between 0 and 100
        // LB ≤ Vj ≤ UB , j ∈ Model
        // and
        // LB Yj ≤ Vj ≤ Yj UB , j ∈ Database

        binaryConstraints();


        for (int j = 0; j < s.getReactionCount(); j++) {

            // j ∈ Model
            if (modelRxns.contains(j)) {
                //skip
                cplex.addGe(v[j], 0);
                cplex.addLe(v[j], 100);
            } // j ∈ Database
            else {
                System.out.println(j);
                cplex.addGe(v[j], cplex.prod(y[j], -1000));
                cplex.addLe(v[j], cplex.prod(y[j], 100));
            }


        }

        addProductionConstraints(i);
        cplex.add(getMassBalanceConstraint());

        // database y's

        toSolve = binvar.toArray(new IloIntVar[0]);
        cplex.addMinimize(cplex.sum(y));

    }


    private Integer[] solve()
            throws IloException {

        cplex.solve();

//        List<Double> problemMetabolites = new ArrayList<Double>();
        double[] solutions = cplex.getValues(y);

        for (int i = 0; i < solutions.length; i++) {
            System.out.println(solutions[i]);
        }

        return null;
//        return problemMetabolites.toArray(new Integer[0]);
    }


    private void binaryConstraints()
            throws IloException {

        w = new IloIntVar[s.getMoleculeCount()][s.getReactionCount()];

        for (int i = 0; i < s.getMoleculeCount(); i++) {
            w[i] = cplex.boolVarArray(s.getReactionCount());
        }

    }


    /**
     * @brief Set min and max production constraints for each molecule @f[
     * S_{ij} v_{j} \geq \epsilon w_{ij} @f] @f[ S_{ij} v_{j} \leq E w_{ij} @f]
     *
     * @throws IloException
     */
    public void addProductionConstraints(int i)
            throws IloException {


        List<IloIntVar> values = new ArrayList<IloIntVar>();

        for (int j = 0; j < s.getReactionCount(); j++) {


            if (s.get(i, j) != 0) {

                // min production limit
                cplex.addGe(cplex.prod(s.get(i, j).intValue(),
                                       v[j]), // Sijvj
                            cplex.sum(1,
                                      cplex.negative(cplex.prod(1000,
                                                                cplex.sum(1,
                                                                          cplex.negative(w[i][j])))))); // 1-1000*(1-wij)
                // max production limit
                cplex.addLe(cplex.prod(s.get(i, j).intValue(),
                                       v[j]), // Sijvj
                            cplex.prod(1000, w[i][j])); // 1000*wij        

                values.add(w[i][j]);


            }


        }

        cplex.addGe(cplex.sum(values.toArray(new IloIntVar[0])), 1);


    }


    public IloAddable[] getMassBalanceConstraint()
            throws IloException {
        IloAddable[] positiveFlux = new IloAddable[s.getMoleculeCount()];

        for (int i = 0; i < s.getMoleculeCount(); i++) {
            IloNumExpr[] values = new IloNumExpr[s.getReactionCount()];

            for (int j = 0; j < s.getReactionCount(); j++) {
                values[j] = cplex.prod(v[j], s.get(i, j));
            }

            // The sum of the reaction flux for this metabolite should
            // be greater then zero
            positiveFlux[i] = cplex.ge(cplex.sum(values), 0);
        }

        return positiveFlux;
    }


    public static void main(String[] args) throws IloException, UnsatisfiedLinkError {

        SimulationUtil.setup();

        BasicStoichiometricMatrix model = BasicStoichiometricMatrix.create();

        model.addReaction(new String[0], new String[]{"A"});
        model.addReaction(new String[]{"C"}, new String[0]);

        model.addReaction("A => D");
        model.addReaction("D => E");
        model.addReaction("F => G");
        model.addReaction("G => C");

        model.display(System.out, ' ', "0", 4, 4);

        System.out.println("Non-production Metabolites");
        for (int i : new GapFind(model).getUnproducedMetabolites()) {
            System.out.println(model.getMolecule(i));
        }


        BasicStoichiometricMatrix ref = BasicStoichiometricMatrix.create();

        ref.addReaction("I => F");
        ref.addReaction("E => F");
        ref.addReaction("E => I");



        ref.display();


        GapFillOld gf = new GapFillOld();
        gf.setup(ref, model);

        ref.display();

        gf.setupConstraints(1);
        gf.solve();


    }
}
