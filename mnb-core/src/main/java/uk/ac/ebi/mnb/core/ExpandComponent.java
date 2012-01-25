/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.core;

import uk.ac.ebi.caf.action.GeneralAction;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;

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

    public ExpandComponent(JComponent component) {
        super("DropDownButton");
        this.component = component;
    }

    public void actionPerformed(ActionEvent e) {
        component.setVisible(button.isSelected());
    }

    public void setButton(ExpandButton button) {
        this.button = button;
    }
}
