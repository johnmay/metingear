/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view.labels;

import com.jgoodies.forms.factories.Borders;
import javax.swing.Action;
import javax.swing.JButton;

/**
 * IconButton.java
 *
 *
 * @author johnmay
 * @date May 5, 2011
 */
public class IconButton extends JButton{

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( IconButton.class );

    public IconButton(Action a) {
        super( a );
        setBorder(Borders.EMPTY_BORDER);
        if ( getIcon() != null ) {
            setText( null );
        }
    }

}
