package uk.ac.ebi.mnb.main;

import com.google.common.collect.Multimap;
import com.jgoodies.forms.layout.CellConstraints;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.*;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.component.factory.PreferencePanelFactory;
import uk.ac.ebi.caf.component.theme.ComponentPreferences;
import uk.ac.ebi.caf.utility.preference.Preference;
import uk.ac.ebi.chemet.service.loader.single.TaxonomyLoader;
import uk.ac.ebi.chemet.service.loader.structure.ChEBIStructureLoader;
import uk.ac.ebi.chemet.service.loader.structure.HMDBStructureLoader;
import uk.ac.ebi.chemet.service.loader.structure.KEGGCompoundStructureLoader;
import uk.ac.ebi.render.resource.LoaderRow;
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


    public PreferencePanel(final Window window) {

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
        options.add(new ResourceLoading(window), "Resources");



        add(options);
        add(Box.createHorizontalGlue());

    }
    
    
    private class ResourceLoading extends JPanel {
        
        public ResourceLoading(final Window window){
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            try{
                System.out.println("Creating resource loader panel");
                add(LabelFactory.newFormLabel("Taxonomy"));
                add(new LoaderRow(new TaxonomyLoader(), window));
                add(LabelFactory.newFormLabel("Chemical Structures"));
                add(new JSeparator());
                add(new LoaderRow(new ChEBIStructureLoader(),window));
                add(new LoaderRow(new KEGGCompoundStructureLoader(),window));
                add(new LoaderRow(new HMDBStructureLoader(),window));
            }catch (IOException ex){
                LOGGER.error("Something's wrong with the index");
            }
        }
        
    }
    
}
