/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view.labels;

import javax.swing.Icon;
import javax.swing.JLabel;
import uk.ac.ebi.mnb.settings.Settings;

/**
 * Label.java
 * Alt color label
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class DialogLabel extends JLabel {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(DialogLabel.class);

    public DialogLabel(String text) {
        this();
        setText(text);
    }

    public DialogLabel(Icon icon) {
        this();
        setIcon(icon);
    }

    public DialogLabel(String string, int i) {
        this();
        setText(string);
        setHorizontalAlignment(i);
    }

    public DialogLabel() {
        setFont(Settings.getInstance().getTheme().getBodyFont());
        setForeground(Settings.getInstance().getTheme().getAltForeground());
    }
}
