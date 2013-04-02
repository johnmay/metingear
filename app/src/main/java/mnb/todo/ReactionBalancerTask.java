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
package mnb.todo;

import org.openscience.cdk.interfaces.IMolecule;
import uk.ac.ebi.mdk.domain.entity.Entity;
import uk.ac.ebi.mdk.tool.task.RunnableTask;

import java.util.ArrayList;
import java.util.List;

/**
 * ReactionBalancerTask.java
 *
 * @author johnmay
 * @date May 24, 2011
 */
public class ReactionBalancerTask
        extends RunnableTask {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ReactionBalancerTask.class);
    private List<IMolecule> intialBalancingMolecules;

    public ReactionBalancerTask() {

        // instantiate collections here
        intialBalancingMolecules = new ArrayList<IMolecule>();

    }

    @Override
    public void prerun() {
//
//        // set up the balancing molecule
//        JobParameters params = super.getJobParameters();
//
//        if (params.containsKey(ReactionBalancerParamType.USE_WATER)) {
//            intialBalancingMolecules.add(water);
//        }
//        if (params.containsKey(ReactionBalancerParamType.USE_OXYGEN)) {
//            intialBalancingMolecules.add(oxygen);
//        }
//        if (params.containsKey(ReactionBalancerParamType.USE_PROTON)) {
//            intialBalancingMolecules.add(proton);
//        }

//todo//        ReactionList reactionCollection = ( ReactionList ) params.get( JobParamType.REACTION_COLLECTION );
//        if ( reactionCollection == null ) {
//            setErrorStatus();
//            return;
//        }
//
//        List<List<IMoleculeSet>> moleculeSets = new ArrayList<List<IMoleculeSet>>();
//
//        // iterate over the biochemical reactions and order them in a set
//        for ( BiochemicalReaction reaction : reactionCollection.getBiochemicalReactions() ) {
//            // provide the balancing molecules
//           // ReactionBalancer.balance(reaction, innerMoleculeSets);
//        }

    }

    @Override
    public void run() {
    }

    @Override
    public void postrun() {
        //   throw new UnsupportedOperationException( "Not supported yet." );
    }


    public Entity newInstance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
