/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.metabolomes.gap.filling;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;

import uk.ac.ebi.metabolomes.core.reaction.matrix.InChIStoichiometricMatrix;
import uk.ac.ebi.metabolomes.identifier.InChI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import uk.ac.ebi.metabolomes.core.reaction.BiochemicalReaction;
import uk.ac.ebi.warehouse.exceptions.UnknownStructureException;
import uk.ac.ebi.warehouse.util.ReactionLoader;

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

    private static final Logger LOGGER = Logger.getLogger( IterativeExpansion.class );
    private InChIStoichiometricMatrix s;

    protected IterativeExpansion( InChIStoichiometricMatrix s ) {
        this.s = s;
    }

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
