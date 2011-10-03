/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.awt.AWTUtilities;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Paint;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import sun.net.ApplicationProxy;
import uk.ac.ebi.mnb.core.ActionProperties;
import uk.ac.ebi.mnb.core.CloseDialogAction;
import uk.ac.ebi.mnb.core.ProcessDialogAction;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.view.labels.Label;
import uk.ac.ebi.mnb.view.theme.Theme;

/**
 *
 * DropdownDialog.java
 * A drop down modal dialog
 *
 * @author johnmay
 * @date Apr 27, 2011
 *
 */
public abstract class DropdownDialog
        extends JDialog {

    private static final Logger LOGGER = Logger.getLogger(DropdownDialog.class);
    private JButton close;
    private JButton active;
    private DialogController controller;
    private Theme theme = Settings.getInstance().getTheme();
    private Paint paint = new GradientPaint(0, 0, Color.LIGHT_GRAY, 0, 10, Settings.getInstance().getTheme().getDialogBackground());

    public DropdownDialog(JFrame frame,
            DialogController controller,
            String type) {

        super(frame, ModalityType.APPLICATION_MODAL);

        this.controller = controller;
        close = new JButton(new CloseDialogAction(this));
        active = new JButton(new ProcessDialogAction(type + ".DialogButton", this));
        setUndecorated(true);
    }

    /**
     * Allows easy instantiation with a JFrame that implements DialogController.
     * If the frame does not implement DialogController then an InstantiationError
     * will be thrown
     * @param frame
     * @param dialogName
     */
    public DropdownDialog(JFrame frame,
            String dialogName) {

        super(frame, ModalityType.APPLICATION_MODAL);

        if (frame instanceof DialogController) {
            this.controller = (DialogController) frame;
        } else {
            LOGGER.error("Attempt to instantiate DropdownDialog without a dialog controller");
            throw new InstantiationError("Frame " + frame.getName() + "does not implement DialogController");
        }

        close = new JButton(new CloseDialogAction(this));
        active = new JButton(new ProcessDialogAction(dialogName + ".DialogButton", this));
        setUndecorated(true);
    }

    /**
     * Updates the theme to that current stored in {@see ApplicationPreferences} and sets values on affected
     * components
     */
    public void updateTheme() {
        this.theme = Settings.getInstance().getTheme();
        AWTUtilities.setWindowOpacity(this, theme.getDialogOpacity());
        setBackground(theme.getDialogBackground());
        this.paint = new GradientPaint(0, 0, Color.LIGHT_GRAY, 0, 10, Settings.getInstance().getTheme().getDialogBackground());

    }

    /**
     * Returns the dialog description label. By default the description is the Class name and should
     * be overridden to return a meaningful description of what the dialog does
     * @return
     */
    public JLabel getDescription() {
        return new Label(getClass().getSimpleName());
    }

    /**
     *
     * Returns the options section of the dialog. This method should be over-
     * ridden if use default layout
     *
     * @return
     * 
     */
    public JPanel getOptions() {

        JPanel options = new DialogPanel();

        options.add(new Label("Options", SwingConstants.CENTER));

        return options;

    }

    /**
     * Sets the default layout of the dialog. Class wishing to use Default layout
     * should overrider getDescription and getOptions. In addition to adding
     * values to the ActionProperties file.
     */
    public void setDefaultLayout() {

        LayoutManager layout = new FormLayout("p:grow, right:min, 4dlu ,right:min",
                "p, 4dlu, p, 4dlu, p, 4dlu, p");
        JPanel panel = new DialogPanel(layout);
        CellConstraints cc = new CellConstraints();

        panel.setBorder(Borders.DLU7_BORDER);

        panel.add(getDescription(), cc.xyw(1, 1, 4));
        panel.add(new JSeparator(SwingConstants.HORIZONTAL), cc.xyw(1, 3, 4));
        panel.add(getOptions(), cc.xyw(1, 5, 4));

        // close and active buttons in the bottom right
        panel.add(getClose(), cc.xy(2, 7));
        panel.add(getActivate(), cc.xy(4, 7));

        this.add(panel);
        this.pack();
    }

    /**
     *
     * Packs and sets the location of the dialog
     * @param visible
     * 
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            pack();
            this.setLocation();
            if (theme != Settings.getInstance().getTheme()) {
                updateTheme();
            }
        }
        super.setVisible(visible);
    }

    /**
     * Uses the {@see DialogController#place(DropdownDialog)} method to position the dialog
     */
    public void setLocation() {
        controller.place((DropdownDialog) this);
    }

    /**
     * Access the default close button
     * @return
     */
    public JButton getClose() {
        return close;
    }

    /**
     * Sets the default close button
     * @param closeButton
     */
    public void setClose(JButton closeButton) {
        this.close = closeButton;
    }

    /**
     * Access the default active button (information loaded from {@see ActionProperties}) with the
     * provided type in constructor
     */
    public JButton getActivate() {
        return active;
    }

    /**
     * Sets the default activate button
     * @param runButton
     */
    public void setActive(JButton runButton) {
        this.active = runButton;
    }

    /**
     * Paints a shadow on the background to make it appear tucked under the tool-bar
     */
    public void paint(Graphics g) {
        super.paint(g); // probably better to paint on the main panel...
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(paint);
        g2.fillRect(0, 0, getPreferredSize().width, 10);
    }

    /**
     * Process the options in the dialog
     */
    public abstract void process();

    /**
     * Called on process finish this contain the calls to update the views
     * this method is automatically wrapped in a SwingUtils.invokeLater to be
     * thread safe
     */
    public abstract void update();
}
