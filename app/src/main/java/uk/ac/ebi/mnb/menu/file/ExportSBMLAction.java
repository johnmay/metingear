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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.file;

import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.xml.stax.SBMLWriter;
import uk.ac.ebi.caf.report.Report;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.io.xml.sbml.SBMLIOUtil;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Action event for the menu item to export the reconstrucSBML.
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class ExportSBMLAction
        extends FileChooserAction {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
                    ExportSBMLAction.class);

    private final MainController controller;

    public ExportSBMLAction(MainController controller) {
        super("ExportSBML");
        this.controller = controller;
    }

    private boolean xmlSuffix(File file) {
        return file.getName().endsWith(".xml");
    }

    @Override public void buildComponents() {
        super.buildComponents();
        super.getChooser().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        super.getChooser().setFileView(new FileView() {
            @Override public String getDescription(File f) {
                if (f.isDirectory() && f.getName().endsWith(".mr"))
                    return "Metingear Reconstruction";
                return super.getDescription(f);
            }

            @Override public Boolean isTraversable(File f) {
                // do not allow traversal into .mr folders
                if (f.isDirectory() && f.getName().endsWith(".mr"))
                    return false;
                return super.isTraversable(f);
            }
        });
    }

    @Override
    public void activateActions() {

        File tmp = getFile(showSaveDialog());

        // no file selected
        if (tmp == null)
            return;

        // correct file name if not sbml/xml
        final File file =
                tmp.getName().endsWith("xml") || tmp.getName().endsWith("sbml")
                ? tmp : new File(tmp.getPath() + ".xml");

        final SpinningDialWaitIndicator waitIndicator = new SpinningDialWaitIndicator((JFrame) controller);

        waitIndicator.setText("Exporting SBML to " + file);

        final int level = 2;
        final int version = 4;

        final List<Report> messages = new ArrayList<Report>();
        final long t0 = System.nanoTime();

        Thread thread = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    Logger.getRootLogger().setLevel(Level.WARN);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {
                            waitIndicator.setText("Exporting SBML to " + file + " (transferring to jSBML)");
                        }
                    });
                    SBMLIOUtil util = new SBMLIOUtil(DefaultEntityFactory.getInstance(), level, version);
                    SBMLDocument document = util.getDocument(DefaultReconstructionManager.getInstance().active());
                    long t1 = System.nanoTime();
                    Logger.getRootLogger().setLevel(Level.INFO);
                    Logger.getLogger(getClass()).info(TimeUnit.NANOSECONDS.toMillis(t1-t0) + " ms to transfer to SBML");
                    Logger.getRootLogger().setLevel(Level.WARN);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {
                            waitIndicator.setText("Exporting SBML to " + file + " (writing using jSBML)");
                        }
                    });
                    SBMLWriter writer = new SBMLWriter(' ', (short) 2);
                    writer.write(document, file);
                    long t2 = System.nanoTime();
                    Logger.getRootLogger().setLevel(Level.INFO);
                    Logger.getLogger(getClass()).info(TimeUnit.NANOSECONDS.toMillis(t2-t1) + " ms to write to SBML");
                    Logger.getRootLogger().setLevel(Level.WARN);
                } catch (IOException ex) {
                    messages.add(new ErrorMessage("unable to export SBML: " + ex.getMessage()));
                } catch (SBMLException ex) {
                    messages.add(new ErrorMessage("unable to export SBML: " + ex.getMessage()));
                } catch (XMLStreamException ex) {
                    messages.add(new ErrorMessage("unable to export SBML: " + ex.getMessage()));
                } finally {
                    Logger.getRootLogger().setLevel(Level.DEBUG);
                    // send update to UI
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            // add collected messages to the manager outside of thread
                            for (Report message : messages) {
                                controller.getMessageManager().addReport(message);
                            }
                            // dispose of wait indicator (spinning dial)
                            waitIndicator.dispose();
                            controller.update();
                        }
                    });


                }

            }
        });

        thread.setName("SBML Export");
        thread.start();


    }


}

