/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.view.old;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import uk.ac.ebi.metabolomes.descriptor.observation.AbstractObservation;

/**
 * ObservationRenderer.java
 *
 *
 * @author johnmay
 * @date May 4, 2011
 */
public class ObservationRenderer extends JComponent {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ObservationRenderer.class );
    private AbstractObservation observation;
    private BufferedImage image;

    public ObservationRenderer( AbstractObservation observation ,
                                Dimension dimension ) {
        this.observation = observation;
        setPreferredSize( dimension );
        image = observation.getObservationImage( dimension.width , dimension.height );
        setToolTipText( observation.getObservationDescription() );
    }

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        Graphics2D g2 = ( Graphics2D ) g;
        g2.drawImage( image , 0 , 0 , null );
    }
}
