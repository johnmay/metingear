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
package uk.ac.ebi.mnb.dialog.table;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.awt.AWTUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import uk.ac.ebi.metabonater.components.theme.MRoundButton;
import uk.ac.ebi.mnb.core.CloseDialogAction;
import uk.ac.ebi.mnb.interfaces.Theme;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.view.DialogPanel;
import uk.ac.ebi.mnb.view.ViewUtils;

/**
 * @name    PopupDialog - 2011.10.07 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class PopupDialog extends JDialog {

    private static final Logger LOGGER = Logger.getLogger(PopupDialog.class);
    private JPanel panel = new DialogPanel();
    private Area callout = getCalloutShape();
    private JPanel background = new JPanel() {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Theme t = Settings.getInstance().getTheme();
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(t.getDialogBackground());
            g2.fill(callout);
            g2.setColor(ViewUtils.shade(t.getDialogBackground(), -0.2f));
            g2.draw(callout);
        }
    };

    // move to view utils
    private static Color getMixedColor(Color c1, float pct1, Color c2, float pct2) {
        float[] clr1 = c1.getComponents(null);
        float[] clr2 = c2.getComponents(null);
        for (int i = 0; i < clr1.length; i++) {
            clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
        }
        return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
    }

    public PopupDialog(JFrame frame) {
        super(frame, ModalityType.APPLICATION_MODAL);
        //  setUndecorated(true);
        setUndecorated(true);
        add(background);
        AWTUtilities.setWindowOpaque(this, false);
        panel.setOpaque(false);
        background.setLayout(new FormLayout("right:13dlu, p, 13dlu", "bottom:13dlu, p, 13dlu"));
        CellConstraints cc = new CellConstraints();
        Icon close = ViewUtils.getIcon("images/cutout/close_12x12.png");
        JButton closeButton = new MRoundButton(close, new CloseDialogAction(this, false));
        background.add(closeButton, cc.xy(1, 1));
        background.setOpaque(true);
        background.add(panel, cc.xy(2, 2));
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                callout = getCalloutShape();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                setLocation(openingLocation.x - (getWidth() / 2), openingLocation.y - getHeight() - 5);
                callout = getCalloutShape();
                repaint();
            }
        });
    }

    @Override
    public void repaint() {
        super.repaint();
    }
    private Point openingLocation;

    /**
     * Sets the pop-up location based on mouse position
     */
    public void setOpenLocation() {
        openingLocation = MouseInfo.getPointerInfo().getLocation();
        setLocation(openingLocation.x - (getWidth() / 2), openingLocation.y - getHeight() - 5);
    }

    public JPanel getPanel() {
        return panel;
    }

    public Area getCalloutShape() {
        RoundRectangle2D rect = new RoundRectangle2D.Float(11, 11, getPreferredSize().width - 22, getPreferredSize().height - 22, 15, 15);

        int center = getPreferredSize().width / 2;

        int[] x = new int[]{
            center,
            center - 10,
            center + 10
        };
        int[] y = new int[]{
            getPreferredSize().height - 2,
            getPreferredSize().height - 12,
            getPreferredSize().height - 12};
        Polygon p = new Polygon(x, y, 3);

        Area composite = new Area(rect);
        composite.add(new Area(p));

        return composite;

    }
}
