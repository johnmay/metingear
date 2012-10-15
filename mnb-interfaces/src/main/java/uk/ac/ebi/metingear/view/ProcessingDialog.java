package uk.ac.ebi.metingear.view;

import uk.ac.ebi.mnb.interfaces.DialogController;

import javax.swing.*;

/**
 * @version $Rev$
 */
public interface ProcessingDialog {

    public void process();

    public void update();

    /**
     * Invoked just before the dialog is visible
     */
    public void prepare();

    public void setDialogController(DialogController controller);

    public void position();

    public void initialiseLayout();

    public JComponent getInformation();

    public JComponent getForm();

    public JComponent getNavigation();

    // nav buttons

    public JButton getCloseButton();

    public JButton getOkayButton();

    // these will be inherited form JDialog

    public void setVisible(boolean visible);

    public void pack();

}
