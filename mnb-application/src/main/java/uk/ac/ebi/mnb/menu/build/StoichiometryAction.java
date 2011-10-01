/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.build;

import uk.ac.ebi.mnb.core.DelayedBuildAction;

/**
 * StoichiometricMatixAction.java
 *
 *
 * @author johnmay
 * @date May 13, 2011
 */
public class StoichiometryAction
        extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( StoichiometryAction.class );
    private MassChargeBalanceDialog dialog;

    public StoichiometryAction() {
        super( "MassChargeBalance" );
    }

    @Override
    public void buildComponents() {
        dialog = new MassChargeBalanceDialog();
    }

    @Override
    public void activateActions() {
        dialog.setVisible( true );
    }
}
