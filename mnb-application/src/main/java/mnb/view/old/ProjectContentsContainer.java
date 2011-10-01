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
package mnb.view.old;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.labels.BoldLabel;
import uk.ac.ebi.mnb.view.labels.Label;

/**
 * ProjectContentsContainer.java
 *
 *
 * @author johnmay
 * @date May 23, 2011
 */
public class ProjectContentsContainer
        extends JComponent {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ProjectContentsContainer.class );
    private final Label countLabel;
    // bold label with icon
    private final BoldLabel nameLabel;

    public ProjectContentsContainer( String name ,
                                     ImageIcon icon ,
                                     int count ) {
        this.countLabel = new Label( "(" + count + ")" );
        this.nameLabel = new BoldLabel( name, icon );
        setLayout( new BoxLayout( this , BoxLayout.LINE_AXIS ) );
        countLabel.setFont( ViewUtils.HELVATICA_NEUE_PLAIN_11 );
        add( nameLabel );
        add(Box.createRigidArea(new Dimension(5,0)));
        add( countLabel);
    }

    public ProjectContentsContainer( String name , int count ) {
        this( name , null , count );
    }

    /**
     * Sets the number on the contents count label
     * @param count An integer of the new number of elements help in the container
     */
    public void setContentsCount( int count ) {
        countLabel.setText( "(" + count + ")" );
    }



}
