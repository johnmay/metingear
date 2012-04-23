package uk.ac.ebi.metingear.view;

import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;

import javax.swing.event.UndoableEditListener;


/**
 * @author johnmay
 */
public interface ControlDialog extends ProcessingDialog {


    public void setSelectionController(SelectionController controller);

    public void setUndoManager(UndoableEditListener undoManager);

    public void setReportManager(ReportManager report);

}
