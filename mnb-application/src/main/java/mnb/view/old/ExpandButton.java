/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.view.old;

import javax.swing.ImageIcon;
import uk.ac.ebi.visualisation.ViewUtils;
import uk.ac.ebi.mnb.core.GeneralAction;
import uk.ac.ebi.mnb.view.labels.IconButton;

/**
 * ExpandButton.java
 *
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class ExpandButton
        extends IconButton {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ExpandButton.class );
    private ImageIcon open;
    private ImageIcon close;

    public ExpandButton( ExpandComponent a ) {
        super( a );
        open = ViewUtils.getIcon( a.getValue( GeneralAction.EXPAND_BUTTON_OPEN_ICON ).toString() , "" );
        close = ViewUtils.getIcon( a.getValue( GeneralAction.EXPAND_BUTTON_CLOSE_ICON ).toString() , "" );
        setIcon( open );
        setText( null );
      //  a.setButton( this );
    }

    public void setOpen() {
        setIcon( open );
    }

    public void setClose() {
        setIcon( close );
    }
}
