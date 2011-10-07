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

package mnb.view.old;

import edu.uci.ics.jung.graph.SparseMultigraph;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.metabolomes.core.gene.OldGeneProduct;
import uk.ac.ebi.metabolomes.core.reaction.BiochemicalReaction;


/**
 * ReactionGraph.java
 *
 *
 * @author johnmay
 * @date May 27, 2011
 */
public class ReactionGraph
  extends SparseMultigraph<String, String> {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      ReactionGraph.class);
    private HashSet<String> vertixHashSet;


    public ReactionGraph() {
        vertixHashSet = new HashSet<String>();
    }


    /**
     * Loads the reactions into the graph
     */
    public void addReactions(List<Reaction> reactions) {
        logger.debug("Adding " + reactions.size() + " reactions");
        for( Reaction reaction : reactions ) {
            //todo String modifier = getModifierName(reaction);
            //addVertex(modifier);
            //addMolecules(modifier,
            //             reaction.getReactants(),
            //             reaction.getReactantCoefficients());
            //addMolecules(modifier,
            //             reaction.getProducts(),
            //             reaction.getProductCoefficients());
        }
    }


    /**
     * Access the modifier for this reaction. If this is a biochemical
     * reaction the id(s) of the enzymes are returned. If the reaction is
     * not involve any modifiers then it is considered spontaneous and this
     * description is returned
     * @param reaction
     * @return
     */
    private String getModifierName(IReaction reaction) {
        if( reaction instanceof BiochemicalReaction ) {
            BiochemicalReaction biochemicalReaction = (BiochemicalReaction) reaction;
            List<OldGeneProduct> modifiers = biochemicalReaction.getModifiers();
            // could add option here fore what to use.. e.g. ID, Name, EC etc..
            return StringUtils.join(modifiers, ',');
        }
        return "Spontaneous";
    }


    private void addMolecules(String modifier,
                              IMoleculeSet molecules,
                              Double[] coefficients) {
        logger.debug("Adding reaction to graph... " + modifier);
        for( int i = 0 ; i < molecules.getAtomContainerCount() ; i++ ) {
            String id = molecules.getMolecule(i).getID();
            System.out.println(id);
            if( !containsVertex(id) ) {
                addVertex(id);
            }
            addEdge("edge_" + getEdgeCount(), modifier, id);
        }
    }


}

