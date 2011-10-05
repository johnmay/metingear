/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view.labels;

import javax.swing.Icon;
import javax.swing.JLabel;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.view.ViewUtils;

/**
 * Label.java
 *
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class ThemedLabel extends JLabel {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(ThemedLabel.class);

    public ThemedLabel(String text) {
        this();
        setText(text);
    }

    public ThemedLabel(String text, int horizontalAlignment) {
        this();
        setText(text);
        setHorizontalAlignment(horizontalAlignment);
    }

    public ThemedLabel(Icon icon) {
        this();
        setIcon(icon);
    }

    public ThemedLabel() {
        setFont(Settings.getInstance().getTheme().getBodyFont());
        setForeground(Settings.getInstance().getTheme().getForeground());
    }
}
