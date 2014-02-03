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
import ilog.concert.IloAddable;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    private StoichiometricMatrix<M, R> combined;

    /**
     * Bi-directional hash maps provide look-up of database/model reaction index
     * in the combined matrix and vise versa
     */
    private BiMap<Integer, Integer> databaseMap;

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

        this.combined = database.newInstance(database.getMoleculeCount(),
                                             database.getReactionCount());

        databaseMap = combined.assign(database);
        modelMap    = combined.assign(model);

        System.out.println("model: " + model.getReactionCount());
        System.out.println("database: " + database.getReactionCount());
        System.out.println("combined: " + combined.getReactionCount());
                                
        // remove intersect from database (note the databases is the base so
        // each key=value)
        for (Integer j : modelMap.values()) {
            databaseMap.remove(j);        
        }

        // metabolites in cytosole and non-extracellular
        cytosolic = new HashSet<Integer>();
        other = new HashSet<Integer>();

        for (int i = 0; i < combined.getMoleculeCount(); i++) {
            Compartment compartment = functor.apply(combined.getMolecule(i));
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

    /** Mass balance constrains on cytosolic and non cytosolic metabolites. */
    private void cytosolicMassBalance() throws IloException {
        // Massbalance(i)$(cytosol(i) and not extracellular(i)).. sum(j$S(i,j),S(i,j)*v(j))=g=0;
        for (int i : cytosolic) {
            // todo: make sparse
            IloNumExpr[] values = new IloNumExpr[combined.getReactionCount()];
            for (int j = 0; j < combined.getReactionCount(); j++) {
                values[j] = solver.prod(combined.get(i, j),
                                        v[j]);
            }
            solver.addGe(solver.sum(values),
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

                if (combined.isReversible(j)) {
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

        for (int j = 0; j < combined.getReactionCount(); j++) {

            if (combined.get(problem, j) == 0)
                continue;

            // min production limit
            // prodcons1(i,j)$(problem(i) and S(i,j) ne 0)..S(i,j)*v(j)=g=1-1000*(1-w(i,j));
            solver.addGe(solver.prod(combined.get(problem, j),
                                                  v[j]), // Sijvj
                                      solver.sum(1,
                                                 solver.negative(solver.prod(1000,
                                                                             solver.sum(1,
                                                                                        solver.negative(w.get(problem, j))))))); // 1-1000*(1-wij)
            // max production limit
            // prodcons2(i,j)$(problem(i) and S(i,j) ne 0)..S(i,j)*v(j)=l=1000*w(i,j);
            solver.addLe(solver.prod(combined.get(problem, j),
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
        v = solver.numVarArray(combined.getReactionCount(), -1000, 100);
        y = solver.boolVarArray(combined.getReactionCount()); // - modelMap.size()
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
    public List<Integer> getCandidates(int problem) throws IloException {

        problem = combined.getIndex(model.getMolecule(problem));
        
        init();
        productionConstraints(problem);

//        List<IloIntVar> dbY = new ArrayList<IloIntVar>(databaseMap.size());
//        for (Integer dbRxn : databaseMap.values()) {
//            dbY.add(y[dbRxn]);
//        }
//        solver.addMinimize(solver.sum(dbY.toArray(new IloIntVar[dbY.size()])));
        solver.addMinimize(solver.sum(y));

        boolean solved = solver.solve();

        List<Integer> candidates = new ArrayList<Integer>();

        if (solved) {

            double[] solutions = solver.getValues(y);
            
            for (int j = 0; j < solutions.length; j++) {
                if (solutions[j] == 1.0d) {
                    candidates.add(j);
                }
            }

        }

        return candidates;

    }


    public List<R> getCandidateReactions(int i) throws IloException {
        List<R> rxns = new ArrayList<R>();
        
        for (Integer j : getCandidates(i)) {
            rxns.add(combined.getReaction(j));
        }
        return rxns;
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
        model.addReaction("G => E", false);

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
        reference.addReactionWithName("d5", "C => E");
        
        reference.display();

        GapFill<String, String> gf = new GapFill<String, String>(reference, model, new Function<String, Compartment>() {
            @Override public Compartment apply(String s) {
                return Organelle.CYTOPLASM;
            }
        });

        System.out.println(gf.getCandidateReactions(model.getIndex("G")));

    }
}
