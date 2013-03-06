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

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.Report;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.Entity;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.identifier.IdentifierFactory;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.util.Collection;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public abstract class AbstractControlDialog
        extends AbstractProcessingDialog
        implements ControlDialog {

    private static final Logger LOGGER = Logger.getLogger(AbstractControlDialog.class);

    /* controllers */
    private UndoableEditListener undoManager;
    private SelectionController selection;
    private ReportManager reportManager;
    private TargetedUpdate updateManager;

    /* factories */
    private EntityFactory entities;
    private IdentifierFactory identifiers;
    private AnnotationFactory annotationFactory;

    public AbstractControlDialog(Window window) {
        super(window);
    }

    public void update() {
        updateManager.update();
    }

    public void update(EntityCollection entities) {
        updateManager.update(entities);
    }

    public UndoableEditListener getUndoManager() {
        return undoManager;
    }

    @Override
    public void setUndoManager(UndoableEditListener undoManager) {
        this.undoManager = undoManager;
    }

    public SelectionController getSelectionController() {
        return selection;
    }

    @Override
    public void setSelectionController(SelectionController selection) {
        this.selection = selection;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    @Override
    public void setReportManager(ReportManager report) {
        this.reportManager = report;
    }

    @Override
    public void setEntityFactory(EntityFactory entities) {
        this.entities = entities;
    }

    public EntityFactory getEntityFactory() {
        return entities;
    }

    @Override
    public void setIdentifierFactory(IdentifierFactory identifierFactory) {
        this.identifiers = identifierFactory;
    }

    public IdentifierFactory getIdentifierFactory() {
        return identifiers;
    }

    @Override
    public void setAnnotationFactory(AnnotationFactory annotationFactory){
        this.annotationFactory = annotationFactory;
    }

    @Override
    public void setUpdateManager(TargetedUpdate update){
        this.updateManager = update;
    }

    public TargetedUpdate getUpdateManager(){
        return this.updateManager;
    }

    /* UTIL FUNCTIONS */

    public void addReport(Report report) {
        this.reportManager.addReport(report);
    }

    public <T extends Entity> Collection<T> getSelection(Class<T> c) {
        return selection.getSelection().get(c);
    }

    public void addEdit(UndoableEdit edit){
        undoManager.undoableEditHappened(new UndoableEditEvent(this, edit));
    }

}
