/*
 *     This file is part of Metabolic Network Builder
 * 
 *     Metabolic Network Builder is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.menu;

import javax.swing.ButtonModel;
import javax.swing.JMenu;
import uk.ac.ebi.visualisation.ViewUtils;

/**
 * ClearMenu.java – MetabolicDevelopmentKit – Jun 3, 2011
 * 
 * @author johnmay <johnmay@ebi.ac.uk, john.wilkinsonmay@gmail.com>
 */
class ClearMenu
        extends JMenu {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ClearMenu.class );

    public ClearMenu( String name ) {
        super( name );
        setBackground( ViewUtils.CLEAR_COLOUR );
        setOpaque( false );
    }

    @Override
    protected void fireStateChanged() {
        ButtonModel m = getModel();
        if ( m.isPressed() && m.isArmed() ) {
            setOpaque( true );
        } else if ( m.isSelected() ) {
            setOpaque( true );
        } else if ( isRolloverEnabled() && m.isRollover() ) {
            setOpaque( true );
        } else {
            setOpaque( false );
        }
        super.fireStateChanged();
    }
;
}
