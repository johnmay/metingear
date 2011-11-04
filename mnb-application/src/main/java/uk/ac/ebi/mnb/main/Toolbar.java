/**
 * Toolbar.java
 *
 * 2011.11.03
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
package uk.ac.ebi.mnb.main;

import com.explodingpixels.macwidgets.LabeledComponentGroup;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.explodingpixels.macwidgets.plaf.EmphasizedLabelUI;
import com.explodingpixels.painter.Painter;
import com.explodingpixels.widgets.WindowUtils;
import com.jgoodies.forms.factories.Borders;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.view.labels.EmphasizedLabel;

/**
 *          Toolbar - 2011.11.03 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class Toolbar extends TriAreaComponent {

    private static final Logger LOGGER = Logger.getLogger(Toolbar.class);

    public Toolbar() {
        this.setBackgroundPainter(new Painter<Component>() {

            private Color ACTIVE_TOP_GRADIENT_COLOR = UIManager.getColor("Panel.background");
            private Color ACTIVE_BOTTOM_GRADIENT_COLOR = ACTIVE_TOP_GRADIENT_COLOR.darker();
            private Color INACTIVE_TOP_GRADIENT_COLOR = new Color(0xe4e4e4);
            private Color INACTIVE_BOTTOM_GRADIENT_COLOR = new Color(0xd1d1d1);

            public void paint(Graphics2D graphics2D, Component component, int width, int height) {
                boolean containedInActiveWindow = WindowUtils.isParentWindowFocused(component);

                Color topColor = containedInActiveWindow
                                 ? ACTIVE_TOP_GRADIENT_COLOR : INACTIVE_TOP_GRADIENT_COLOR;
                Color bottomColor = containedInActiveWindow
                                    ? ACTIVE_BOTTOM_GRADIENT_COLOR : INACTIVE_BOTTOM_GRADIENT_COLOR;

                GradientPaint paint = new GradientPaint(0, 1, topColor, 0, height, bottomColor);
                graphics2D.setPaint(paint);
                graphics2D.fillRect(0, 0, width, height);
            }
        });
        this.getComponent().setBorder(Borders.createEmptyBorder("3dlu, 4dlu, 3dlu, 4dlu"));
        WindowUtils.installJComponentRepainterOnWindowFocusChanged(getComponent());
    }

    public void addComponentToRight(JLabel label, JComponent... components) {
        addComponentToRight(new LabeledComponentGroup(label, components).getComponent());
    }

    public void addComponentToRight(String text, JComponent... components) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(11.0f));
        label.setUI(new EmphasizedLabelUI(UIManager.getColor("Label.foreground"),
                                          UIManager.getColor("Label.foreground").darker(),
                                          UIManager.getColor("Label.foreground").darker().darker()));
        addComponentToRight(new LabeledComponentGroup(label, components).getComponent());
    }
}
