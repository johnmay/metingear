/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mnb.todo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * MenuController.java
 *
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public class MenuController implements ActionListener, MouseListener {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( MenuController.class );

    public void actionPerformed( ActionEvent e ) {
        System.out.println( e );
    }

    public void mouseClicked( MouseEvent e ) {
       
        //if ( e.getComponent().getClass() == ActionLabel.class ) {
          //  ActionLabel label = (ActionLabel) e.getComponent();
         //   label.performAction( null );
     //   }
        
    }

    public void mousePressed( MouseEvent e ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void mouseReleased( MouseEvent e ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void mouseEntered( MouseEvent e ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void mouseExited( MouseEvent e ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }


}
