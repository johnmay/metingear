package uk.ac.ebi.metingear.view;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.ActionProperties;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.component.injection.AbstractComponentInjector;
import uk.ac.ebi.caf.component.injection.Inject;
import uk.ac.ebi.caf.component.injection.PropertyComponentInjector;
import uk.ac.ebi.mnb.interfaces.DialogController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public abstract class AbstractProcessingDialog
        extends JDialog
        implements ProcessingDialog {

    private static final Logger LOGGER = Logger.getLogger(AbstractProcessingDialog.class);

    private DialogController controller;

    @Inject
    private JButton close;

    @Inject
    private JButton okay;

    @Inject
    private JLabel     information;
    private JComponent form;
    private JComponent navigation;

    private AbstractComponentInjector INJECTOR;

    private static final CellConstraints CELL_CONSTRAINTS = new CellConstraints();

    public AbstractProcessingDialog(final Window window) {
        super(window, ModalityType.APPLICATION_MODAL);

        setUndecorated(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                window.requestFocusInWindow();
            }
        });

    }

    private final void inject() {
        prepareInjector();
        INJECTOR.inject(this);
    }


    private void prepareInjector() {
        // inject from property component injector using action.properties
        if (INJECTOR == null)
            INJECTOR = new PropertyComponentInjector(ActionProperties.getInstance());
    }

    public void prepare() {
        // do nothing
    }

    public void process() {
        // do nothing
    }

    /**
     * Set whether the "okay"/"process" button is enabled. Allows subclasses
     * to inhibit processing without a complete form.
     * @param enabled whether to enable the button
     */
    public void setEnabled(boolean enabled){
        okay.setEnabled(enabled);
    }

    public void initialiseLayout() {

        JPanel panel = PanelFactory.createDialogPanel("p:grow, p, p:grow",
                                                      "p, 4dlu, p, 4dlu, p, 4dlu, p");

        panel.add(getInformation(), CELL_CONSTRAINTS.xyw(1, 1, 3));
        panel.add(new JSeparator(), CELL_CONSTRAINTS.xyw(1, 3, 3));
        panel.add(getForm(), CELL_CONSTRAINTS.xy(2, 5));

        // close and active buttons in the bottom right
        panel.add(getNavigation(), CELL_CONSTRAINTS.xyw(1, 7, 3));

        getRootPane().setBorder(Borders.DLU7_BORDER);
        setContentPane(panel);

        getRootPane().setDefaultButton(getOkayButton());

        inject();

    }

    public void position() {
        controller.place(this);
    }

    // default component

    public final JComponent getInformation() {
        if (information == null) {
            information = createInformation();
        }
        return information;
    }

    public final JComponent getForm() {
        if (form == null) {
            form = createForm();
        }
        return form;
    }

    public final JComponent getNavigation() {
        if (navigation == null) {
            navigation = createNavigation();
        }
        return navigation;
    }

    // navigation buttons

    public final JButton getCloseButton() {
        if (close == null) {
            close = createCloseButton();
        }
        return close;
    }

    public final JButton getOkayButton() {
        if (okay == null) {
            okay = createOkayButton();
        }
        return okay;
    }

    public void setDialogController(DialogController controller) {
        this.controller = controller;
    }

    /* creation methods */

    public JLabel createInformation() {
        return LabelFactory.newLabel("Default Description");
    }

    public JComponent createForm() {
        return new JPanel();
    }

    public JComponent createNavigation() {

        JPanel navigation = new JPanel(new FormLayout("p:grow, right:min, 4dlu ,right:min",
                                                      "p"));

        navigation.add(getCloseButton(), CELL_CONSTRAINTS.xy(2, 1));
        navigation.add(getOkayButton(), CELL_CONSTRAINTS.xy(4, 1));

        return navigation;

    }

    private JButton createOkayButton() {
        return ButtonFactory.newButton(new AbstractAction("Okay") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread t = new Thread(new Runnable() {

                    public void run() {
                        try {
                            process();

                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    setVisible(false);
                                    update();
                                }

                            });
                        } catch (Exception e) {
                            e.printStackTrace();
//                            (new ErrorMessage("An error occurred: " + e.getMessage() + e.getCause()));
                        }
                    }
                });
                t.setName(getClass().getSimpleName() + "-PROCESSING");
                t.start();
            }
        });
    }

    private JButton createCloseButton() {
        return ButtonFactory.newButton(new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
    }

    // background shaddow

    private Paint paint = new GradientPaint(0, 0, getBackground().darker(), 0, 10, getBackground());


    public void paint(Graphics g) {
        super.paint(g); // probably better to paint on the main panel...
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(paint);
        g2.fillRect(0, 0, getPreferredSize().width, 10);
    }


    public final JLabel getLabel(Class c, String name) {

        prepareInjector();

        JLabel label = LabelFactory.newFormLabel(name, "no information injected");

        INJECTOR.inject(c, label, name);

        return label;

    }

    public final JLabel getLabel(String name) {
        return getLabel(getClass(), name);
    }


}
