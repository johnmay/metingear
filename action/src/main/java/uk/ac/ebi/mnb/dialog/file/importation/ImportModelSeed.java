/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
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

import mnb.io.resolve.AutomatedReconciler;
import mnb.io.resolve.EntryReconciler;
import mnb.io.tabular.ExcelEntityResolver;
import mnb.io.tabular.ExcelModelProperties;
import mnb.io.tabular.parser.ReactionParser;
import mnb.io.tabular.parser.UnparsableReactionError;
import mnb.io.tabular.preparse.PreparsedReaction;
import mnb.io.tabular.preparse.PreparsedSheet;
import mnb.io.tabular.type.EntityColumn;
import mnb.io.tabular.type.ReactionColumn;
import mnb.io.tabular.xls.HSSFPreparsedSheet;
import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.ChEBIIdentifier;
import uk.ac.ebi.mdk.domain.tool.AutomaticCompartmentResolver;
import uk.ac.ebi.mdk.service.query.name.ChEBINameService;
import uk.ac.ebi.mdk.service.query.name.NameService;
import uk.ac.ebi.mdk.tool.resolve.ChemicalFingerprintEncoder;
import uk.ac.ebi.mdk.tool.resolve.NameCandidateFactory;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class ImportModelSeed extends DelayedBuildAction {

    private static final Logger LOGGER = Logger.getLogger(ImportModelSeed.class);

    private JFileChooser   chooser;
    private Window         parent;
    private MainController controller;

    public ImportModelSeed(Window parent, MainController controller) {
        super("ImportModelSeed");
        this.parent = parent;
        this.controller = controller;
    }

    @Override
    public void buildComponents() {
        chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                String name = f.getName();
                String extension = name.substring(Math.max(0, name.lastIndexOf('.')));
                return f.isDirectory() || extension.equals(".xls");
            }


            @Override
            public String getDescription() {
                return "model-SEED file (xls)";
            }
        });
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }

    @Override
    public void activateActions() {

        int choice = chooser.showOpenDialog(parent);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (f.getName().endsWith(".xls")) {
                importXLS(f);
            } else {
                importSBML(f);
            }
        }

    }

    public void importXLS(final File xls) {

        final Reconstruction recon = DefaultReconstructionManager.getInstance().getActive();

        final SpinningDialWaitIndicator waitIndicator = new SpinningDialWaitIndicator((JFrame) controller);

        waitIndicator.setText("Importing model-SEED");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(xls));

                    ExcelModelProperties properties = new ExcelModelProperties();
                    properties.load(getClass().getResourceAsStream("modelseed.properties"));

                    Integer rxnI = Integer.parseInt(properties.getProperty("rxn.sheet"));
                    Integer metI = Integer.parseInt(properties.getProperty("ent.sheet"));

                    PreparsedSheet rxnSht = new HSSFPreparsedSheet(workbook.getSheetAt(rxnI),
                                                                   properties,
                                                                   ReactionColumn.DATA_BOUNDS);
                    PreparsedSheet entSht = new HSSFPreparsedSheet(workbook.getSheetAt(metI),
                                                                   properties,
                                                                   EntityColumn.DATA_BOUNDS);

                    NameService service = new ChEBINameService();
                    service.startup();

                    NameCandidateFactory factory = new NameCandidateFactory(new ChemicalFingerprintEncoder(),
                                                                            service);

                    EntryReconciler reconciler = new AutomatedReconciler(factory,
                                                                         new ChEBIIdentifier());

                    ExcelEntityResolver entitySheet = new ExcelEntityResolver(entSht,
                                                                              reconciler,
                                                                              DefaultEntityFactory.getInstance());

                    ReactionParser reactionParser = new ReactionParser(entitySheet, new AutomaticCompartmentResolver());

                    while (rxnSht.hasNext()) {
                        try {
                            recon.addReaction(reactionParser.parseReaction((PreparsedReaction) rxnSht.next()));
                        } catch (final UnparsableReactionError rxn) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    controller.getMessageManager().addReport(new WarningMessage(rxn.getMessage()));
                                }
                            });
                        }
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.update();
                            waitIndicator.dispose();
                        }
                    });

                } catch (final IOException ex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.getMessageManager().addReport(new ErrorMessage("Could not import model-SEED file: " + ex.getMessage()));
                            waitIndicator.dispose();
                        }
                    });

                }
            }
        });
        t.setName("MODEL-SEED IMPORT");
        t.start();


    }

    public void importSBML(File f) {

    }

}
