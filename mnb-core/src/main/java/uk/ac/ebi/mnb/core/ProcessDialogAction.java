/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package uk.ac.ebi.mnb.core;

import furbelow.SpinningDialWaitIndicator;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.mnb.core.GeneralAction;


/**
 * NewProjectAction.java
 * Class to handling the processing step of a dialog (b).
 *  a) MenuItem > Create Dialog
 *  b) A Button > Process Dialog Values
 *
 * @author johnmay
 * @date Apr 27, 2011
 */
public class ProcessDialogAction extends GeneralAction {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      ProcessDialogAction.class);
    private DropdownDialog dialog;


    public ProcessDialogAction(String command, DropdownDialog dialog) {
        super(command);
        this.dialog = dialog;
    }


    /**
     * Perform the action of calling the MNBDialogs process method
     * hide the dialog
     * @param e redundant action
     */
    public void actionPerformed(ActionEvent e) {
        final SpinningDialWaitIndicator waiter = new SpinningDialWaitIndicator(dialog);
        new Thread(new Runnable() {

            public void run() {
                dialog.process();
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        waiter.dispose();
                        dialog.setVisible(false);
                        dialog.update();
                    }


                });
            }


        }).start();
    }


}

