package uk.ac.ebi.mnb.main;

import com.google.common.collect.Multimap;
import com.jgoodies.forms.layout.CellConstraints;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.component.factory.PreferencePanelFactory;
import uk.ac.ebi.caf.component.theme.ComponentPreferences;
import uk.ac.ebi.caf.utility.preference.Preference;
import uk.ac.ebi.render.resource.ResourcePanel;


/**
 *          PreferencePanel 2012.02.16 <br/>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class PreferencePanel extends Box {

    private static final Logger LOGGER = Logger.getLogger(PreferencePanel.class);

    private JPanel options = PanelFactory.createInfoPanel();


    public PreferencePanel() {

        super(BoxLayout.X_AXIS);

        JPanel category = PanelFactory.createInfoPanel("10dlu, right:min, 4dlu",
                                                       "p:grow, 10dlu, p, 10dlu, p, 10dlu, p, 10dlu, p:grow");

        CellConstraints cc = new CellConstraints();
        setOpaque(true);
        setBackground(Color.WHITE);

        category.setBackground(new Color(234, 237, 243));
        category.setOpaque(true);


        category.add(ButtonFactory.newCleanButton(new AbstractAction("Resources") {

            public void actionPerformed(ActionEvent e) {
                ((CardLayout) options.getLayout()).show(options, "Resources");
            }
        }), cc.xy(2, 3));
        category.add(ButtonFactory.newCleanButton(new AbstractAction("Rendering") {

            public void actionPerformed(ActionEvent e) {
                ((CardLayout) options.getLayout()).show(options, "Rendering");
            }
        }), cc.xy(2, 5));


        add(category);

        options.setLayout(new CardLayout());
        options.setBackground(Color.WHITE);

        Multimap<String, Preference> map = ComponentPreferences.getInstance().getCategoryMap();

        options.add(PreferencePanelFactory.getPreferencePanel(map.get("Rendering")), "Rendering");
        options.add(new ResourcePanel(), "Resources");



        add(options);
        add(Box.createHorizontalGlue());

    }
}
