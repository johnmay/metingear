/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view.labels;

import javax.swing.Icon;
import javax.swing.JLabel;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.visualisation.ViewUtils;

/**
 * Label.java
 *
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class MLabel extends JLabel {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(MLabel.class);

    public MLabel(String text) {
        this();
        setText(text);
    }

    public MLabel(String text, int horizontalAlignment) {
        this();
        setText(text);
        setHorizontalAlignment(horizontalAlignment);
    }

    public MLabel(Icon icon) {
        this();
        setIcon(icon);
    }

    public MLabel() {
        setFont(Settings.getInstance().getTheme().getBodyFont());
        setForeground(Settings.getInstance().getTheme().getForeground());
    }
}
