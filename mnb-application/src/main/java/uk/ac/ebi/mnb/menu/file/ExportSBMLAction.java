/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.file;

import net.sf.furbelow.SpinningDialWaitIndicator;
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
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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


    @Override
    public void activateActions() {

        final File file = getFile(showSaveDialog());
        final SpinningDialWaitIndicator waitIndicator = new SpinningDialWaitIndicator((JFrame) controller);

        waitIndicator.setText("Export SBML to " + file);

        final int level   = 2;
        final int version = 4;

        final List<Report> messages = new ArrayList<Report>();

        Thread thread = new Thread(new Runnable() {
            @Override public void run() {
                try {

                    SBMLIOUtil util = new SBMLIOUtil(DefaultEntityFactory.getInstance(), level, version);

                    SBMLDocument document = util.getDocument(DefaultReconstructionManager.getInstance().getActive());
                    SBMLWriter writer     = new SBMLWriter(' ', (short) 2);
                    writer.write(document, file);

                } catch (IOException ex) {
                    messages.add(new ErrorMessage("unable to export SBML: " + ex.getMessage()));
                } catch (SBMLException ex) {
                    messages.add(new ErrorMessage("unable to export SBML: " + ex.getMessage()));
                } catch (XMLStreamException ex) {
                    messages.add(new ErrorMessage("unable to export SBML: " + ex.getMessage()));
                } finally {

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

