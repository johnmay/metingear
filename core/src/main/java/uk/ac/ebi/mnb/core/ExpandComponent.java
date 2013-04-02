/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
