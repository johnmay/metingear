/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.core;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;
import uk.ac.ebi.chemet.render.ViewUtilities;

/**
 * ExpandButton.java
 *
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class ExpandButton
        extends JToggleButton {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ExpandButton.class );
    private ImageIcon open;
    private ImageIcon close;

    public ExpandButton( ExpandComponent a ) {
        super( a );
        open = ViewUtilities.getIcon( a.getValue( GeneralAction.EXPAND_BUTTON_OPEN_ICON ).toString() , "" );
        close = ViewUtilities.getIcon( a.getValue( GeneralAction.EXPAND_BUTTON_CLOSE_ICON ).toString() , "" );
        setUI(new BasicButtonUI());
        setIcon(close);
        setBorder(null);
        setBackground(null);
        setFocusable(false);
        setSelectedIcon( open );
        setText( null );
        a.setButton(this);
    }
}
