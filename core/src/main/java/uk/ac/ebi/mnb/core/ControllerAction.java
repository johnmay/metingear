/**
 * SelectionAction.java
 *
 * 2011.10.03
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.core;

import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.caf.report.Report;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.interfaces.MainController;


/**
 * @name    SelectionAction - 2011.10.03 <br>
 *          Controller action is action that delegates calls to MainController
 *          enabling access to methods such as {@see getSelection()}
 *
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class ControllerAction extends GeneralAction {

    private MainController controller;


    public ControllerAction(String command, MainController controller) {
        super(command);
        this.controller = controller;
    }


    /**
     * Access the selection from the MainController child, ViewController.
     * @return
     */
    public EntityCollection getSelection() {
        return controller.getViewController().getSelection();
    }


    public boolean setSelection(EntityCollection selection) {
        return controller.getViewController().setSelection(selection);
    }


    public void addMessage(Report report) {
        controller.getMessageManager().addReport(report);
    }


    public boolean update() {
        return controller.update();
    }


    public boolean update(EntityCollection collection) {
        return controller.update(collection);
    }


    public MainController getController() {
        return controller;
    }
}
