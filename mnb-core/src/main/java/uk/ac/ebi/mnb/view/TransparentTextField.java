/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view;

import java.awt.Color;
import javax.swing.JTextField;
import uk.ac.ebi.mnb.core.ApplicationPreferences;
import uk.ac.ebi.mnb.view.theme.Theme;


/**
 * TransparentTextField.java
 *
 *
 * @author johnmay
 * @date Apr 29, 2011
 */
public class TransparentTextField
  extends JTextField {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      TransparentTextField.class);


    public TransparentTextField(String text, int columns, boolean editable) {
        super(text, columns);
        Theme theme = ApplicationPreferences.getInstance().getTheme();
        setFont(theme.getBodyFont());
        setForeground(theme.getForeground());
        setBackground(theme.getBackground());
        setBorder(null);
        setEditable(editable);
    }


    public TransparentTextField(String text, int columns) {
        this(text, columns, true);
    }


    public TransparentTextField(int columns) {
        this(null, columns);
    }


    public TransparentTextField(String text) {
        this(text, text.length());
    }


    public TransparentTextField(String text, Boolean editable) {
        this(text, text.length(), editable);
    }


    public TransparentTextField() {
        this("", 3);
    }


    @Override
    public void setText(String t) {
        super.setText(t);
    }


}

