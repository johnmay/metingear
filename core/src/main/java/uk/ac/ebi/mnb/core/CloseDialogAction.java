/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
import uk.ac.ebi.mnb.view.DropdownDialog;

import java.awt.event.ActionEvent;
import javax.swing.JDialog;

/**
 * CloseDialogAction.java
 *
 *
 * @author johnmay
 * @date Apr 27, 2011
 */
public class CloseDialogAction extends GeneralAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CloseDialogAction.class);
    private JDialog dialog;

    public CloseDialogAction(JDialog dialog) {
        super("CloseDialog");
        this.dialog = dialog;
    }

    public CloseDialogAction(JDialog dialog, boolean named) {
        super(named ? "CloseDialog" : "");
        this.dialog = dialog;
    }

    public void actionPerformed(ActionEvent e) {
        if(dialog instanceof DropdownDialog){
            ((DropdownDialog) dialog).clear();
        }
        dialog.setVisible(false);
    }
}
