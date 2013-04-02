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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.metabolomes.gap.filling;



import java.util.ArrayList;
import java.util.List;

/**
 * IterativeExpansion.java – 2011-07-06
 * The IterativeExpansion class fills gaps in metabolic networks but adding reactions that 'fit'
 * with a dead-end metabolite. Reactions 'fit' if they consume/produce the dead-end metabolite
 * and produce/consume another metabolite in the network. The second metabolite may also be a
 * dead-end. The method is called iterative because you could keep adding reaction to close gaps
 * until convergence is reached and there are either no more gaps or no more addable reactions.
 *
 * The object can be created in the GapFillingFactory class.
 * <pre>
 *
 * </pre>
 *
 * @author johnmay <johnmay@ebi.ac.uk>
 */
public class IterativeExpansion
        implements GapFillingMethod {

//    private static final Logger LOGGER = Logger.getLogger( IterativeExpansion.class );
//    private InChIStoichiometricMatrix s;
//
//    protected IterativeExpansion( InChIStoichiometricMatrix s ) {
//        this.s = s;
//    }

    /**s
     * @inheritDoc
     */
    public List getFillingCandidates( Integer n ) {

    


        return new ArrayList();
    }

    /**
     * @inheritDoc
     */
    public void fillGap( List l ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

}
