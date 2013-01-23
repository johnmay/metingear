/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.core;

import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.caf.utility.ResourceUtility;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

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
        open = ResourceUtility.getIcon(a.getValue(GeneralAction.EXPAND_BUTTON_OPEN_ICON).toString());
        close = ResourceUtility.getIcon( a.getValue( GeneralAction.EXPAND_BUTTON_CLOSE_ICON ).toString());
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
