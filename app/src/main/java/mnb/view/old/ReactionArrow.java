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
package mnb.view.old;

import uk.ac.ebi.caf.component.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * ReactionArrow.java
 * A arrow for a reaction diagram. This is simply and arrow painted on a JComponent
 *
 * @author johnmay
 * @date May 19, 2011
 */
public class ReactionArrow
        extends JComponent {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ReactionArrow.class );
    private Dimension dimensions;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int gap;
    private Polygon arrowHead;

    public ReactionArrow( Dimension d ,
                          int gap ) {
        dimensions = d;
        x1 = 0 + gap;
        y1 = d.height / 2;
        x2 = d.width - gap;
        y2 = d.height / 2;
        setPreferredSize( d );
        arrowHead = new Polygon( new int[]{ x2 , x2 - gap , x2 - gap } ,
                                 new int[]{ y2 , y2 + ( gap / 2 ) , y2 - ( gap / 2 ) } ,
                                 3 );
        setBackground(ThemeManager.getInstance().getTheme().getBackground());
    }

    public ReactionArrow( Dimension d ) {
        this( d , d.width / 10 );
    }

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        g.setColor( Color.GRAY );
        g.drawLine( x1 , y1 , x2 , y2 );
        g.fillPolygon( arrowHead );
    }
}
