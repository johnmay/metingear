/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.build;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import uk.ac.ebi.core.reconstruction.ReconstructionContents;
import mnb.view.old.TaskManager;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.metabolomes.core.gene.GeneProteinProduct;
import uk.ac.ebi.metabolomes.descriptor.observation.JobParameters;
import uk.ac.ebi.metabolomes.resource.BlastMatrix;
import uk.ac.ebi.metabolomes.run.BlastHomologySearch;
import uk.ac.ebi.metabolomes.run.BlastHomologySearchFactory;
import uk.ac.ebi.mnb.main.MainView;


/**
 * SwissProtHomologyDialog.java
 *
 *
 * @author johnmay
 * @date Apr 27, 2011
 */
public class SwissProtHomologyDialog
  extends DropdownDialog {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      SwissProtHomologyDialog.class);
    private JCheckBox useLocalHomologyCheckBox;
    private JCheckBox useEnzymeProfileCheckBox;
    private JTextField expectValueThresholdField;
    private JSpinner blosumMatrixThresholdField;
    private JSpinner cpuSpinner;
    private JSpinner maxSequenceSpinner;
    private Integer[] maxSequenceValues;


    public SwissProtHomologyDialog() {
        super(MainView.getInstance(), MainView.getInstance(), "SwissProtHomology");
        setName("Find SwissProt Homologies");
        //setUndecorated( true );
        buildComponents();
        layoutComponents();
        pack();
        setLocationRelativeTo(null);
    }


    /**
     * Builds the form components
     */
    private void buildComponents() {

        expectValueThresholdField = new JTextField("1e-30");

        cpuSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 8, 1));
        maxSequenceValues = new Integer[]{ 16, 32, 64, 128, 256 };
        maxSequenceSpinner = new JSpinner(new SpinnerListModel(maxSequenceValues));
        blosumMatrixThresholdField = new JSpinner(new SpinnerListModel(BlastMatrix.values()));
        maxSequenceSpinner.setValue(maxSequenceValues[2]);
        blosumMatrixThresholdField.setValue(BlastMatrix.BLOSUM80);

    }


    /**
     * Lays out the components
     */
    private void layoutComponents() {

        FormLayout layout =
                   new FormLayout("right:100dlu, 4dlu, left:48dlu, 4dlu, 48dlu",
                                  "pref, 1dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref ");

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        int componentConstraintY = -1;

        componentConstraintY += 2;
        builder.addLabel("Expected Value Threshold:", cc.xy(1, componentConstraintY));
        builder.add(expectValueThresholdField, cc.xyw(3, componentConstraintY, 3));
        componentConstraintY += 2;

        builder.addLabel("Filter Sequences:", cc.xy(1, componentConstraintY));
        builder.add(expectValueThresholdField, cc.xyw(3, componentConstraintY, 3));
        componentConstraintY += 2;

        builder.addLabel("Substitution Matrix:", cc.xy(1, componentConstraintY));
        builder.add(blosumMatrixThresholdField, cc.xyw(3, componentConstraintY, 3));
        componentConstraintY += 2;

        builder.addSeparator("", cc.xyw(1, componentConstraintY, 5));
        componentConstraintY += 2;

        builder.addLabel("Number of CPU(s):", cc.xy(1, componentConstraintY));
        builder.add(cpuSpinner, cc.xyw(3, componentConstraintY, 3));

        componentConstraintY += 2;

        builder.addLabel("Max sequences per search:", cc.xy(1, componentConstraintY));
        builder.add(maxSequenceSpinner, cc.xyw(3, componentConstraintY, 3));
        componentConstraintY += 2;

        builder.add(getClose(), cc.xy(3, componentConstraintY));
        builder.add(getActivate(), cc.xy(5, componentConstraintY));

        add(builder.getPanel());
    }


    @Override
    public void process() {

        Reconstruction activeProject = ReconstructionManager.getInstance().getActiveReconstruction();

        JobParameters parameters = new JobParameters("Blast Test");

        GeneProteinProduct[] products = activeProject.getGeneProducts().getProteinProducts();

        // if there are no protein products then show a warning message
        if( products == null || products.length == 0 ) {
            MainView.getInstance().addWarningMessage(
              "Cannot run UniProt/SwissProt homology, project contains no protein products.");
            return;
        }

        BlastHomologySearchFactory.setMaxSequences((Integer) maxSequenceSpinner.getValue());
        BlastHomologySearch[] searches =
                              BlastHomologySearchFactory.getBlastPonSwissProtTasks(products,
                                                                                   ((BlastMatrix) blosumMatrixThresholdField.
                                                                                    getValue()),
                                                                                   expectValueThresholdField.
          getText(),
                                                                                   (Integer) cpuSpinner.
          getValue(),
                                                                                   parameters);

        // add to the task manager, update the task table and switch the view
        TaskManager tm = TaskManager.getInstance();
        tm.add(searches);
        //MainView.getInstance().setTaskTableView();
        MainView.getInstance().update(); // only need to to tasks


        // todo: need to move to post run in the RunnableTask which means moving the Runnable task...
        activeProject.addContents(ReconstructionContents.SWISSPROT_HOMOLOGY);



    }


    @Override
    public boolean update() {
        return true;
    }


}

