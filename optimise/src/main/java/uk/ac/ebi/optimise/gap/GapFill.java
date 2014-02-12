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

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.reaction.Compartment;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.domain.matrix.BasicStoichiometricMatrix;
import uk.ac.ebi.mdk.domain.matrix.StoichiometricMatrix;
import uk.ac.ebi.optimise.SimulationUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * GapFill 2012.01.10
 *
 * @author johnmay
 * @author $Author$ (this version)
 *
 *         Class description
 * @version $Rev$ : Last Changed $Date: 2012-01-10 13:15:20 +0000 (Tue, 10 Jan
 *          2012) $
 */
public class GapFill<M, R> {

    private static final Logger LOGGER = Logger.getLogger(GapFill.class);

    private StoichiometricMatrix<M, R> database;

    private StoichiometricMatrix<M, R> model;

    private StoichiometricMatrix<M, R> s;

    private BiMap<Integer, Integer> modelMap;

    private IloCplex solver;

    /**
     * Linear programming variables
     *
     * w: binary matrix indicates if reaction j produces metabolite i y: binary
     * vector indicates whether database reaction j resolves gap v: flux vector
     */
    private SparseIloBoolMatrix w;

    private IloIntVar[] y;

    private IloNumVar[] v;

    private Set<Integer> cytosolic, other;

    public static boolean REPORT_SINGLE_SOLUTION = false;

    private boolean adjOnly = false;

    /**
     * @param database
     * @param model
     * @throws IloException
     * @throws UnsatisfiedLinkError thrown if libray.path is not setup correct
     */
    public GapFill(StoichiometricMatrix<M, R> database,
                   StoichiometricMatrix<M, R> model,
                   Function<M, Compartment> functor) throws IloException, UnsatisfiedLinkError {

        this.database = database;
        this.model = model;

        this.s = database;

        int nDbReactions = database.getReactionCount();
        int nModelReactions = model.getReactionCount();

        System.out.println("adding model reactions");
        modelMap = s.assign(model);

        System.out.println("model: " + nModelReactions);
        System.out.println("database: " + nDbReactions);
        System.out.println("combined: " + s.getReactionCount() + " merged = " + ((nDbReactions + nModelReactions) - s.getReactionCount()));

        // metabolites in cytosole and non-extracellular
        cytosolic = new HashSet<Integer>();
        other = new HashSet<Integer>();

        for (int i = 0; i < s.getMoleculeCount(); i++) {
            Compartment compartment = functor.apply(s.getMolecule(i));
            if (compartment == Organelle.CYTOPLASM)
                cytosolic.add(i);
            else if (compartment == Organelle.BOUNDARY)
                throw new IllegalArgumentException("boundary metabolites should be removed");
            else if (compartment != Organelle.EXTRACELLULAR)
                other.add(i);
        }

        if (!other.isEmpty())
            throw new IllegalArgumentException("non-simple models not yet supported");

        SimulationUtil.setup();
    }

    void onlyAddAdjacent() {
        this.adjOnly = true;
    }

    /** Mass balance constrains on cytosolic and non cytosolic metabolites. */
    private void cytosolicMassBalance() throws IloException {
        // Massbalance(i)$(cytosol(i) and not extracellular(i)).. sum(j$S(i,j),S(i,j)*v(j))=g=0;
        for (int i : cytosolic) {
            // todo: make sparse
            List<IloNumExpr> values = new ArrayList<IloNumExpr>();
            for (int j = 0; j < s.getReactionCount(); j++) {
                values.add(solver.prod(s.get(i, j),
                                       v[j]));
            }
            solver.addGe(solver.sum(values.toArray(new IloNumExpr[values.size()])),
                         0);
        }
    }

    private static final double UPPER_BOUND = 100;
    private static final double LOWER_BOUND = -100;

    /** Constrain reaction flux */
    private void boundConstraints() throws IloException {
        // constrain flux
        for (int j = 0; j < v.length; j++) {

            // model
            if (modelMap.containsValue(j)) {
                // boundcon1(j)$(model(j)).. v(j) =l= UB(j);
                solver.addLe(v[j], UPPER_BOUND);

                if (s.isReversible(j)) {
                    // boundcon2(j)$(model(j) and rev(j)) .. v(j) =g= LB(j);
                    solver.addGe(v[j], LOWER_BOUND);
                }
                else {
                    // boundcon5(j)$( model (j) and not rev(j)) .. v(j) =g= -1000*y(j);
                    solver.addGe(v[j], solver.prod(y[j], -1000));
                }
            } // database
            else {
                // boundcon3(j)$(database(j) and  not  model(j)) .. v(j) =g= LB(j)*y(j);
                solver.addGe(v[j], solver.prod(y[j], LOWER_BOUND));

                // boundcon4(j)$(database(j) and not model(j)) .. v(j) =l= UB(j)*y(j);
                solver.addLe(v[j], solver.prod(y[j], UPPER_BOUND));
            }
        }
    }

