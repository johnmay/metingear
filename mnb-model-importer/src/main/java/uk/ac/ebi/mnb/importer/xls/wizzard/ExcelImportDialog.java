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

import com.google.common.base.Joiner;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.event.UndoableEditListener;
import mnb.io.tabular.ExcelModelProperties;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.parser.ExcelHelper;
import uk.ac.ebi.mnb.xls.options.ImporterOptions;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Sizes;
import java.awt.CardLayout;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import mnb.io.resolve.AutomatedReconciler;
import mnb.io.resolve.EntryReconciler;
import mnb.io.resolve.ListSelectionReconciler;
import mnb.io.tabular.ExcelEntityResolver;
import mnb.io.tabular.parser.ReactionParser;
import mnb.io.tabular.parser.UnparsableReactionError;
import mnb.io.tabular.preparse.PreparsedReaction;
import mnb.io.tabular.preparse.PreparsedSheet;
import mnb.io.tabular.type.EntityColumn;
import mnb.io.tabular.type.ReactionColumn;
import mnb.io.tabular.xls.HSSFPreparsedSheet;
import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import uk.ac.ebi.io.service.ChEBINameService;
import uk.ac.ebi.metabolomes.webservices.util.CandidateFactory;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.caf.report.Report;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.interfaces.entities.MetabolicReaction;
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
public class ExcelImportDialog
        extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(ExcelImportDialog.class);

    private CardLayout layout = new CardLayout();

    private int activeStage = 0;

    private SheetChooserDialog sheetChooserDialog;

    private ReactionColumnChooser reactionColumnChooser;

    private JPanel shiftPanel = PanelFactory.createDialogPanel();

    private WizzardStage[] stages = new WizzardStage[4];

    private Reconstruction reconstruction;

    private CellConstraints cc = new CellConstraints();

    private JLabel label;

    private JFrame frame;


    public ExcelImportDialog(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits, Reconstruction reconstruction, File file, ExcelHelper helper) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");

        this.frame = frame;
        this.reconstruction = reconstruction;

        // card layout for stages...
        shiftPanel.setLayout(layout);

        ImporterOptions options = new ImporterOptions();
        properties = new ExcelModelProperties();
        properties.setFile(file);

        stages[0] = new SheetChooserDialog(helper, properties);
        stages[1] = new ReactionColumnChooser(helper, properties);
        stages[2] = new MetaboliteColumnChooser(helper, properties);
        stages[3] = new AdditionalOptions(properties);

        for (int i = 0; i < 4; i++) {
            shiftPanel.add((JPanel) stages[i], stages[i].getClass().getSimpleName());
        }

        getActivate().setEnabled(false);

        setDefaultLayout();

    }


    @Override
    public JLabel getDescription() {
        label = super.getDescription();
        label.setText("Excel Importer");
        return label;
    }


    @Override
    public JPanel getOptions() {
        return shiftPanel;
    }


    @Override
    public JPanel getNavigation() {
        JPanel panel = super.getNavigation();

        FormLayout navLayout = (FormLayout) panel.getLayout();

        navLayout.insertColumn(1, new ColumnSpec(Sizes.MINIMUM));
        navLayout.insertColumn(2, new ColumnSpec(Sizes.DLUX2));
        navLayout.insertColumn(3, new ColumnSpec(Sizes.MINIMUM));

        panel.add(new JButton(new PrevPanel()), cc.xy(1, 1));
        panel.add(new JButton(new NextPanel()), cc.xy(3, 1));

        return panel;
    }

    private ExcelModelProperties properties;


    @Override
    public void process() {
        // do nothing
    }


    @Override
    public void process(final SpinningDialWaitIndicator waitIndicator) {


        stages[activeStage].updateSelection(); // get final options

        if (reconstruction == null) {
            addMessage(new ErrorMessage("No active reconstruction - please open an existing reconstruction or create a new one"));
            return;
        }

        List<String> problemReactions = new ArrayList();
        ReactionParser parser = null;

        try {

            waitIndicator.setText(String.format("initialising"));

            // final stage
            LOGGER.info("Begining import of excel document");

            File xls = new File(properties.getFilePath());
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(xls));

            Integer rxnI = Integer.parseInt(properties.getProperty("rxn.sheet"));
            Integer metI = Integer.parseInt(properties.getProperty("ent.sheet"));

            final PreparsedSheet rxnSht = new HSSFPreparsedSheet(workbook.getSheetAt(rxnI),
                                                                 properties,
                                                                 ReactionColumn.DATA_BOUNDS);
            PreparsedSheet entSht = new HSSFPreparsedSheet(workbook.getSheetAt(metI),
                                                           properties,
                                                           EntityColumn.DATA_BOUNDS);
            waitIndicator.setText(String.format("initialising."));
            waitIndicator.repaint();

//            ChemicalDBWebService ws =
//                                 new CachedChemicalWS(
//                    new ChEBIWebServiceConnection(StarsCategory.ALL, 10));

            waitIndicator.setText(String.format("initialising.."));
            waitIndicator.repaint();


            CandidateFactory factory =
                             new CandidateFactory(ChEBINameService.getInstance(),
                                                  new ChemicalFingerprintEncoder());

            // todo should be selectable
            EntryReconciler reconciler = Boolean.parseBoolean(properties.getProperty("import.skip")) ? new AutomatedReconciler(factory, new ChEBIIdentifier())
                                         : new ListSelectionReconciler(frame, factory, new ChEBIIdentifier());

            ExcelEntityResolver entitySheet = new ExcelEntityResolver(entSht, reconciler, DefaultEntityFactory.getInstance());
            parser = new ReactionParser(entitySheet);

            waitIndicator.setText(String.format("initialising..."));
            waitIndicator.repaint();


            int completed = 0;
            while (rxnSht.hasNext()) {


                PreparsedReaction ppRxn = (PreparsedReaction) rxnSht.next();
                try {

                    MetabolicReaction rxn = parser.parseReaction(ppRxn);
                    if (rxn != null) {
                        reconstruction.addReaction(rxn);
                    } else {
                        problemReactions.add(ppRxn.getIdentifier());
                    }


                } catch (UnparsableReactionError ex) {
                    LOGGER.error("Error parsing reaction");
                }

                completed++;

                final int done = (int) (((float) completed / (float) rxnSht.getRowCount()) * 100);

                waitIndicator.setText(String.format("importing... %d%%", done));
                waitIndicator.repaint();

            }


        } catch (Exception ex) {
            addMessage(new ErrorMessage("Unable to import document " + ex.getMessage()));
            ex.printStackTrace();
        }

        if (!problemReactions.isEmpty()) {
            addMessage(new WarningMessage("The following reaction producted errors whilst loading: " + Joiner.on(", ").join(problemReactions)));
        }

        if (parser != null) {
            for (Report m : parser.collectMessages()) {
                addMessage(m);
            }
        }

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
            if (stages[activeStage].updateSelection()) {
                if (activeStage + 1 < stages.length) {
                    activeStage++;
                    stages[activeStage].reloadPanel();
                    layout.show(shiftPanel, stages[activeStage].getClass().getSimpleName());
                    label.setText(stages[activeStage].getDescription());
                    pack();
                    setLocation();
                } else {
                    getActivate().setEnabled(true);
                }
            }
        }
    }

    private Dimension orgSize;

    private Dimension shiftSize;


    @Override
    public Dimension getPreferredSize() {

        // we need to do this as cardlayout returns the largest size and we only want the currenly active size

        orgSize = orgSize == null ? super.getPreferredSize() : orgSize;
        shiftSize = shiftSize == null ? shiftPanel.getPreferredSize() : shiftSize;

        Dimension actualSize = ((JPanel) stages[activeStage]).getPreferredSize();
        shiftPanel.setPreferredSize(actualSize);

        return new Dimension(orgSize.width - shiftSize.width + actualSize.width, orgSize.height - shiftSize.height + actualSize.height + 20);

    }


    public class PrevPanel extends GeneralAction {

        public PrevPanel() {
            super("ExcelImporterPrev");
        }


        public void actionPerformed(ActionEvent e) {

            if (activeStage - 1 >= 0) {
                activeStage--;
                stages[activeStage].reloadPanel();
                layout.show(shiftPanel, stages[activeStage].getClass().getSimpleName());
                pack();
                setLocation();
            }
        }
    }
}
