/**
 * PopupDialog.java
 *
 * 2011.10.07
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
package uk.ac.ebi.mnb.dialog.popup;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import uk.ac.ebi.mnb.core.CloseDialogAction;
import uk.ac.ebi.caf.component.theme.Theme;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.chemet.render.ViewUtilities;

import javax.swing.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.awt.AWTUtilities;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import uk.ac.ebi.caf.action.ActionProperties;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.utility.ColorUtility;


/**
 * @name    PopupDialog - 2011.10.07 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class PopupDialog extends JDialog {

    private static final Logger LOGGER = Logger.getLogger(PopupDialog.class);

    private JPanel panel = PanelFactory.createDialogPanel();

    private BufferedImage bgImage;

    private Point mouse;

    private int offset;

    private JPanel background = new JPanel() {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bgImage, 0, 0, null);
        }
    };


    public PopupDialog(JDialog dialog) {
        this(dialog, ModalityType.APPLICATION_MODAL);
    }


    public PopupDialog(JDialog dialog, ModalityType modality) {
        super(dialog, modality);
        setup();
    }


    public PopupDialog(JFrame frame) {
        this(frame, ModalityType.APPLICATION_MODAL);
    }


    public PopupDialog(JFrame frame, ModalityType modality) {
        super(frame, modality);
        setup();
    }


    private void setup() {
        setUndecorated(true);
        add(background);
        AWTUtilities.setWindowOpaque(this, false);
        //getRootPane().setOpaque(false);
        //getRootPane().setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));
        panel.setOpaque(false);
        background.setLayout(new FormLayout("8px, 16px, p, 16px, 8px", "8px, 16px, p, 16px, 8px"));
        CellConstraints cc = new CellConstraints();
        Icon close = ViewUtilities.getIcon("images/cutout/close_whitebg_16x16.png");

        Action closeAction = new CloseDialogAction(this, false);
        JButton closeButton = ButtonFactory.newCleanButton(close, closeAction);
        closeButton.registerKeyboardAction(closeButton.getAction(),
                                           KeyStroke.getKeyStroke(
                ActionProperties.getInstance(getClass()).getProperty("CloseDialog.Action." + Action.ACCELERATOR_KEY)),
                                           JComponent.WHEN_IN_FOCUSED_WINDOW);

        background.add(closeButton, cc.xy(2, 2));
        background.setOpaque(true);
        background.add(panel, cc.xy(3, 3));
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                bgImage = getBackgroundImage();
            }


            @Override
            public void componentResized(ComponentEvent e) {
                if (mouse != null) {
                    setLocation(mouse.x - (getWidth() / 2), mouse.y - getHeight() + 7 - offset);
                }
                bgImage = getBackgroundImage();
                repaint();
            }
        });
    }


    /**
     * Sets the pop-up location based on mouse position. The tip of the callout will be at the mouse point with no offset
     */
    public void setOnMouse() {
        setOnMouse(offset);
    }


    /**
     * Sets the location on the mouse with a specified offset. This will place the dialog tip at the offset above the
     * mouse
     * @param offset
     */
    public void setOnMouse(int offset) {
        mouse = MouseInfo.getPointerInfo().getLocation();
        this.offset = offset;
        setLocation(mouse.x - (getWidth() / 2), mouse.y - getHeight() + 7 - offset);
    }


    public JPanel getPanel() {
        return panel;
    }
    // rendering maps

    private Map<Dimension, BufferedImage> backgroundCache = new HashMap();

    private Map<Integer, Stroke> strokeMap = new HashMap<Integer, Stroke>();

    private Map<Float, Color> colorMap = new HashMap<Float, Color>();

    private Color grey = new Color(0, 0, 0, 50);


    public BufferedImage getBackgroundImage() {

        Theme theme = Settings.getInstance().getTheme();
        Dimension size = getPreferredSize();

        if (backgroundCache.containsKey(size)) {
            return backgroundCache.get(size);
        }

        if (backgroundCache.size() > 200) {
            LOGGER.warn("Many images are being stored the background caching map has now be reinstantiated" + " considering making your popup discrete sizes prevent constant background re-drawing");
            backgroundCache = new HashMap();
        }

        LOGGER.info("Drawing new background image for size: " + size);

        BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        Shape callout = getCalloutShape();
        // draws the border
        int sw = 5 * 2;
        for (int i = sw; i >= 2; i -= 2) {
            float pct = (float) (sw - i) / (sw - 1);

            Color color = getShadowColor(pct);
            Stroke stroke = getShadowStroke(i);

            g2.setColor(color);
            g2.setStroke(stroke);
            g2.draw(callout);
        }

        g2.setColor(theme.getDialogBackground());
        g2.fill(callout);
        g2.dispose();

        backgroundCache.put(size, img);

        return img;
    }


    public Stroke getShadowStroke(int i) {
        if (strokeMap.containsKey(i)) {
            return strokeMap.get(i);
        }
        strokeMap.put(i, new BasicStroke(i));
        return strokeMap.get(i);
    }


    public Color getShadowColor(float pct) {
        if (colorMap.containsKey(pct)) {
            return colorMap.get(pct);
        }
        colorMap.put(pct, ColorUtility.getMixedColor(grey, pct,
                                                     ViewUtilities.CLEAR_COLOUR, 1.0f - pct));
        return colorMap.get(pct);
    }


    public Area getCalloutShape() {

        RoundRectangle2D rect = new RoundRectangle2D.Float(16, 16, getPreferredSize().width - 32, getPreferredSize().height - 32, 16, 16);

        // 16 for corners as icon is 16 rounded

        int center = getPreferredSize().width / 2;

        int[] x = new int[]{
            center,
            center - 25,
            center + 25
        };
        int[] y = new int[]{
            getPreferredSize().height - 7,
            getPreferredSize().height - 32,
            getPreferredSize().height - 32};
        Polygon p = new Polygon(x, y, 3);

        Area composite = new Area(rect);
        composite.add(new Area(p));

        return composite;

    }
}
