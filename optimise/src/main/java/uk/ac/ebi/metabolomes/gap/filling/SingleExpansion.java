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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.metabolomes.gap.filling;

/**
 * SingleExpansion.java – Jul 6, 2011
 * The SingleExpansion class fills gaps in metabolic networks but adding reactions that 'fit'
 * with a dead-end metabolite. Reactions 'fit' if they consume/produce the dead-end metabolite
 * and produce/consume another metabolite in the network. The second metabolite may also be a
 * dead-end.
 *
 * The object can be created in the GapFillingFactory class.
 *
 * <h4>Usage:</h4>
 * <pre>
 *
 * </pre>
 *
 * @author johnmay <johnmay@ebi.ac.uk>
 */
public class SingleExpansion
        implements GapFillingMethod {

//    private static final Logger LOGGER = Logger.getLogger( SingleExpansion.class );
//    private InChIStoichiometricMatrix s;
//
//    protected SingleExpansion( InChIStoichiometricMatrix s ) {
//        this.s = s;
//    }
//
//    /**
//     * Finds candidate reactions from the database that fill the gap
//     * @param n
//     * @return
//     */
//    public List<BiochemicalReaction> getFillingCandidates( Integer n ) {
//        List<BiochemicalReaction> candidateReactions = new ArrayList<BiochemicalReaction>();
//        InChI inchi = s.getMolecule( n );
//
//        try {
//            BiochemicalReaction[] allReactions = ReactionLoader.getInstance().getInvolvedReactions( inchi );
//            Set<BiochemicalReaction> unqiueReactions = new HashSet<BiochemicalReaction>( Arrays.asList( allReactions ) );
//
//            LOGGER.info("N reactions involving "+ String.format("%10s", inchi.toString()) + ": " + allReactions.length );
//            // test which will fit
//            for ( BiochemicalReaction biochemicalReaction : unqiueReactions ) {
//                List<InChI> compounds = biochemicalReaction.getInchiProducts();
//                compounds.addAll( biochemicalReaction.getInchiReactants() );
//
//                int count = 0;
//
//                for ( InChI compound : compounds ) {
//                    count += ( s.containsMolecule( compound ) ? 1 : 0 );
//                }
//
//                // if all molecules are already present in the stoichiometric matrix
//                // add the reaction to the network
//                if ( count == compounds.size() ) {
//                    candidateReactions.add( biochemicalReaction );
//                }
//
//                // todo check we're not adding the same reaction
//                // that is producing/consuming the molecule
//            }
//        } catch ( SQLException ex ) {
//
//            LOGGER.error( "Could not fetch reaction" , ex );
//        } catch ( CDKException ex ) {
//
//            LOGGER.error( ex.getMessage() , ex );
//        }
//
//        return candidateReactions;
//    }
//
//    public void fillGap( List l ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
//    }
}
