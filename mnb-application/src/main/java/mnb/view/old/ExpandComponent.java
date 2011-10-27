/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.view.old;

import java.awt.event.ActionEvent;
import uk.ac.ebi.mnb.core.GeneralAction;

/**
 * ExpandComponent.java
 *
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class ExpandComponent
        extends GeneralAction {

    public ExpandComponent(String command) {
        super(command);
    }

    

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
//
//    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ExpandComponent.class );
//    private ExpandButton button;
//    private ObservationJobPanel component;
//
//    public ExpandComponent( ObservationJobPanel panel ) {
//        super( "DropDownButton" );
//        this.component = panel;
//        if ( panel.getRemoveHighlight() == false ) {
//            panel.build();
//        }
//    }
//
//    public void setButton( ExpandButton button ) {
//        this.button = button;
//    }
//
//    @Override
//    public void actionPerformed( ActionEvent e ) {
//
//        // build if it isn't built yet
//        if ( component.isBuilt() == Boolean.FALSE ) {
//            component.build();
//        }
//
//        component.setVisible( !component.isVisible() );
//
//        if ( button != null ) {
//            if ( component.isVisible() ) {
//                button.setOpen();
//            } else {
//                button.setClose();
//            }
//        }
//    }
}
