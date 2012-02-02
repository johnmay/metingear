/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.core;

import uk.ac.ebi.caf.action.GeneralAction;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;


/**
 * ExpandComponent.java
 *
 *
 * @author johnmay
 * @date May 9, 2011
 */
public class ExpandComponent
        extends GeneralAction {

    private ExpandButton button;

    private JComponent component;

    private JDialog dialog;


    public ExpandComponent(JComponent component, JDialog dialog) {
        super("DropDownButton");
        this.component = component;
        this.dialog = dialog;
    }


    public void actionPerformed(ActionEvent e) {
        if (dialog != null) {
            dialog.pack(); // packing before ensure's an update
        }
        component.setVisible(button.isSelected());
        if (dialog != null) {
            dialog.pack();
        }
    }


    public void setButton(ExpandButton button) {
        this.button = button;
    }
}
