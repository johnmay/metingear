/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
        setText(text);
        setToolTipText(text);
        setFont(ThemeManager.getInstance().getTheme().getLinkFont());
        setForeground(ThemeManager.getInstance().getTheme().getForeground());

        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorder(Borders.EMPTY_BORDER);
        setMargin(new Insets(0, 0, 0, 0));

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
