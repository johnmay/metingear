/**
 * ExcelWizzard.java
 *
 * 2011.08.04
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.importer.xls.wizzard;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import mnb.io.tabular.parser.UnparsableReactionError;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.parser.ExcelImporter;
import uk.ac.ebi.mnb.core.GeneralAction;
import mnb.io.resolve.AutomatedReconciler;
import mnb.io.resolve.EntryReconciler;
import mnb.io.tabular.EntityResolver;
import mnb.io.tabular.ExcelModelProperties;
import mnb.io.tabular.parser.ReactionParser;
import mnb.io.tabular.preparse.PreparsedReaction;
import mnb.io.tabular.preparse.PreparsedSheet;
import mnb.io.tabular.type.EntityColumn;
import mnb.io.tabular.type.ReactionColumn;
import mnb.io.tabular.xls.HSSFPreparsedSheet;
import uk.ac.ebi.mnb.interfaces.DialogController;
import uk.ac.ebi.mnb.view.DropdownDialog;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StarsCategory;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.chemet.ws.CachedChemicalWS;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
import uk.ac.ebi.metabolomes.webservices.ChemicalDBWebService;
import uk.ac.ebi.metabolomes.webservices.util.CandidateFactory;
import uk.ac.ebi.mnb.xls.options.ImporterOptions;
import uk.ac.ebi.reconciliation.ChemicalFingerprintEncoder;
import uk.ac.ebi.resource.chemical.ChEBIIdentifier;

/**
 * @name    ExcelWizzard
 * @date    2011.08.04
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class ExcelWizzard
        extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(ExcelWizzard.class);
    private CardLayout layout = new CardLayout();
    private int activeStage = 0;
    private String[] stages = new String[]{"Sheet Choice",
        "Reaction Column Choice",
        "Metabolite Column Choice",
        "Gene Column Choice"};
    private SheetChooserDialog sheetChooserDialog;
    private ReactionColumnChooser reactionColumnChooser;
    private JPanel shiftPanel = new JPanel();
    private WizzardStage[] stageObjects = new WizzardStage[4];
    private Reconstruction reconstruction;

    public ExcelWizzard(Reconstruction reconstruction,
            File file,
            ExcelImporter excelImporter,
            final JFrame parent) {

        super(parent, (DialogController) parent, "Excel Import");

        super.setPreferredSize(new Dimension(500, 300));

        this.reconstruction = reconstruction;

        // card layout for stages...
        shiftPanel.setLayout(layout);

        ImporterOptions options = new ImporterOptions();
        properties = new ExcelModelProperties();
        properties.put("reconstruction.file.name", file.getAbsolutePath());

        stageObjects[0] = new SheetChooserDialog(excelImporter, options, properties);
        stageObjects[1] = new ReactionColumnChooser(excelImporter, options, properties);
        stageObjects[2] = new MetaboliteColumnChooser(properties);
        stageObjects[3] = new ImportPanel(properties);

        for (int i = 0; i < 4; i++) {
            shiftPanel.add((JPanel) stageObjects[i], stages[i]);
        }

        add(shiftPanel, BorderLayout.CENTER);
        shiftPanel.setBorder(Borders.createEmptyBorder("4dlu, 4dlu, 4dlu, 4dlu"));

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FormLayout("pref:grow, p ,4dlu, right:min, p, left:min", "p"));
        navPanel.add(getClose(), new CellConstraints().xy(2, 1));
        navPanel.add(new JButton(new PrevPanel()), new CellConstraints().xy(4, 1));
        navPanel.add(new JButton(new NextPanel()), new CellConstraints().xy(6, 1));
        add(navPanel, BorderLayout.SOUTH);

    }
    private ExcelModelProperties properties;

    @Override
    public void process() {
        // do nothing
    }

    @Override
    public boolean update() {
        return true;
    }

    public class NextPanel extends GeneralAction {

        public NextPanel() {
            super("ExcelImporterNext");
        }

        public void actionPerformed(ActionEvent e) {
            if (stageObjects[activeStage].updateSelection()) {
                if (activeStage + 1 < stages.length) {
                    activeStage++;
                    stageObjects[activeStage].reloadPanel();
                    layout.show(shiftPanel, stages[activeStage]);
                } else {
                    try {
                        JProgressBar bar = ((ImportPanel) stageObjects[3]).getProgressBar();
                        int completed = 0;


                        properties.list(System.out);

                        // final stage
                        LOGGER.info("Begining import of excel document");

                        File xls = new File(properties.getProperty("reconstruction.file.name"));
                        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(xls));

                        Integer rxnI = Integer.parseInt(properties.getProperty("rxn.sheet"));
                        Integer metI = Integer.parseInt(properties.getProperty("ent.sheet"));

                        PreparsedSheet rxnSht = new HSSFPreparsedSheet(workbook.getSheetAt(rxnI),
                                properties,
                                ReactionColumn.DATA_BOUNDS);
                        PreparsedSheet entSht = new HSSFPreparsedSheet(workbook.getSheetAt(metI),
                                properties,
                                EntityColumn.DATA_BOUNDS);
                        bar.setValue(0);
                        bar.setMaximum(rxnSht.getRowCount());

                        ChemicalDBWebService ws =
                                new CachedChemicalWS(
                                new ChEBIWebServiceConnection(StarsCategory.ALL, 10));

                        CandidateFactory factory =
                                new CandidateFactory(ws,
                                new ChemicalFingerprintEncoder());

                        EntryReconciler reconciler = new AutomatedReconciler(factory,
                                new ChEBIIdentifier());

                        EntityResolver entitySheet = new EntityResolver(entSht, reconciler);
                        ReactionParser parser = new ReactionParser(entitySheet);


                        while (rxnSht.hasNext()) {

                            completed += 1;
                            final int i = completed;

                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    ((ImportPanel) stageObjects[3]).getProgressBar().setValue(i);
                                }
                            });

                            PreparsedReaction ppRxn = (PreparsedReaction) rxnSht.next();
                            try {

                                MetabolicReaction rxn =
                                        parser.parseReaction(ppRxn);
                                reconstruction.addReaction(rxn);


                            } catch (UnparsableReactionError ex) {
                                LOGGER.error("Error parsing reaction");
                            }
                        }

                        // close on finish
                        setVisible(false);

                    } catch (IOException ex) {
                        LOGGER.error("Could not load workbook:" + ex.getMessage());
                    }

                }
            }
        }
    }

    public class PrevPanel extends GeneralAction {

        public PrevPanel() {
            super("ExcelImporterPrev");
        }

        public void actionPerformed(ActionEvent e) {

            if (activeStage - 1 >= 0) {
                activeStage--;
                stageObjects[activeStage].reloadPanel();
                layout.show(shiftPanel, stages[activeStage]);
            }
        }
    }
}
