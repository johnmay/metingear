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
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.chemet.resource.chemical.ChEBIIdentifier;
import uk.ac.ebi.chemet.service.query.name.ChEBINameService;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.metabolomes.webservices.util.CandidateFactory;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.reconciliation.ChemicalFingerprintEncoder;

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

    private JFileChooser chooser;
    private Window parent;
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
                return f.isDirectory()  || extension.equals(".xls");
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

    public void importXLS(File xls) {

        Reconstruction recon = ReconstructionManager.getInstance().getActive();

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

            CandidateFactory factory = new CandidateFactory(new ChEBINameService(),
                                                            new ChemicalFingerprintEncoder());

            EntryReconciler reconciler = new AutomatedReconciler(factory,
                                                                 new ChEBIIdentifier());

            ExcelEntityResolver entitySheet = new ExcelEntityResolver(entSht,
                                                                      reconciler,
                                                                      DefaultEntityFactory.getInstance());

            ReactionParser reactionParser = new ReactionParser(entitySheet);

            while (rxnSht.hasNext()) {
                try {
                    recon.addReaction(reactionParser.parseReaction((PreparsedReaction)rxnSht.next()));
                }catch (UnparsableReactionError rxn){
                    controller.getMessageManager().addReport(new WarningMessage(rxn.getMessage()));
                }
            }

            controller.update();

        } catch (IOException ex) {
            controller.getMessageManager().addReport(new ErrorMessage("Could not import model-SEED file: " + ex.getMessage()));
        }


    }

    public void importSBML(File f) {

    }

}
