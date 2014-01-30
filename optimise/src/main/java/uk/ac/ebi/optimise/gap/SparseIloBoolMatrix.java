/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
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

import com.google.common.primitives.Ints;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;
import uk.ac.ebi.mdk.domain.matrix.AbstractReactionMatrix;
import uk.ac.ebi.mdk.domain.matrix.ReactionMatrix;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static uk.ac.ebi.mdk.domain.matrix.ReactionMatrix.IndexKey;

/** @author John May */
public class SparseIloBoolMatrix {

    private final IloCplex cplex;
    
    public SparseIloBoolMatrix(IloCplex cplex) {
        this.cplex = cplex;
    }

    private final Map<IndexKey, IloIntVar> data = new TreeMap<IndexKey, IloIntVar>(new Comparator<IndexKey>() {
        @Override public int compare(IndexKey a, IndexKey b) {
            int cmp = Ints.compare(a.i(), b.i());
            if (cmp != 0) {
                return cmp;
            }
            return Ints.compare(a.j(), b.j());
        }
    });

    public void put(int i, int j, IloIntVar var) {
        data.put(new IndexKey(i, j), var);
    }

    public IloIntVar get(int i, int j) {
        IndexKey key = new IndexKey(i, j);
        IloIntVar var = data.get(key);
        if (var == null) {
            try {
                data.put(key, (var = cplex.boolVar()));
            } catch (IloException e) {
                throw new InternalError("Count not create a new cplex bool");
            }
        }
        return var;
    }

    public String toString() {
        return data.toString();
    }
}
