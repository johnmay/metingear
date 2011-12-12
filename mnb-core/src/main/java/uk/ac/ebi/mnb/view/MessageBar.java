/**
 * MessageManager.java
 *
 * 2011.09.30
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.view;

import uk.ac.ebi.ui.component.factory.LabelFactory;
import uk.ac.ebi.visualisation.ViewUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.Stack;

import uk.ac.ebi.mnb.core.*;
import uk.ac.ebi.mnb.view.labels.IconButton;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import uk.ac.ebi.mnb.interfaces.Message;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.visualisation.ColorUtilities;

/**
 *          MessageManager – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MessageBar extends JPanel implements MessageManager {

    private static final Logger LOGGER = Logger.getLogger(MessageBar.class);
    private final ImageIcon warningIcon = ViewUtils.getIcon("images/cutout/warning_16x16.png",
                                                            "Warning");
    private final ImageIcon errorIcon =
                            ViewUtils.getIcon("images/cutout/error_16x16.png", "Error");
    private final Color WARN_LOW = new Color(240, 240, 30);
    private final Color WARN_HIGH = ColorUtilities.shade(WARN_LOW, 0.3f);
    private final Color ERROR_LOW = new Color(230, 40, 60);
    private final Color ERROR_HIGH = ColorUtilities.shade(ERROR_LOW, 0.3f);
    private final Paint ERROR_PAINT;
    private final Paint WARN_PAINT;
    private Paint paint;
    private JLabel iconLabel = LabelFactory.newLabel("");
    private JLabel label = LabelFactory.newLabel("");
    private Stack<Message> stack = new Stack();

    public MessageBar() {
        super(
                new FormLayout("1dlu, min, 1dlu, min, 4dlu, p:grow, 1dlu", "0dlu, center:p, 0dlu"));
        CellConstraints cc = new CellConstraints();
        label.setForeground(Color.BLACK);
        this.add(new IconButton(new HideMessage()),
                 cc.xy(2, 2, CellConstraints.CENTER,
                       CellConstraints.CENTER));
        this.add(iconLabel, cc.xy(4, 2, CellConstraints.CENTER, CellConstraints.CENTER));
        this.add(label, cc.xy(6, 2, CellConstraints.CENTER, CellConstraints.CENTER));


        this.addMouseListener(new CopyPopup());
        // set the paints
        ERROR_PAINT = new GradientPaint(0, 0, ERROR_LOW, 0,
                                        getPreferredSize().height / 2, ERROR_HIGH, true);
        WARN_PAINT = new GradientPaint(0, 0, WARN_LOW, 0,
                                       getPreferredSize().height / 2, WARN_HIGH, true);

        this.setVisible(false);
    }

    public void addMessage(Message message) {


        if (message instanceof ErrorMessage) {
            LOGGER.error("Displaying error message: " + message.getMessage());
        } else if (message instanceof WarningMessage) {
            LOGGER.warn("Displaying warning message: " + message.getMessage());
        }

        stack.push(message);
        update();
        setVisible(true);
    }

    public boolean update() {

        if (!stack.isEmpty()) {

            label.setPreferredSize(new Dimension(getParent().getSize().width - 75, 18));
            label.setText(stack.peek().getMessage());

            if (stack.peek() instanceof ErrorMessage) {
                paint = ERROR_PAINT;
                iconLabel.setIcon(errorIcon);
            } else if (stack.peek() instanceof WarningMessage) {
                paint = WARN_PAINT;
                iconLabel.setIcon(warningIcon);
            }

        }
        repaint();
        revalidate();
        return true;
    }

    /**
     * Copies the top message to the clipboard
     */
    private class CopyPopup extends MouseAdapter {

        private JPopupMenu menu = new JPopupMenu("Messages");

        public CopyPopup() {
            menu.add(new JMenuItem(new GeneralAction("CopyMessage") {

                public void actionPerformed(ActionEvent e) {
                    if (!stack.isEmpty()) {
                        ViewUtils.setClipboard(stack.peek().getMessage());
                    }
                    menu.setVisible(false);
                }
            }));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                Point mouse = MouseInfo.getPointerInfo().getLocation();
                menu.setLocation(mouse.x + 20, mouse.y - 20);
                menu.setVisible(!menu.isVisible());
            } else {
                menu.setVisible(false);
            }
        }
    }

    /**
     * Action to hide the panel
     */
    private class HideMessage extends GeneralAction {

        public HideMessage() {
            super("HideMessage");
        }

        public void actionPerformed(ActionEvent e) {
            stack.pop();
            // when empty hide it
            if (stack.isEmpty()) {
                setVisible(false);
            } else {
                update();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
