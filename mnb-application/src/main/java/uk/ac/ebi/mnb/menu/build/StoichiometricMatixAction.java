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
public class StoichiometricMatixAction
    extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( StoichiometricMatixAction.class );

    private StoichiometricMatixActionDialog dialog;

    public StoichiometricMatixAction() {
        super("BuildStoichiometricMatrix");
    }

    @Override
    public void buildComponents() {
        dialog = new StoichiometricMatixActionDialog();
    }

    @Override
    public void activateActions() {
        dialog.setVisible(true);
    }
}
