/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view.labels;

import com.jgoodies.forms.factories.Borders;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import uk.ac.ebi.mnb.view.ViewUtils;


/**
 * ActionLabel.java
 *
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class ActionLabel
  extends JButton {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(ActionLabel.class);


    public ActionLabel(String text,
                       AbstractAction action) {

        super(action);
        setBorder(Borders.EMPTY_BORDER);
        setText(text);
        setToolTipText(text);
        setFont(ViewUtils.DEFAULT_LINK_FONT);
        addMouseListener(new FontHover());
    }


    private class FontHover extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            setFont(ViewUtils.DEFAULT_LINK_HOVER_FONT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }


        @Override
        public void mouseExited(MouseEvent e) {
            setFont(ViewUtils.DEFAULT_LINK_FONT);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }


    }


}

