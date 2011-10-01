/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.view.old;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import javax.swing.JPanel;
import uk.ac.ebi.mnb.view.labels.BoldLabel;
import uk.ac.ebi.metabolomes.descriptor.observation.JobParamType;


/**
 * JobPanelExpander.java
 *
 *
 * @author johnmay
 * @date May 10, 2011
 */
public class JobPanelExpander
        extends JPanel {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( JobPanelExpander.class );

    protected JobPanelExpander( ObservationJobPanel jobPanel ) {
        FormLayout layout = new FormLayout( "right:8dlu, 2dlu, left:70dlu , 4dlu, left:150dlu" ,
                                            "p" );
        setLayout( layout );
        setBackground( Color.WHITE );
        CellConstraints cc = new CellConstraints();

        ExpandButton expandAbleButton = new ExpandButton( new ExpandComponent( jobPanel ) );

        add( expandAbleButton , cc.xy( 1 , 1 ) );
        add( new BoldLabel( jobPanel.getParameters().get( JobParamType.JOBID ).toString() ) , cc.xy( 3 , 1 ) );
        add( new BoldLabel( jobPanel.getParameters().get( JobParamType.DATE ).toString() ) , cc.xy( 5 , 1 ) );

        if ( jobPanel.getRemoveHighlight() ) {
            expandAbleButton.setClose();
            jobPanel.setVisible( false );
        } else {
            expandAbleButton.setOpen();
            jobPanel.setVisible( true );
        }

    }
}
