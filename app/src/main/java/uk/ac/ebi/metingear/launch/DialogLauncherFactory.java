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

package uk.ac.ebi.metingear.launch;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.metingear.view.ControlDialog;
import uk.ac.ebi.mnb.interfaces.DialogController;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.mdk.domain.DefaultIdentifierFactory;

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
    private final TargetedUpdate updateManager;

    public DialogLauncherFactory(Window window,
                                 DialogController dialogController,
                                 SelectionController selectionController,
                                 UndoableEditListener undoableEditListener,
                                 ReportManager reportManager,
                                 TargetedUpdate updateManager
    ) {
        this.window               = window;
        this.dialogController     = dialogController;
        this.selectionController  = selectionController;
        this.undoableEditListener = undoableEditListener;
        this.reportManager        = reportManager;
        this.updateManager        = updateManager;
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
                    instance.setUpdateManager(updateManager);

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
                instance.prepare();
                instance.setVisible(true);
            }
        };
    }


}
