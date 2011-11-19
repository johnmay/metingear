/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view.labels;

import com.jgoodies.forms.factories.Borders;
import java.awt.Color;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import uk.ac.ebi.visualisation.ViewUtils;

/**
 * IconButton.java
 *
 *
 * @author johnmay
 * @date May 5, 2011
 */
public class IconButton extends JButton {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(IconButton.class);
    private Border border = Borders.createEmptyBorder("2dlu, 2dlu, 2dlu, 2dlu");
    private ButtonUI ui = new BasicButtonUI();

    public IconButton(Icon icon, Action a) {
        super(a);
        setUI(ui);
        setIcon(icon);
        setBackground(ViewUtils.CLEAR_COLOUR);
        setBorder(border);
        if (getIcon() != null) {
            setText(null);
        }
    }

    public IconButton(Action a) {
        super(a);
        setUI(ui);
        setBorder(border);
        setBackground(ViewUtils.CLEAR_COLOUR);
        if (getIcon() != null) {
            setText(null);
        }
    }
}
