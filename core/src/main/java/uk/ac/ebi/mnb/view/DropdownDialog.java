/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.Sizes;
import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.component.theme.Theme;
import uk.ac.ebi.caf.component.theme.ThemeManager;
import uk.ac.ebi.mnb.core.CloseDialogAction;
import uk.ac.ebi.mnb.core.ProcessDialogAction;
import uk.ac.ebi.mnb.interfaces.DialogController;
import uk.ac.ebi.mnb.interfaces.Updatable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


/**
 * ControllerDialog.java
 * A drop down modal dialog
 *
 * @author johnmay
 * @date Apr 27, 2011
 */
public abstract class DropdownDialog
        extends JDialog implements Updatable {

    private static final Logger LOGGER = Logger.getLogger(DropdownDialog.class);

    private JButton close;

    private JButton active;

    private Paint paint;

    private CellConstraints cc = new CellConstraints();

    private DialogController controller;


    public DropdownDialog(Window window) {
        this(window, ModalityType.APPLICATION_MODAL);
        
        if (window instanceof DialogController)
            setDialogController((DialogController) window);
    }

    public DropdownDialog(Window window, ModalityType modality) {
        super(window, modality);

        setUndecorated(true);

        // push focus back to the parent when the dialog is hidden
        if (getParent() != null) {
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentHidden(ComponentEvent e) {
                    getParent().requestFocusInWindow();
                }
            });
        }

        paint = new GradientPaint(0, 0, getBackground().darker(), 0, 10, getBackground());

    }


    @Deprecated
    public DropdownDialog(final JFrame frame,
                          DialogController controller,
                          String type) {

        this(frame, ModalityType.APPLICATION_MODAL);

        setDialogController(controller);

    }


    /**
     * Allows easy instantiation with a JFrame that implements DialogController.
     * If the frame does not implement DialogController then an InstantiationError
     * will be thrown
     *
     * @param frame
     * @param dialogName
     */
    @Deprecated
    public DropdownDialog(final JFrame frame,
                          String dialogName) {

        this(frame, ModalityType.APPLICATION_MODAL);

        setDialogController((DialogController) frame);


    }


    /**
     * Returns the dialog description label. By default the description is the Class name and should
     * be overridden to return a meaningful description of what the dialog does
     *
     * @return
     */
    public JLabel getDescription() {
        return LabelFactory.newLabel(getClass().getSimpleName(), LabelFactory.Size.LARGE);
    }


    /**
     * Returns the options/form section of the dialog. This method should be over-
     * ridden if use default layout
     *
     * @return
     */
    public JPanel getForm() {
        JPanel form = PanelFactory.createDialogPanel();
        form.setBackground(getBackground());
        return form;
    }


    public JPanel getNavigation() {

        JPanel navigation = PanelFactory.createDialogPanel("p:grow, right:min, 4dlu ,right:min",
                                                           "min");

        navigation.setBackground(getBackground());

        navigation.add(getClose(), cc.xy(2, 1));
        navigation.add(getActivate(), cc.xy(4, 1));


        return navigation;

    }


    /**
     * Sets the default layout of the dialog. Class wishing to use Default layout
     * should overrider getDescription and getForm. In addition to adding
     * values to the ActionProperties file.
     */
    public void setDefaultLayout() {

        Box box = Box.createVerticalBox();

        box.setBorder(Borders.DLU7_BORDER);

        JLabel label = getDescription();
        JPanel form = getForm();
        JPanel nav = getNavigation();

        // make sure all is aligned to the left
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.setAlignmentX(Component.LEFT_ALIGNMENT);

        int paddingPixels = Sizes.dialogUnitYAsPixel(4, box);

        box.add(Box.createVerticalStrut(paddingPixels));

        box.add(label);

        box.add(Box.createVerticalStrut(paddingPixels));
        box.add(new JSeparator(SwingConstants.HORIZONTAL));
        box.add(Box.createVerticalStrut(paddingPixels));

        box.add(form);

        box.add(Box.createVerticalStrut(paddingPixels));
        box.add(new JSeparator(SwingConstants.HORIZONTAL));
        box.add(Box.createVerticalStrut(paddingPixels));

        box.add(nav);

        this.setContentPane(box);

        getRootPane().setDefaultButton(active);

    }

    @Override
    public void pack() {
        super.pack();
        position();
    }

    /**
     * Access the close button for the dialog
     *
     * @return
     */
    public JButton getClose() {
        if (close == null)
            close = ButtonFactory.newButton(new CloseDialogAction(this));
        return close;
    }


    /**
     * Sets the default close button
     *
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
        if (active == null) {
            active = new JButton(new ProcessDialogAction(getClass(),
                                                         getClass().getSimpleName() + ".DialogButton",
                                                         this));

            // set the default option
            if (active.getText() == null || active.getText().isEmpty()) {
                active.setText("Okay");
            }

        }
        return active;

    }


    /**
     * Sets the default activate button
     *
     * @param runButton
     */
    public void setActive(JButton runButton) {
        this.active = runButton;
    }

    public void setDialogController(DialogController controller) {
        this.controller = controller;
    }

    public void position() {
        if (controller == null)
            throw new NullPointerException("No dialog controller has been set");
        controller.place(this);
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
     * Process method that allows access to the spinning dial wait indicator for example setting text.
     * This method is optional and simply invokes the {@see process()}
     *
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

    public void clear() {
        // do nothing
    }


}
