/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.view.old;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.labels.BoldLabel;
import uk.ac.ebi.mnb.view.labels.Label;
import uk.ac.ebi.metabolomes.descriptor.observation.AbstractObservation;
import uk.ac.ebi.metabolomes.descriptor.observation.ObservationCollection;
import uk.ac.ebi.metabolomes.descriptor.observation.JobParameters;

/**
 * ObservationsPanel.java
 *
 *
 * @author johnmay
 * @date Apr 29, 2011
 */
public class ObservationsPanel
        extends JPanel {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ObservationsPanel.class );
    private static final long serialVersionUID = 1725701519795079585L;
    private ObservationCollection observations = null;
    private int codedWidth;

    public ObservationsPanel( ) {
        codedWidth = 500;
        setLayout( new BoxLayout( this , BoxLayout.PAGE_AXIS ) );
        setBackground( Color.WHITE );

    }

    /**
     * @return the observations
     */
    public ObservationCollection getObservations() {
        return observations;
    }

    /**
     * @param observations the observations to set
     */
    public void setObservations( ObservationCollection observations ) {
        this.observations = observations;
        updateComponents( true );
    }

    public void updateComponents() {
        updateComponents( false );
    }

    /**
     * Updates the display
     */
    public void updateComponents( boolean removeHighlight ) {

        if ( observations == null || observations.size() == 0 ) {
            return;
        }

        // remove all current components
        removeAll();

        // get the parameters so we can create a new sub panel for each
        // of the seperate jobs
        List<JobParameters> parameters = observations.getParametersList();


        for ( JobParameters jobParameters : parameters ) {
            ObservationJobPanel jobPanel = new ObservationJobPanel( jobParameters ,
                                                                    observations.getByParameters( jobParameters ) ,
                                                                    removeHighlight );
            JobPanelExpander panelExpander = new JobPanelExpander( jobPanel );
            add( panelExpander );
            add( jobPanel );
        }

        revalidate();
    }

    @Override
    public void setSize( Dimension d ) {
        super.setSize( d );
    }
}
