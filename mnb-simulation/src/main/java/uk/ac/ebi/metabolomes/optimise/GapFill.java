/*
 *     This file is part of Metabolic Network Builder
 *
 *     Metabolic Network Builder is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.metabolomes.optimise;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;

import ilog.cplex.IloCplex;

import uk.ac.ebi.metabolomes.core.reaction.matrix.StoichiometricMatrix;

/**
 * GapFill.java – MetabolicDevelopmentKit – Jun 30, 2011
 *
 * @author johnmay <johnmay@ebi.ac.uk, john.wilkinsonmay@gmail.com>
 */
public class GapFill
{
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( GapFill.class );
    private IloCplex cplex;

    // entire reaction database represented as a stoichiometric matrix
    private StoichiometricMatrix database;
    private StoichiometricMatrix model;
    private IloIntVar[] y;

    public GapFill( StoichiometricMatrix database, StoichiometricMatrix model )
            throws IloException
    {
        this.database = database;
        this.model = model;

        // binary variable
        y = cplex.intVarArray( database.getReactionCount(  ),
                               0,
                               1 );
    }

    /**
     * Minimise the number of reactions added from the database to fix problem metabolite i
     * @param i
     * @return
     */
    public Integer[] getAddedReactions( int i )
    {
        return new Integer[] {  };
    }
}
