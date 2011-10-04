/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view.labels;

import com.jgoodies.forms.factories.Borders;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import uk.ac.ebi.mnb.settings.Settings;


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
        setFont(Settings.getInstance().getTheme().getLinkFont());
        setForeground(Settings.getInstance().getTheme().getForeground());
        addMouseListener(new FontHover());
    }


    private class FontHover extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            setForeground(Settings.getInstance().getTheme().getEmphasisedForeground());
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }


        @Override
        public void mouseExited(MouseEvent e) {
            setForeground(Settings.getInstance().getTheme().getForeground());
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }


    }


}

