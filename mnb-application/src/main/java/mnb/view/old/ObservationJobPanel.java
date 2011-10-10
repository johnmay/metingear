/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.view.old;

import mnb.view.old.ObservationRenderer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JPanel;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.labels.MLabel;
import uk.ac.ebi.metabolomes.descriptor.observation.AbstractObservation;
import uk.ac.ebi.metabolomes.descriptor.observation.JobParameters;

/**
 * ObservationJobPanel.java
 *
 *
 * @author johnmay
 * @date May 10, 2011
 */
public class ObservationJobPanel
        extends JPanel {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ObservationJobPanel.class );
    private JobParameters jobParameters;
    private List<AbstractObservation> observations;
    private Boolean built = Boolean.FALSE;
    private Boolean removeHighlight;

    public ObservationJobPanel( JobParameters jobParameters ,
                                List<AbstractObservation> observations ,
                                boolean removeHighlight ) {

        this.jobParameters = jobParameters;
        this.observations = observations;
        this.removeHighlight = removeHighlight;
        setBackground( Color.WHITE );


    }

    public Boolean isBuilt() {
        return built;
    }

    public void build() {
        int nrows = this.observations.size();

        FormLayout layout = new FormLayout( "right:80dlu, 4dlu, left:p" ,
                                            ViewUtils.goodiesFormHelper( nrows , 2 , false ) );
        CellConstraints cc = new CellConstraints();
        setLayout( layout );
        setBackground( null );

        for ( int i = 0; i < nrows; i++ ) {

            AbstractObservation observation = observations.get( i );

            if ( removeHighlight && observation.isHighlighted() ) {
                observation.toggleHighlight();
            }

            ObservationRenderer observationRenderer = new ObservationRenderer( observation , new Dimension( 800 , 12 ) );
            int ccy = ( i == 0 ) ? 1 : i * 2 + 1;
            MLabel label = new MLabel( observation.getObservationName() );
            label.setToolTipText( observation.getObservationName() );
            add( label , cc.xy( 1 , ccy ) );
            add( observationRenderer , cc.xy( 3 , ccy ) );
        }
        built = true;
    }

    public JobParameters getParameters() {
        return jobParameters;
    }

    public Boolean getRemoveHighlight() {
        return removeHighlight;
    }
}
