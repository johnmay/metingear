/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import java.util.Set;
import javax.swing.JMenuItem;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.core.reconstruction.ReconstructionContents;
import uk.ac.ebi.mnb.core.GeneralAction;

/**
 * DynamicMenuItem.java
 *
 *
 * @author johnmay
 * @date Apr 26, 2011
 */
public class DynamicMenuItem
    extends JMenuItem {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( DynamicMenuItem.class );

    public DynamicMenuItem( GeneralAction a ) {
        super( a );
    }

    // very time consuming method but is called infrequently
    public void reloadEnabled() {

        Object requiredPropertyObject = getAction().getValue( GeneralAction.PROJECT_REQUIRMENTS );
        if ( requiredPropertyObject == null ) {
            setEnabled( true );
            return;
        }

        Set<ReconstructionContents> requiredContents = ReconstructionContents.expandList( requiredPropertyObject.toString() );

        if ( ReconstructionManager.getInstance().getActiveReconstruction() == null ) {
            setEnabled( false );
            return;
        }

        Set<ReconstructionContents> projectContentsList = ReconstructionManager.getInstance().getActiveReconstruction().getContents();

        int requiredCount = requiredContents.size();
        int foundCount = 0;

        for ( ReconstructionContents requirement : requiredContents ) {
             if ( projectContentsList.contains( requirement ) ) {
                foundCount++;
            }
        }

        setEnabled( requiredCount == foundCount );

    }
}
