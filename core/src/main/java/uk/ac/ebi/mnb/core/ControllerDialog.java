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
package uk.ac.ebi.mnb.core;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.Report;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.mnb.view.DropdownDialog;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.util.Collection;


/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 * @name MergeEntities - 2011.10.04 <br> Dialog that allows full control of
 * contents, selection, messages and update action
 */
public abstract class ControllerDialog extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(ControllerDialog.class);

    private final JFrame frame;

    private final TargetedUpdate updater;

    private final ReportManager messages;

    private final SelectionController controller;

    private final UndoableEditListener undoManager;


    public ControllerDialog(JFrame frame,
                            TargetedUpdate updater, // updatable called on completion
                            ReportManager messages, // used to post messages
                            SelectionController controller,
                            UndoableEditListener undoableEdits,
                            String name) {
        super(frame);
        this.frame = frame;
        this.undoManager = undoableEdits;
        this.updater = updater;
        this.messages = messages;
        this.controller = controller;
    }


    public void setSelection(Collection<? extends AnnotatedEntity> entities) {
        EntityCollection selection = new EntityMap(DefaultEntityFactory.getInstance());
        selection.addAll(entities);
        this.controller.setSelection(selection);
    }


    public EntityCollection getSelection() {
        return controller.getSelection();
    }

    /**
     * Prepare the dialog before it is shown.
     */
    public void prepare() {

    }


    /**
     * Add a message to the message manager
     *
     * @param edit
     */
    public void addMessage(Report message) {
        messages.addReport(message);
    }


    /**
     * Add an undoable edit to the undo manager
     *
     * @param edit
     */
    public void addEdit(UndoableEdit edit) {
        undoManager.undoableEditHappened(new UndoableEditEvent(this, edit));
    }


    /**
     * Class update() on the provided TargetedUpdate. Override this to provide
     * more efficient updating
     *
     * @return
     */
    @Override
    public boolean update() {
        return updater.update();
    }


    public boolean update(EntityCollection selection) {
        return updater.update(selection);
    }


    public void updateMenuContext() {
        if (frame instanceof MainController) {
            ((MainController) frame).updateMenuContext();
        } else {
            LOGGER.error("Can't fire menu update as frame is not a MainController!");
        }
    }
}
