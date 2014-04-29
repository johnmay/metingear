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
package uk.ac.ebi.mnb.dialog.file.importation;

import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.caf.report.Report;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.tool.AutomaticCompartmentResolver;
import uk.ac.ebi.mdk.io.xml.sbml.SBMLReactionReader;
import uk.ac.ebi.mdk.ui.edit.reaction.DialogCompartmentResolver;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;


/**
 * ImportSBML - 2011.12.01 <br> Class allows importation of SBML
 * models/networks
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class ImportSBML extends DelayedBuildAction {

    private static final Logger LOGGER = Logger.getLogger(ImportSBML.class);

    private MainController controller;

    private JFileChooser chooser;


    public ImportSBML(MainController controller) {
        super("ImportSBML");
        this.controller = controller;
    }


    @Override
    public void buildComponents() {
        chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                String name = f.getName();
                String extension = name.substring(Math.max(0, name
                        .lastIndexOf('.')));
                return f.isDirectory() || extension.equals(".xml") || extension
                        .equals(".sbml");
            }


            @Override
            public String getDescription() {
                return "SBML File (xml,sbml)";
            }
        });
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }


    @Override
    public void activateActions() {


        if (chooser
                .showOpenDialog((JFrame) controller) == JFileChooser.APPROVE_OPTION) {
            InputStream in = null;


            final Set<Report> messages = new HashSet<Report>();

            LOGGER.info("Importing SBML model");
            final File choosen = chooser.getSelectedFile();

            final SpinningDialWaitIndicator wait = new SpinningDialWaitIndicator((JFrame) controller);
            wait.setText("Importing " + choosen.getName());

            ReconstructionManager manager = DefaultReconstructionManager
                    .getInstance();
            final Reconstruction active = manager.active();

            Thread t = new Thread(new Runnable() {

                public void run() {
                    InputStream in = null;
                    try {
                        // turn off debug logging, can be a lot when reading the SBML
                        Logger.getRootLogger().setLevel(Level.WARN);
                        in = new BufferedInputStream(new FileInputStream(choosen));
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() {
                                wait.setText("reading from XML to SBMLDocument (JSBML)...");
                            }
                        });
                        long t0 = System.nanoTime();
                        SBMLDocument document = SBMLReader.read(in);
                        long t1 = System.nanoTime();

                        long tJSBML = t1 - t0;

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() {
                                wait.setText("reading from SBMLDocument to Reconstruction..");
                            }
                        });
                        EntityFactory entities = DefaultEntityFactory
                                .getInstance();
                        SBMLReactionReader reader = new SBMLReactionReader(document,
                                                                           entities,
                                                                           new DialogCompartmentResolver(new AutomaticCompartmentResolver(),
                        
                                                                                                         (JFrame) controller));
                        t0 = System.nanoTime();
                        while (reader.hasNext()) {
                            active.addReaction(reader.next());
                        }
                        t1 = System.nanoTime();
                        long tConv = t1 - t0;

                        Logger.getRootLogger().setLevel(Level.DEBUG);

                        LOGGER.info(String.format("Loaded SBML in %.0f ms (JSBML), %.0f ms (conv)", tJSBML / 1e6, tConv / 1e6));
                        
                    } catch (IOException e) {
                        messages.add(new ErrorMessage(e.getMessage()));
                    } catch (XMLStreamException ex) {
                        messages.add(new ErrorMessage(ex.getMessage()));
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {
                            // ignored
                        }
                        Logger.getRootLogger().setLevel(Level.DEBUG);
                    }

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            // add collected messages to the manager outside of thread
                            for (Report message : messages) {
                                controller.getMessageManager()
                                          .addReport(message);
                            }
                            wait.dispose();
                            controller.update();
                        }
                    });


                }
            });
            t.setName("SBML Import");
            t.start();

        }


    }
}
