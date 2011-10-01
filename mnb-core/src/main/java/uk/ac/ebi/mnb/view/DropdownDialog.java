/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view;

import com.sun.awt.AWTUtilities;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import uk.ac.ebi.mnb.core.CloseDialogAction;
import uk.ac.ebi.mnb.core.ProcessDialogAction;
import uk.ac.ebi.mnb.core.ApplicationPreferences;
import uk.ac.ebi.mnb.view.theme.Theme;


/**
 * DropdownDialog.java
 *
 *
 * @author johnmay
 * @date Apr 27, 2011
 */
public abstract class DropdownDialog
  extends JDialog {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      DropdownDialog.class);
    private JButton closeButton;
    private JButton runButton;
    private DialogController controller;


    public DropdownDialog(JFrame frame, DialogController controller, String dialogName) {
        super(frame, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        closeButton = new JButton(new CloseDialogAction(this));
        runButton = new JButton(new ProcessDialogAction(dialogName + ".DialogButton", this));
        setUndecorated(true);
        AWTUtilities.setWindowOpacity(this, 0.95f);
        Theme theme = ApplicationPreferences.getInstance().getTheme();
        setBackground(theme.getAltBackground());
    }


    @Override
    public void setLocation(int x, int y) {
        System.out.println("setting location: " + x + ", " + y);
        super.setLocation(x, y);
    }


    /**
     * Packs and sets the location of the dialog
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        if( visible ) {
            pack();
            setLocation();
        }
        super.setVisible(visible);
    }


    /**
     * Uses the DialogController to place the dialog
     */
    public void setLocation() {
        controller.place( (DropdownDialog) this);
    }


    public JButton getCloseButton() {
        return closeButton;
    }


    public void setCloseButton(JButton closeButton) {
        this.closeButton = closeButton;
    }


    public JButton getRunButton() {
        return runButton;
    }


    public void setRunButton(JButton runButton) {
        this.runButton = runButton;
    }


    /**
     * @inheritDoc
     */
    public void paint(Graphics g) {
        super.paint(g);
        Theme theme = ApplicationPreferences.getInstance().getTheme();
        // draws shaddow
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, Color.LIGHT_GRAY, 0, 10, theme.getBackground()));
        g2.fillRect(0, 0, getPreferredSize().width, 10);
    }


    public abstract void process();


    /**
     * Called on process finish this contain the calls to update the views
     * this method is automatically wrapped in a SwingUtils.invokeLater to be
     * thread safe
     */
    public abstract void update();


}

