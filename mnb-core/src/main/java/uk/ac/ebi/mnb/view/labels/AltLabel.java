/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view.labels;

import javax.swing.Icon;
import javax.swing.JLabel;
import uk.ac.ebi.mnb.core.ApplicationPreferences;
import uk.ac.ebi.mnb.view.ViewUtils;


/**
 * Label.java
 * Alt color label
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class AltLabel extends JLabel {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(AltLabel.class);


    public AltLabel(String text) {
        this();
        setText(text);
    }


    public AltLabel(Icon icon) {
        this();
        setIcon(icon);
    }


    public AltLabel() {
        setFont(ApplicationPreferences.getInstance().getTheme().getBodyFont());
        setForeground(ApplicationPreferences.getInstance().getTheme().getAltForeground());
    }


}

