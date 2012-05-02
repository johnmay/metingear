/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view.labels;

import com.jgoodies.forms.factories.Borders;
import uk.ac.ebi.caf.component.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        setFont(ThemeManager.getInstance().getTheme().getLinkFont());
        setForeground(ThemeManager.getInstance().getTheme().getForeground());
        addMouseListener(new FontHover());
    }

    public ActionLabel(Icon icon, AbstractAction action) {
        super(icon);
        setBorder(Borders.EMPTY_BORDER);
        setFont(ThemeManager.getInstance().getTheme().getLinkFont());
        setForeground(ThemeManager.getInstance().getTheme().getForeground());
        addMouseListener(new FontHover());
    }

    private class FontHover extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            setForeground(ThemeManager.getInstance().getTheme().getEmphasisedForeground());
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setForeground(ThemeManager.getInstance().getTheme().getForeground());
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
