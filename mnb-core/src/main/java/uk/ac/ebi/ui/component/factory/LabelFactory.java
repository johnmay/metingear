/**
 * LabelFactory.java
 *
 * 2011.10.08
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
package uk.ac.ebi.ui.component.factory;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.Theme;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.visualisation.ColorUtilities;
import uk.ac.ebi.visualisation.VerticalLabelUI;
import uk.ac.ebi.visualisation.ViewUtils;

/**
 * @name    LabelFactory - 2011.10.08 <br>
 *          Class to handle label creation
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class LabelFactory {

    private static final Logger LOGGER = Logger.getLogger(LabelFactory.class);
    private static Theme theme = Settings.getInstance().getTheme();
    public static JLabel empty = LabelFactory.newLabel("");

    public static JLabel emptyLabel() {
        return empty;
    }

    public enum Size {

        SMALL(11.0f),
        NORMAL(12.0f),
        LARGE(14.0f),
        HUGE(16.0f);
        private final float size;

        private Size(float size) {
            this.size = size;
        }
    };

    public static JLabel newLabel(String text) {
        return newLabel(text, Size.NORMAL);
    }

    public static JLabel newLabel(String text, Size size) {

        JLabel label = new JLabel(text);
        label.setForeground(theme.getForeground());
        label.setFont(theme.getBodyFont().deriveFont(size.size));

        return label;

    }

    /**
     * Wraps the text in {@code <HTML>} tags
     * @param text
     * @return
     */
    public static JLabel newHTMLLabel(String text) {
        return newLabel(ViewUtils.htmlWrapper(text));
    }

    /**
     * Returns a right aligned label
     * @param text
     * @return
     */
    public static JLabel newFormLabel(String text) {
        JLabel label = newLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setForeground(ColorUtilities.shade(label.getForeground(), 0.2f));
        return label;
    }

    /**
     * Returns a new form label (right aligned) with the option of adding tool tip text
     * @param text
     * @param tooltip
     * @return
     */
    public static JLabel newFormLabel(String text, String tooltip) {
        JLabel label = newFormLabel(text);
        label.setToolTipText(tooltip);
        return label;
    }

    /**
     * Creates a label that when clicked opens the web browser on the provided
     * URL
     * @param url
     * @param text
     * @return
     */
    public static JLabel newHyperlinkLabel(final URI uri, String text) {
        final JLabel label = newLabel(text);


        Map<TextAttribute, Object> map = new HashMap();
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        final Font def = label.getFont();
        final Font hover = def.deriveFont(map);

        label.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        try {
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException ex) {
                            LOGGER.error("Could not open browser");
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        label.setFont(hover);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        label.setFont(def);
                    }
                });

        return label;
    }

    public static JLabel newHyperlinkLabel(URL url, String text) {
        try {
            return newHyperlinkLabel(url.toURI(), text);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return newLabel(text);
    }

    public static JLabel newHyperlinkLabel(String address, String text) {
        try {
            return newHyperlinkLabel(new URL(address), text);
        } catch (MalformedURLException ex) {
            LOGGER.error("Malformed URL: " + address);
        }
        return newLabel(text);

    }

    public static JLabel newVerticalLabel(String text,
                                          VerticalLabelUI.Rotation rotation) {
        JLabel label = newLabel(text);
        label.setUI(new VerticalLabelUI(rotation));
        return label;
    }

    public static JLabel newVerticalFormLabel(String text,
                                              VerticalLabelUI.Rotation rotation) {
        JLabel label = newFormLabel(text);
        label.setUI(new VerticalLabelUI(rotation));
        return label;
    }
}
