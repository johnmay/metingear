/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */package uk.ac.ebi.mnb.view;

import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * TransparentTextField.java
 *
 *
 * @author johnmay
 * @date Apr 29, 2011
 */
public class TransparentTextArea
        extends JTextArea {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( TransparentTextArea.class );

    public TransparentTextArea( String text , int rows, int columns ) {
        super( text , rows, columns );
        setLineWrap( true );
        setBackground( null );
        setBorder( null );
    }

    public TransparentTextArea( int rows, int columns ) {
        this( null , rows, columns );
    }
    
    public TransparentTextArea(String text){
        this(text, 5, text.length());
    }

    public TransparentTextArea() {
        this( "" , 5 , 10 );
    }
    
}
