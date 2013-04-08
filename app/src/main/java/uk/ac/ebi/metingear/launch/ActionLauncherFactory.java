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

import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.DefaultIdentifierFactory;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.service.EDTProgressListener;
import uk.ac.ebi.mdk.service.ProgressListener;
import uk.ac.ebi.metingear.view.ControlAction;
import uk.ac.ebi.mnb.interfaces.DialogController;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditListener;

/** @author John May */
public class ActionLauncherFactory {
    private static final Logger LOGGER = Logger
            .getLogger(DialogLauncherFactory.class);

    private JFrame frame;
    private final DialogController dialogController;
    private final SelectionController selectionController;
    private final UndoableEditListener undoableEditListener;
    private final ReportManager reportManager;
    private final TargetedUpdate updateManager;

    public ActionLauncherFactory(JFrame frame,
                                 DialogController dialogController,
                                 SelectionController selectionController,
                                 UndoableEditListener undoableEditListener,
                                 ReportManager reportManager,
                                 TargetedUpdate updateManager
                                ) {
        this.frame = frame;
        this.dialogController = dialogController;
        this.selectionController = selectionController;
        this.undoableEditListener = undoableEditListener;
        this.reportManager = reportManager;
        this.updateManager = updateManager;
    }

    public DelayedBuildAction getLauncher(final Class<? extends ControlAction> c) {
        return new DelayedBuildAction(c, c.getSimpleName()) {

            private ControlAction processable;

            @Override public void buildComponents() {
                try {
                    processable = c.newInstance();
                    processable.setReportManager(reportManager);
                    processable.setSelectionController(selectionController);
                    processable.setUndoManager(undoableEditListener);
                    processable.setAnnotationFactory(DefaultAnnotationFactory
                                                             .getInstance());
                    processable.setIdentifierFactory(DefaultIdentifierFactory
                                                             .getInstance());
                    processable.setEntityFactory(DefaultEntityFactory
                                                         .getInstance());
                } catch (InstantiationException e) {
                    LOGGER.error(e);
                } catch (IllegalAccessException e) {
                    LOGGER.error(e);
                }
            }

            @Override public void activateActions() {
                final SpinningDialWaitIndicator indicator = new SpinningDialWaitIndicator(frame);
                final ProgressListener listener = EDTProgressListener
                        .safeDispatch(new IndicatorProgressListener(indicator));
                Thread t = new Thread(new Runnable() {
                    @Override public void run() {
                        try {
                            processable.process(listener);
                        } catch (RuntimeException e) {
                            LOGGER.error(e);
                        } catch (Exception e) {
                            LOGGER.error(e);
                        } finally {
                            listener.progressed("updating views");
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override public void run() {
                                    updateManager.update();
                                }
                            });
                            indicator.dispose();
                        }
                    }
                });
                t.setName(c.getSimpleName() + "-PROCESSING");
                t.start();
            }
        };
    }

    private static class IndicatorProgressListener implements ProgressListener {
        private final SpinningDialWaitIndicator indicator;

        private IndicatorProgressListener(SpinningDialWaitIndicator indicator) {
            this.indicator = indicator;
        }

        @Override public void progressed(String message) {
            indicator.setText(message);
        }
    }
}
