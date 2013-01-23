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

package uk.ac.ebi.metingear.view;

import uk.ac.ebi.mnb.interfaces.DialogController;

import javax.swing.*;

/**
 * @version $Rev$
 */
public interface ProcessingDialog {

    public void process();

    public void update();

    /**
     * Invoked just before the dialog is visible
     */
    public void prepare();

    public void setDialogController(DialogController controller);

    public void position();

    public void initialiseLayout();

    public JComponent getInformation();

    public JComponent getForm();

    public JComponent getNavigation();

    // nav buttons

    public JButton getCloseButton();

    public JButton getOkayButton();

    // these will be inherited form JDialog

    public void setVisible(boolean visible);

    public void pack();

}
