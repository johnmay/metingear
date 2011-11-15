
/**
 * BorderSample.java
 *
 * 2011.11.02
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.AbstractBorder;
import javax.swing.border.BevelBorder;
import org.apache.log4j.Logger;

/**
 *          BorderSample - 2011.11.02 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class BorderSample {

    private static final Logger LOGGER = Logger.getLogger(BorderSample.class);

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel outer = new JPanel();
        outer.setPreferredSize(new Dimension(400, 400));

        JTextField inner = new JTextField(10);
        inner.setBorder(new AbstractBorder() {

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
//                g.clearRect(x, y, w, h);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 10;

//                g.setColor(c.getBackground());
//                g.fillRoundRect(x, y, w, h, 20, 20);

                g.setColor(c.getBackground());
                g.clearRect(x, y, (arc / 2) + 1, (arc / 2) + 1);
                g.clearRect(x, y + c.getHeight() - arc, (arc / 2) + 1, (arc / 2) + 1);


                g.setColor(c.getBackground().darker());
                g.drawArc(x, y, arc, arc, -270, 90);
                g.drawArc(x, y + c.getHeight() - arc - 1, arc, arc, 180, 90);
                g.setColor(c.getBackground().darker().darker());
                g.drawArc(x + 1, y + 1, arc - 1, arc - 1, -270, 90);
                g.drawArc(x + 1, y + c.getHeight() - arc - 1, arc - 1, arc - 1, 180, 90);

                g.setColor(c.getBackground());
                g.fillArc(x + 2, y + 2, arc - 2, arc - 2, -270, 90);
                g.fillArc(x + 2, y + 2, arc - 2, arc - 2, -270, 90);

            }

            public Insets getBorderInsets(Component c) {
                return new Insets(2, 10, 2, 10);
            }
        });
        outer.add(inner);
        frame.add(outer);
        frame.pack();
        frame.setVisible(true);
    }
}
