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
package uk.ac.ebi.mnb.core;

import net.sf.furbelow.SpinningDialWaitIndicator;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.mnb.view.DropdownDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * ProcessDialogAction <br> Class to handling the processing step of a dialog
 * (b). a) MenuItem > Create Dialog b) A Button > Process Dialog Values
 * <p/>
 * Invokes {
 *
 * @author johnmay @date Apr 27, 2011
 * @see DropdownDialog#process()} followed by {
 * @see DropdownDialog#update()}. The {
 * @see DropdownDialog#process()} action is performed in thread with the {
 * @see DropdownDialog#update()} action is wrapped in a {
 * @see SwingUtilities#invokeLater(Runnable)} call. Finally the action hides the
 *      provided dialog.
 */
public class ProcessDialogAction extends GeneralAction {

    private DropdownDialog dialog;


    public ProcessDialogAction(Class c, String command, DropdownDialog dialog) {
        super(c, command);
        this.dialog = dialog;
    }


    public ProcessDialogAction(String command, DropdownDialog dialog) {
        super(command);
        this.dialog = dialog;
    }

    /**
     * Invokes the attached dialog process method {
     *
     * @param e redundant action
     *
     * @see DropdownDialog#process()} in a different thread. On completion the
     *      provided dialog is hidden
     */
    public void actionPerformed(ActionEvent e) {

        // a wait indicator is shown whilst processing
        final SpinningDialWaitIndicator waiter = new SpinningDialWaitIndicator(dialog);


        Thread t = new Thread(new Runnable() {

            public void run() {
                try {

                    dialog.process(waiter);
                    dialog.pack();

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            waiter.dispose();
                            dialog.clear();
                            dialog.setVisible(false);
                            dialog.update();
                        }
                    });
                } catch (RuntimeException e) {
                    waiter.dispose();
                    dialog.clear();
                    dialog.setVisible(false);
                    if (dialog instanceof ControllerDialog) {
                        ControllerDialog controllerDialog = (ControllerDialog) dialog;
                        controllerDialog.addMessage(new ErrorMessage("A runtime error occurred: " + e.getMessage() + e.getCause()));
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    waiter.dispose();
                    dialog.clear();
                    dialog.setVisible(false);
                    if (dialog instanceof ControllerDialog) {
                        ControllerDialog controllerDialog = (ControllerDialog) dialog;
                        controllerDialog.addMessage(new ErrorMessage("An error occurred: " + e.getMessage() + e.getCause()));
                        e.printStackTrace();
                    }
                }
            }
        });
        t.setName(getClass().getSimpleName() + "-PROCESSING");
        t.start();

    }
}
