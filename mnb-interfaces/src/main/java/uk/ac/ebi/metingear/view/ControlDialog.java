package uk.ac.ebi.metingear.view;

import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.identifier.IdentifierFactory;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.event.UndoableEditListener;


/**
 * @author johnmay
 */
public interface ControlDialog extends ProcessingDialog {


    public void setSelectionController(SelectionController controller);

    public void setUndoManager(UndoableEditListener undoManager);

    public void setReportManager(ReportManager report);

    public void setUpdateManager(TargetedUpdate update);

    public void setEntityFactory(EntityFactory entityFactory);

    public void setIdentifierFactory(IdentifierFactory identifierFactory);

    public void setAnnotationFactory(AnnotationFactory annotationFactory);

}
