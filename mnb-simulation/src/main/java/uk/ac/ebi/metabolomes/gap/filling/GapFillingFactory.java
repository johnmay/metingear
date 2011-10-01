/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.metabolomes.gap.filling;

import uk.ac.ebi.metabolomes.core.reaction.matrix.InChIStoichiometricMatrix;
import uk.ac.ebi.metabolomes.core.reaction.matrix.StoichiometricMatrix;

/**
 * GapFillingFactory.java – Jul 6, 2011
 *
 * @author johnmay <johnmay@ebi.ac.uk>
 */
public class GapFillingFactory
{
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( GapFillingFactory.class );

    public static SingleExpansion getSingleExpansion( StoichiometricMatrix s )
    {
        if ( s instanceof InChIStoichiometricMatrix )
        {
            return new SingleExpansion( (InChIStoichiometricMatrix) s );
        }

        logger.error( "Cannot build SingleExpansion gap filling class in factory – this method is only available to InChIStoichiometricMatricies" );

        return null;
    }
}
