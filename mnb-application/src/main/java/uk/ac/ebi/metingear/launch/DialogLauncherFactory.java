package uk.ac.ebi.metingear.launch;

import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.util.DefaultAnnotationFactory;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.metingear.view.ControlDialog;
import uk.ac.ebi.mnb.interfaces.DialogController;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.resource.DefaultIdentifierFactory;

import javax.swing.event.UndoableEditListener;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @version $Rev$
 */
public class DialogLauncherFactory {

    private static final Logger LOGGER = Logger.getLogger(DialogLauncherFactory.class);

    private Window window;
    private final DialogController dialogController;
    private final SelectionController selectionController;
    private final UndoableEditListener undoableEditListener;
    private final ReportManager reportManager;

    public DialogLauncherFactory(Window window,
                                 DialogController dialogController,
                                 SelectionController selectionController,
                                 UndoableEditListener undoableEditListener,
                                 ReportManager reportManager
    ) {
        this.window               = window;
        this.dialogController     = dialogController;
        this.selectionController  = selectionController;
        this.undoableEditListener = undoableEditListener;
        this.reportManager        = reportManager;
    }

    public DelayedBuildAction getLauncher(final Class<? extends ControlDialog> c) {

        return new DelayedBuildAction(c, c.getSimpleName()) {

            private ControlDialog instance;

            @Override
            public void buildComponents() {
                try {

                    Constructor constructor = c.getConstructor(Window.class);
                    instance = (ControlDialog) constructor.newInstance(window);

                    // setup
                    instance.setDialogController(dialogController);
                    instance.setReportManager(reportManager);
                    instance.setSelectionController(selectionController);
                    instance.setUndoManager(undoableEditListener);

                    // setup factories
                    instance.setAnnotationFactory(DefaultAnnotationFactory.getInstance());
                    instance.setIdentifierFactory(DefaultIdentifierFactory.getInstance());
                    instance.setEntityFactory(DefaultEntityFactory.getInstance());

                    // layout
                    instance.initialiseLayout();


                } catch (NoSuchMethodException ex) {
                    LOGGER.error(c.getSimpleName() + " does now have the expected constructor of type: 'new " + c.getSimpleName() + "(Window.class);'");
                } catch (InvocationTargetException e) {
                    LOGGER.error("Unable to create dialog: " + c.getSimpleName(), e);
                } catch (InstantiationException e) {
                    LOGGER.error("Unable to create dialog: " + c.getSimpleName(), e);
                } catch (IllegalAccessException e) {
                    LOGGER.error("Unable to create dialog: " + c.getSimpleName(), e);
                }
            }

            @Override
            public void activateActions() {
                instance.pack();
                instance.position();
                instance.setVisible(true);
            }
        };
    }


}
