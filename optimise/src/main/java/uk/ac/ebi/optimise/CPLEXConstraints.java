/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
package uk.ac.ebi.optimise;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.matrix.StoichiometricMatrix;
import uk.ac.ebi.mdk.domain.matrix.StoichiometricMatrixImpl;


/**
 *
 * CPLEXConstraints 2012.01.10
 *
 * @version $Rev$ : Last Changed $Date$
 * @author johnmay
 * @author $Author$ (this version)
 *
 * Class description
 *
 */
public class CPLEXConstraints {

    private static final Logger LOGGER = Logger.getLogger(CPLEXConstraints.class);

    private static final IloCplex solver = getSolver();


    private static IloCplex getSolver() {
        SimulationUtil.setup();
        try {
            return new IloCplex();
        } catch (IloException ex) {
            System.err.println("Unable to create instance"
                               + " of CPLEX solver: " + ex.getMessage());
        } catch (UnsatisfiedLinkError ex) {
            System.err.println("Unable to create instance"
                               + " of CPLEX solver: link error");
        }
        return null;
    }


    public static final IloAddable[] getProductionConstraints(StoichiometricMatrix<?,?> s, IloNumVar[] v, IloIntVar[][] w, int i) throws IloException {

        List<IloIntVar> production = new ArrayList<IloIntVar>();
        List<IloAddable> constraints = new ArrayList<IloAddable>();

        for (int j = 0; j < s.getReactionCount(); j++) {

            if (s.get(i, j) != 0) {

                // min production limit
                constraints.add(solver.ge(solver.prod(s.get(i, j).intValue(),
                                                      v[j]), // Sijvj
                                          solver.sum(1,
                                                     solver.negative(solver.prod(1000,
                                                                                 solver.sum(1,
                                                                                            solver.negative(w[i][j]))))))); // 1-1000*(1-wij)
                // max production limit
                constraints.add(solver.le(solver.prod(s.get(i, j).intValue(),
                                                      v[j]), // Sijvj
                                          solver.prod(1000, w[i][j]))); // 1000*wij        

                production.add(w[i][j]);

            }


        }

        constraints.add(solver.ge(solver.sum(production.toArray(new IloIntVar[0])), 1));

        return constraints.toArray(new IloAddable[0]);


    }


    public static final IloAddable[] getPositiveMassBalance(StoichiometricMatrix<?,?> s, IloNumVar[] v)
            throws IloException {
        IloAddable[] positiveFlux = new IloAddable[s.getMoleculeCount()];

        for (int i = 0; i < s.getMoleculeCount(); i++) {
            IloNumExpr[] values = new IloNumExpr[s.getReactionCount()];

            for (int j = 0; j < s.getReactionCount(); j++) {
                values[j] = solver.prod(v[j], s.get(i, j));
            }

            // The sum of the reaction flux for this metabolite should
            // be greater then zero
            positiveFlux[i] = solver.ge(solver.sum(values), 0);
        }

        return positiveFlux;
    }
}
