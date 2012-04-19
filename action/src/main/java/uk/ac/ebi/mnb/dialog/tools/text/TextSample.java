package uk.ac.ebi.mnb.dialog.tools.text;

import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.chemet.io.external.RunnableTask;
import uk.ac.ebi.interfaces.entities.Entity;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class TextSample extends ControllerDialog {

    public TextSample(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "BuildDialog");
    }

    @Override
    public void process() {

        String  query   = "11669627";
        Integer results = 10;

        RunnableTask task = new RunnableTask(){
            @Override
            public void prerun() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void postrun() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Entity newInstance() {
                throw new UnsupportedOperationException("Not supported: Do not use!");
            }

            @Override
            public void run() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };

        
        
    }
}
