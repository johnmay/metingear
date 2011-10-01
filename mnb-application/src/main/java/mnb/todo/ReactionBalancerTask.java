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
package mnb.todo;

import java.util.ArrayList;
import java.util.List;
import uk.ac.ebi.mnb.core.ModelUtils;
import org.openscience.cdk.interfaces.IMolecule;
import uk.ac.ebi.metabolomes.descriptor.observation.JobParameters;
import uk.ac.ebi.metabolomes.run.RunnableTask;

/**
 * ReactionBalancerTask.java
 *
 *
 * @author johnmay
 * @date May 24, 2011
 */
public class ReactionBalancerTask
        extends RunnableTask {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ReactionBalancerTask.class );
    private final static IMolecule proton = ModelUtils.makeProton();
    private final static IMolecule water = ModelUtils.makeWater();
    private final static IMolecule oxygen = ModelUtils.makeOxygen();
    private List<IMolecule> intialBalancingMolecules;
  
    public ReactionBalancerTask( JobParameters params ) {
        super( params );

        // instantiate collections here
        intialBalancingMolecules = new ArrayList<IMolecule>();

    }

    @Override
    public void prerun() {

        // set up the balancing molecule
        JobParameters params = super.getJobParameters();

        if ( params.containsKey( ReactionBalancerParamType.USE_WATER ) ) {
            intialBalancingMolecules.add( water );
        }
        if ( params.containsKey( ReactionBalancerParamType.USE_OXYGEN ) ) {
            intialBalancingMolecules.add( oxygen );
        }
        if ( params.containsKey( ReactionBalancerParamType.USE_PROTON ) ) {
            intialBalancingMolecules.add( proton );
        }

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

    @Override
    public String getTaskDescription() {
        return "Using linear programming to balance reaction reactants/products";
    }

    @Override
    public String getTaskCommand() {
        return "Stoichiometry Balance";
    }
}
