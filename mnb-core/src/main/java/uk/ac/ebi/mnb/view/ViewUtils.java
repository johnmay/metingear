/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package uk.ac.ebi.mnb.view;

import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * ViewUtils.java
 * Util class for handling display
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public class ViewUtils {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(ViewUtils.class);
    // mono space fonts
    public static final Font COURIER_NEW_PLAIN_11 = new Font("Courier New", Font.PLAIN, 11);
    public static final Font MENOL_PLAIN_11 = new Font("Menlo", Font.PLAIN, 11);
    // normal fonts
    public static final Font VERDANA_PLAIN_11 = new Font("Verdana", Font.PLAIN, 11);
    public static final Font VERDANA_BOLD_11 = new Font("Verdana", Font.BOLD, 11);
    public static final Font VERDANA_UNDERLINE_PLAIN_11 = getUnderlineFont("Verdana", 11);
    public static final Font VERDANA_UNDERLINE_BOLD_11 = getUnderlineBoldFont("Verdana", 11);
    public static final Font HELVATICA_NEUE_PLAIN_11 = new Font("Helvatica Neue", Font.PLAIN, 11);
    public static final Font HELVATICA_NEUE_PLAIN_13 = new Font("Helvatica Neue", Font.PLAIN, 13);
    public static final Font HELVATICA_NEUE_PLAIN_15 = new Font("Helvatica Neue", Font.PLAIN, 15);
    public static final Font HELVATICA_NEUE_BOLD_11 = new Font("Helvatica Neue", Font.BOLD, 11);
    public static final Font HELVATICA_NEUE_BOLD_13 = new Font("Helvatica Neue", Font.BOLD, 13);
    public static final Font HELVATICA_NEUE_BOLD_15 = new Font("Helvatica Neue", Font.BOLD, 15);
    // set from prefrences
    public static final Font DEFAULT_MONO_SPACE_FONT = MENOL_PLAIN_11;
    public static final Font DEFAULT_BODY_FONT = VERDANA_PLAIN_11;
    public static final Font DEFAULT_HEADER_FONT = VERDANA_BOLD_11;
    public static final Font DEFAULT_LINK_FONT = VERDANA_UNDERLINE_PLAIN_11;
    public static final Font DEFAULT_LINK_HOVER_FONT = VERDANA_UNDERLINE_BOLD_11;
    // image icons
    public static final ImageIcon icon_16x16 = getIcon("images/networkbuilder_16x16.png",
            "Metabolic Network Builder");
    public static final ImageIcon icon_32x32 = getIcon("images/networkbuilder_32x32.png",
            "Metabolic Network Builder");
    public static final ImageIcon task = getIcon("images/runtasks_32x32.png",
            "Metabolic Network Builder");
    public static final ImageIcon icon_64x64 = getIcon("images/networkbuilder_64x64.png",
            "Metabolic Network Builder");
    public static final ImageIcon WARNING_ICON_16x16 = getIcon("images/cutout/warning_16x16.png",
            "Warning");
    public static final ImageIcon ERROR_ICON_16x16 = getIcon("images/cutout/error_16x16.png",
            "Warning");
    // buffered images
    public static final BufferedImage logo_32x32 = getImage(
            "images/networkbuilder_32x32.png");
    public static final BufferedImage logo_64x64 = getImage(
            "images/networkbuilder_64x64.png");
    public static final BufferedImage logo_128x128 = getImage(
            "images/networkbuilder_128x128.png");
    public static final BufferedImage logo_256x256 = getImage(
            "images/networkbuilder_256x256.png");
    public static final BufferedImage logo_512x512 = getImage(
            "images/networkbuilder_512x512.png");
    // colors
    public static final Color BACKGROUND = new Color(237, 237, 237);
    public static final Color CLEAR_COLOUR = new Color(255, 255, 255, 0);
    public static Color DARK_BACKGROUND = new Color(64, 64, 64);

    /**
     * Utility from lifted from (http://download.oracle.com/javase/tutorial/uiswing/components/icon.html)
     * to create an Icon from a resource
     * @param path
     * @param description
     * @return
     */
    public static ImageIcon getIcon(String path,
            String description) {

        java.net.URL imageURL = ViewUtils.class.getResource(path);

        if (imageURL != null) {
            return new ImageIcon(imageURL, description);
        } else {
            logger.error("Couldn't find file: " + path);
            return null;
        }
    }

    public static ImageIcon getIcon(String path) {

        java.net.URL imageURL = ViewUtils.class.getResource(path);

        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            logger.error("Couldn't find file: " + path);
            return null;
        }
    }

    public static BufferedImage getImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(ViewUtils.class.getResource(path));
        } catch (IOException ex) {
            logger.error("could not load image: " + path);
        }
        return img;

    }

    public static void setClipboard(String contents) {
        StringSelection stringSelection = new StringSelection(contents);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, new ClipboardOwner() {

            public void lostOwnership(Clipboard clipboard, Transferable contents) {
                // do nothing
            }
        });
    }

    public static FormLayout formLayoutHelper(int ncols, int nrows, int ygap, int xgap) {
        return new FormLayout(goodiesFormHelper(ncols, ygap, true),
                goodiesFormHelper(nrows, xgap, false));
    }

    /**
     * Wraps the text in {@code<html>..</html>} tags
     * @param html
     * @return
     */
    public static String htmlWrapper(String html) {
        StringBuilder sb = new StringBuilder(html.length() + 12);
        return sb.append("<html>").append(html).append("</html>").toString();
    }

    /**
     *
     * @param num
     * @param gap Gaps between components in dlu units
     * @return
     */
    public static String goodiesFormHelper(int nelements, int gap, boolean isColumn) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nelements; i++) {
            if (isColumn) {
                sb.append(i == 0 ? "right:" : "left:");
            }
            sb.append(JGOODIES_PREFERED_WIDTH);
            if ((i + 1) < nelements) {
                sb.append(JGOODIES_SEPERATOR).append(gap).append(JGOODIES_DLU).append(JGOODIES_SEPERATOR
                        + "");
            }
        }
        return sb.toString();
    }
    private static String JGOODIES_DLU = "dlu";
    private static String JGOODIES_SEPERATOR = ", ";
    private static String JGOODIES_PREFERED_WIDTH = "pref";

    private static Font getUnderlineFont(String family, int i) {

        Map<TextAttribute, Object> map = new HashMap();
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        map.put(TextAttribute.FAMILY, family);
        map.put(TextAttribute.SIZE, i);

        return new Font(map);

    }

    private static Font getUnderlineBoldFont(String family, int i) {
        Map<TextAttribute, Object> map = new HashMap();
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        map.put(TextAttribute.FAMILY, family);
        map.put(TextAttribute.SIZE, i);

        return new Font(map);
    }


}
