package uk.ac.ebi.metingear.view;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.Report;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.interfaces.entities.Entity;
import uk.ac.ebi.interfaces.entities.EntityCollection;
import uk.ac.ebi.interfaces.entities.EntityFactory;
import uk.ac.ebi.mdk.domain.tool.AnnotationFactory;
import uk.ac.ebi.mdk.domain.tool.IdentifierFactory;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.event.UndoableEditListener;
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



    public void update() {
    }

    public void update(EntityCollection entities) {
        updateManager.update(entities);
    }


    public AbstractControlDialog(Window window) {
        super(window);
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

}
