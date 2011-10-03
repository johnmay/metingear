/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view.labels;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import uk.ac.ebi.mnb.settings.Settings;


/**
 * BoldLabel.java
 * Bold label using the default header font
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public class BoldLabel
  extends JLabel {

    public BoldLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        setForeground(Settings.getInstance().getTheme().getForeground());
        setFont(Settings.getInstance().getTheme().getHeaderFont());
    }


    public BoldLabel(String text, Icon icon) {
        this(text, icon, SwingConstants.TRAILING);
    }


    public BoldLabel(String text) {
        this(text, null);
    }


    public BoldLabel(Icon icon) {
        this(null, icon);
    }


}

