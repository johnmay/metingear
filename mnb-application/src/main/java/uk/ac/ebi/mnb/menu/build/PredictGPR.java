/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.build;

import uk.ac.ebi.mnb.core.DelayedBuildAction;

/**
 * PredictGPR.java
 * Class builds enzyme annotations for gene products
 * provided the SwissProt Homology has been run
 *
 * This action will launch a dialog with options for
 * assigning the enzyme based on classification
 *
 * ProjectRequires=EnzymeHomology,ProteinProducts
 * @author johnmay
 * @date Apr 29, 2011
 */
public class PredictGPR
        extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( PredictGPR.class );
    private PredictGPRDialog dialog;

    public PredictGPR( ) {
        super( "EnzymeAnnotation" );
    }

    @Override
    public void buildComponents() {
        dialog = new PredictGPRDialog();
    }

    @Override
    public void activateActions() {
        dialog.setVisible( true );
    }
}
