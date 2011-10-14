/**
 * MergeEntities.java
 *
 * 2011.10.04
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

import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AbstractAnnotatedEntity;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.Message;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.Updatable;
import uk.ac.ebi.mnb.view.DropdownDialog;

/**
 * @name    MergeEntities - 2011.10.04 <br>
 *          Dialog that allows full control of contents, selection, messages and update action
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract  class ControllerDialog extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(ControllerDialog.class);

    private final Updatable updater;
    private final MessageManager messages;
    private final SelectionController controller;
    private final UndoableEditListener undoableEdits;

    public ControllerDialog(JFrame frame,
                            Updatable updater,          // updatable called on completion
                            MessageManager messages,      // used to post messages
                            SelectionController controller,
                            UndoableEditListener undoableEdits,
                            String name) {
        super(frame, name);
        this.undoableEdits = undoableEdits;
        this.updater = updater;
        this.messages = messages;
        this.controller = controller;
    }


    public void setSelection(Collection<AbstractAnnotatedEntity> entities){
        this.controller.setSelection(entities);
    }

    public Collection<AnnotatedEntity> getSelection(){
        return controller.getSelection();
    }

    public void addMessage(Message message){
        messages.addMessage(message);
    }

    public void addEdit(UndoableEdit edit){
        undoableEdits.undoableEditHappened(new UndoableEditEvent(this, edit));
    }

    @Override
    public boolean update() {
        return updater.update();
    }

}
