/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view;

import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import net.sf.furbelow.SpinningDialWaitIndicator;

import java.awt.*;
import java.awt.Dialog.ModalityType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import uk.ac.ebi.mnb.interfaces.DialogController;
import uk.ac.ebi.caf.component.theme.Theme;
import uk.ac.ebi.mnb.interfaces.Updatable;
import uk.ac.ebi.mnb.settings.Settings;

import javax.swing.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import uk.ac.ebi.mnb.core.CloseDialogAction;
import uk.ac.ebi.mnb.core.ProcessDialogAction;


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
        extends JDialog implements Updatable {

    private static final Logger LOGGER = Logger.getLogger(DropdownDialog.class);

    private JButton close;

    private JButton active;

    private DialogController controller;

    private Theme theme = Settings.getInstance().getTheme();

    private Paint paint = new GradientPaint(0, 0, getBackground().darker(), 0, 10, getBackground());

    private CellConstraints cc = new CellConstraints();

    private static final Set<String> GENERIC_DIALOGS = new HashSet<String>(Arrays.asList("OkayDialog",
                                                                                         "SaveDialog",
                                                                                         "RunDialog"));

    public DropdownDialog(JFrame frame,
                          DialogController controller,
                          String type) {

        super(frame, ModalityType.APPLICATION_MODAL);

        this.controller = controller;

        close = new JButton(new CloseDialogAction(this));

        active = new JButton(new ProcessDialogAction(getClass(),
                                                     type + ".DialogButton", this));

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
        active = GENERIC_DIALOGS.contains(dialogName)
                 ? new JButton(new ProcessDialogAction(dialogName + ".DialogButton", this))
                 : new JButton(new ProcessDialogAction(getClass(), dialogName + ".DialogButton", this));
        setUndecorated(true);
    }


    /**
     * Returns the dialog description label. By default the description is the Class name and should
     * be overridden to return a meaningful description of what the dialog does
     * @return
     */
    public JLabel getDescription() {
        return LabelFactory.newLabel(getClass().getSimpleName(), LabelFactory.Size.LARGE);
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

        JPanel options = PanelFactory.createDialogPanel();

        return options;

    }


    public JPanel getNavigation() {

        JPanel navigation = PanelFactory.createDialogPanel("p:grow, right:min, 4dlu ,right:min",
                                                           "p");

        navigation.add(getClose(), cc.xy(2, 1));
        navigation.add(getActivate(), cc.xy(4, 1));


        return navigation;

    }


    /**
     * Sets the default layout of the dialog. Class wishing to use Default layout
     * should overrider getDescription and getOptions. In addition to adding
     * values to the ActionProperties file.
     */
    public void setDefaultLayout() {

        JPanel panel = PanelFactory.createDialogPanel("p:grow",
                                                      "p, 4dlu, p, 4dlu, p, 4dlu, p");

        panel.setBorder(Borders.DLU7_BORDER);

        panel.add(getDescription(), cc.xy(1, 1));
        panel.add(new JSeparator(SwingConstants.HORIZONTAL), cc.xy(1, 3));
        panel.add(getOptions(), cc.xy(1, 5));

        // close and active buttons in the bottom right
        panel.add(getNavigation(), cc.xy(1, 7));

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
            this.pack();
        }
        super.setVisible(visible);

        if (!visible) {
            // return focus to parent
            getParent().requestFocusInWindow();
        }
    }


    /**
     * Uses the {@see DialogController#place(DropdownDialog)} method to position the dialog
     */
    public void setLocation() {
        controller.place((DropdownDialog) this);
    }


    @Override
    public void pack() {
        super.pack();
        setLocation();
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
     * Draws the dialog to an image (experimental)
     */
    public void drawDialog() {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D g2 = (Graphics2D) img.createGraphics();
        super.setLocation(0, 0);
        super.setVisible(true);
        super.setVisible(false);
        g2.dispose();
        try {
            ImageIO.write(img, "png", new File("/Users/johnmay/Desktop/dialog.png"));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(DropdownDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * Allows access to the spinning dial wait indicator for example setting text
     * @param waitIndicator
     */
    public void process(final SpinningDialWaitIndicator waitIndicator) {
        process();
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
    public abstract boolean update();
}
