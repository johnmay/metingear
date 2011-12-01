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
import java.awt.event.KeyEvent;
import uk.ac.ebi.mnb.view.DropdownDialog;

import javax.swing.SwingUtilities;

/**
 * ProcessDialogAction <br>
 * Class to handling the processing step of a dialog (b).
 *  a) MenuItem > Create Dialog
 *  b) A Button > Process Dialog Values
 *
 * Invokes {@see DropdownDialog#process()} followed by {@see DropdownDialog#update()}.
 * The {@see DropdownDialog#process()} action is performed in thread with the
 * {@see DropdownDialog#update()} action is wrapped in a {@see SwingUtilities#invokeLater(Runnable)}
 * call. Finally the action hides the provided dialog.
 *
 * @author johnmay
 * @date Apr 27, 2011
 */
public class ProcessDialogAction extends GeneralAction {

    private DropdownDialog dialog;

    public ProcessDialogAction(String command, DropdownDialog dialog) {
        super(command);
        this.dialog = dialog;
    }

    /**
     * Invokes the attached dialog process method {@see DropdownDialog#process()} in a
     * different thread. On completion the provided dialog is hidden
     * @param e redundant action
     */
    public void actionPerformed(ActionEvent e) {

        // a wait indicator is shown whilst processing
        final SpinningDialWaitIndicator waiter = new SpinningDialWaitIndicator(dialog);


        Thread t = new Thread(new Runnable() {

            public void run() {
                dialog.process(waiter);

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        waiter.dispose();
                        dialog.setVisible(false);
                        dialog.update();
                    }
                });
            }
        });
        t.setName(getClass().getSimpleName() + "-PROCESSING");
        t.start();

    }
}
