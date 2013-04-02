/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