    /**
     * Ensure production of problem metabolite.
     *
     * @param problem index of problem metabolite
     */
    private void productionConstraints(int problem) throws IloException {
        List<IloIntVar> production = new ArrayList<IloIntVar>();

        for (int j = 0; j < s.getReactionCount(); j++) {

            if (s.get(problem, j) == 0)
                continue;

            // min production limit
            // prodcons1(i,j)$(problem(i) and S(i,j) ne 0)..S(i,j)*v(j)=g=1-1000*(1-w(i,j));
            solver.addGe(solver.prod(s.get(problem, j),
                                     v[j]), // Sijvj
                         solver.sum(1,
                                    solver.negative(solver.prod(1000,
                                                                solver.sum(1,
                                                                           solver.negative(w.get(problem, j))))))); // 1-1000*(1-wij)
            // max production limit
            // prodcons2(i,j)$(problem(i) and S(i,j) ne 0)..S(i,j)*v(j)=l=1000*w(i,j);
            solver.addLe(solver.prod(s.get(problem, j),
                                     v[j]), // Sijvj
                         solver.prod(1000, w.get(problem, j))); // 1000*wij        

            production.add(w.get(problem, j));
        }

        // binarycons(i)$(problem(i))..sum(j$(s(i,j) ne 0), w(i,j))=g=1;
        solver.addGe(solver.sum(production.toArray(new IloIntVar[production.size()])), 1);
    }

    private final void init() throws IloException {
        this.solver = new IloCplex();

        // reset variables        
        v = solver.numVarArray(s.getReactionCount(), -1000, 100);
        y = solver.boolVarArray(s.getReactionCount()); // - modelMap.size()
        w = new SparseIloBoolMatrix(solver);

        cytosolicMassBalance();
        boundConstraints();
    }

    /**
     * Access the column indices (database) of reactions that can resolve
     * dead-end metabolite at row index 'i' (model)
     *
     * @return
     */
    public List<Set<Integer>> getCandidates(int problem) throws IloException {

        problem = s.getIndex(model.getMolecule(problem));


        long t0 = System.nanoTime();
        System.out.print("initalising...");
        init();
        productionConstraints(problem);

        // constraint such as to only search direct neighbors
        if (adjOnly) {
            for (int j = 0; j < s.getReactionCount(); j++) {
                if (s.get(problem, j) == 0)
                    solver.addEq(y[j], 0);
            }
        }

        solver.addMinimize(solver.sum(y));
        long t1 = System.nanoTime();
        System.out.println("done " + TimeUnit.NANOSECONDS.toSeconds(t1 - t0) + " s");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        solver.setOut(out);
        solver.setWarning(out);

        List<Set<Integer>> result = new ArrayList<Set<Integer>>();

        try {
            while (solver.solve()) {

                double[] solutions = solver.getValues(y);

                Set<Integer> partial = new HashSet<Integer>();

                for (int j = 0; j < solutions.length; j++) {
                    if (solutions[j] == 1.0d) {
                        partial.add(j);
                    }
                }

                result.add(partial);

                if (REPORT_SINGLE_SOLUTION)
                    break;

                Set<IloIntVar> constrain = new HashSet<IloIntVar>();
                for (int j : partial)
                    constrain.add(y[j]);
                solver.addCut(solver.le(solver.sum(constrain.toArray(new IloIntVar[constrain.size()])),
                                        constrain.size() - 1));
                solver.addCut(solver.le(solver.sum(y),
                                        constrain.size()));
            }
        } catch (Exception e) {
            System.err.println("CPLEX Error: " + e.getMessage());
        }

        try {
            solver.end();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            out.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }


    public List<Set<R>> getCandidateReactions(int i) throws IloException {
        List<Set<R>> rxns = new ArrayList<Set<R>>();
        for (Set<Integer> solution : getCandidates(i)) {
            rxns.add(new HashSet<R>(Collections2.transform(solution, new Function<Integer, R>() {
                @Override public R apply(Integer j) {
                    return s.getReaction(j);
                }
            })));
        }
        return rxns;
    }

    public List<Set<R>> getCandidateReactions(M problem) throws IloException {
        return getCandidateReactions(model.getIndex(problem));
    }

    public static void main(String[] args) throws IloException {

        SimulationUtil.setup();

        BasicStoichiometricMatrix model = BasicStoichiometricMatrix.create();

        model.addReaction(new String[0], new String[]{"A"}, true);
        //       model.addReaction(new String[]{"E"}, new String[0], false);

        model.addReaction("A => B", false);
        model.addReaction("B => C", false);
        model.addReaction("B => D", false);
        //model.addReaction("D => E", false);
        model.addReaction("E => C", false);
        // model.addReaction("G => E", false);

        model.addReaction(new String[]{"C"}, new String[0], true);


        //model.addReaction("B => D", false);
//        model.addReaction("D => E", false);
        //model.addReaction("E => F", false);
        //model.addReaction("F => G", false);
        //model.addReaction("G => C", false);

        model.display();
        try {
            System.out.println("Unconsumed metabolites:");
            for (int i : new GapFind(model).getUnconsumedMetabolites()) {
                System.out.println(model.getMolecule(i));
            }
            System.out.println("Unproduced metabolites:");
            for (int i : new GapFind(model).getUnproducedMetabolites()) {
                System.out.println(model.getMolecule(i));
            }
        } catch (Exception e) {

        }


        BasicStoichiometricMatrix reference = BasicStoichiometricMatrix.create();

        reference.addReactionWithName("d1", "J => F");
        reference.addReactionWithName("d2", "D => E");
        reference.addReactionWithName("d3", "I => F");
        reference.addReactionWithName("d4", "E => F");
        reference.addReactionWithName("d5", "E => I");

        reference.display();

        GapFill<String, String> gf = new GapFill<String, String>(reference, model, new Function<String, Compartment>() {
            @Override public Compartment apply(String s) {
                return Organelle.CYTOPLASM;
            }
        });

        System.out.println(gf.getCandidateReactions(model.getIndex("E")));

    }
}
