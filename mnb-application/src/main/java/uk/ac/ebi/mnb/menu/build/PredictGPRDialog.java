/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.build;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sri.biospice.warehouse.database.Warehouse;
import com.sri.biospice.warehouse.schema.DataSet;
import java.io.IOException;
import javax.swing.JSeparator;
import mnb.view.old.TaskManager;
import uk.ac.ebi.core.ReconstructionManager;
import mnb.todo.PredictGPR;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.mnb.view.labels.ThemedLabel;
import uk.ac.ebi.metabolomes.biowh.BiowhConnection;
import uk.ac.ebi.metabolomes.biowh.DataSetProvider;
import uk.ac.ebi.metabolomes.descriptor.observation.JobParameters;
import uk.ac.ebi.mnb.interfaces.DialogController;
import uk.ac.ebi.mnb.main.MainView;


/**
 * PredictGPRDialog.java
 * GPR = Gene Protein Reaction
 *
 * @author johnmay
 * @date May 7, 2011
 */
public class PredictGPRDialog
  extends DropdownDialog {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      PredictGPRDialog.class);


    public PredictGPRDialog() {
        super(MainView.getInstance(), MainView.getInstance(), "EnzymeAnnotation");
        layoutForm();
    }


    private void layoutForm() {
        // layout of dialog
        FormLayout layout = new FormLayout("p, 4dlu, p",
                                           "p, 4dlu, p, 4dlu, p");

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        builder.setDefaultDialogBorder();
        builder.add(new ThemedLabel("Gene-Protein-Reaction"), cc.xyw(1, 1, 3));
        builder.add(new JSeparator(), cc.xyw(1, 3, 3));
        builder.add(getClose(), cc.xy(1, 5));
        builder.add(getActivate(), cc.xy(3, 5));
        add(builder.getPanel());
    }


    @Override
    public void process() {

        setVisible(false);

        JobParameters params = new JobParameters("Gene-Protein-Reaction");

        // need to establish connection before we create the task
        try {
            logger.debug("Connecting to biowarehouse...");
            BiowhConnection bwhc = new BiowhConnection();
            Warehouse bwh = bwhc.getWarehouseObject();
            DataSetProvider.loadPropsForCurrentSchema();
            DataSet ds = DataSetProvider.getDataSetObject(DataSetProvider.DataSetEnum.KEGG);
        } catch( IOException ex ) {
            logger.error("Error openning the warehouse connection");
        }

        PredictGPR task = new PredictGPR(params, ReconstructionManager.getInstance().
          getActiveReconstruction().getGeneProducts());

        // todo.. move some to update..
        TaskManager.getInstance().add(task);
        MainView.getInstance().getSourceListController().update();

    }


    @Override
    public boolean update() {
        return MainView.getInstance().getViewController().update();
    }


}

