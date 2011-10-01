/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.view.old;

import java.awt.event.ActionEvent;
import java.lang.annotation.Annotation;
import javax.swing.AbstractAction;
import uk.ac.ebi.metabolomes.descriptor.observation.ObservationCollection;

/**
 * HighLightObservations.java
 *
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class HighLightObservations
        extends AbstractAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( HighLightObservations.class );
    private Annotation annotation;

    public HighLightObservations( Annotation annotation ) {
        this.annotation = annotation;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {

        ObservationCollection observations = null;//annotation.getEvidence();
        if ( observations != null ) {
//            for ( Observation obs : observations ) {
//                obs.toggleHighlight( );
//            }
        }
    }
}
