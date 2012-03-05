package uk.ac.ebi.metingear.pref;

import com.explodingpixels.macwidgets.plaf.EmphasizedLabelUI;
import com.explodingpixels.painter.FocusStatePainter;
import com.explodingpixels.painter.GradientWithBorderPainter;
import com.google.common.collect.Multimap;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.component.factory.PreferencePanelFactory;
import uk.ac.ebi.caf.component.theme.ComponentPreferences;
import uk.ac.ebi.caf.component.theme.Theme;
import uk.ac.ebi.caf.component.theme.ThemeManager;
import uk.ac.ebi.caf.utility.ColorUtility;
import uk.ac.ebi.caf.utility.preference.Preference;
import uk.ac.ebi.chemet.service.loader.crossreference.ChEBICrossReferenceLoader;
import uk.ac.ebi.chemet.service.loader.data.ChEBIDataLoader;
import uk.ac.ebi.chemet.service.loader.location.DefaultLocationFactory;
import uk.ac.ebi.chemet.service.loader.multiple.HMDBMetabocardsLoader;
import uk.ac.ebi.chemet.service.loader.multiple.KEGGCompoundLoader;
import uk.ac.ebi.chemet.service.loader.name.ChEBINameLoader;
import uk.ac.ebi.chemet.service.loader.single.TaxonomyLoader;
import uk.ac.ebi.chemet.service.loader.structure.ChEBIStructureLoader;
import uk.ac.ebi.chemet.service.loader.structure.HMDBStructureLoader;
import uk.ac.ebi.chemet.service.loader.structure.KEGGCompoundStructureLoader;
import uk.ac.ebi.render.resource.LoaderGroupFactory;


/**
 * PreferencePanel 2012.02.16 <br/>
 * Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class PreferencePanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(PreferencePanel.class);

    private JPanel options = PanelFactory.createInfoPanel();

    public PreferencePanel(final Window window) {


        setLayout(new FormLayout("p, p:grow", "p:grow, p, p:grow"));

        DefaultListModel model = new DefaultListModel();
        final JList category = new JList(model);

        CellConstraints cc = new CellConstraints();
        
        setBackground(Color.WHITE);

        category.setBackground(new Color(234, 237, 243));
        category.setPreferredSize(new Dimension(200, 150));
        category.setFixedCellHeight(40);
        category.setCellRenderer(new MyListRenderer());

        model.addElement("Resources");
        model.addElement("Rendering");
        

        category.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                CardLayout layout = (CardLayout) options.getLayout();
                layout.show(options, (String) category.getSelectedValue());
            }
        });

        JPanel topfill = new JPanel();
        JPanel bottomfill = new JPanel();
        topfill.setBackground(new Color(234, 237, 243));
        bottomfill.setBackground(new Color(234, 237, 243));

        add(topfill, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
        add(category, cc.xy(1, 2, CellConstraints.FILL, CellConstraints.FILL));
        add(bottomfill, cc.xy(1, 3, CellConstraints.FILL, CellConstraints.FILL));


        category.setMaximumSize(new Dimension(50, 2000));

        options.setLayout(new CardLayout());
        options.setBackground(Color.WHITE);

        Multimap<String, Preference> map = ComponentPreferences.getInstance().getCategoryMap();

        options.add(new CenteringPanel(PreferencePanelFactory.getPreferencePanel(map.get("Rendering"))), "Rendering");
        options.add(new CenteringPanel(new ResourceLoading(window)), "Resources");


        add(options, cc.xywh(2, 1, 1, 3));


        category.setSelectedValue("Resources", true);


    }
    
    private class CenteringPanel extends JPanel {
        public CenteringPanel(JComponent child){
            super(new FormLayout("p:grow", "p:grow, p, p:grow"));
            setOpaque(false);
            setBorder(Borders.DLU14_BORDER);
            add(child, new CellConstraints(1,2));
        }
    }


    private class ResourceLoading extends Box {

        public ResourceLoading(final Window window) {
            super(BoxLayout.PAGE_AXIS);

            LoaderGroupFactory factory = new LoaderGroupFactory(window, DefaultLocationFactory.getInstance());
            try {

                add(Box.createHorizontalStrut(50));
                add(Box.createGlue());
                add(Box.createHorizontalStrut(50));
                add(factory.createGroup("Miscellaneous",
                                            new TaxonomyLoader()));
                add(Box.createHorizontalStrut(50));
                add(factory.createGroup("ChEBI",
                                            new ChEBIStructureLoader(),
                                            new ChEBINameLoader(),
                                            new ChEBIDataLoader(),
                                            new ChEBICrossReferenceLoader()));
                add(Box.createHorizontalStrut(50));
                add(factory.createGroup("KEGG",
                                            new KEGGCompoundLoader(),
                                            new KEGGCompoundStructureLoader()));
                add(Box.createHorizontalStrut(50));
                add(factory.createGroup("HMDB",
                                            new HMDBMetabocardsLoader(),
                                            new HMDBStructureLoader()));
                add(Box.createGlue());
                add(Box.createHorizontalStrut(50));

                CellConstraints cc = new CellConstraints();

            } catch (IOException ex) {
                LOGGER.error("Something's wrong with the index");
            }
        }

    }

    class MyListRenderer extends JLabel implements ListCellRenderer {

        private JPanel unselected = new JPanel(new FormLayout("p:grow, center:p, 20px", "p:grow, center:p, p:grow"));
        private JLabel label = new JLabel();
        private CellConstraints cc = new CellConstraints();

        Color topLineColor = new Color(0x4580c8);
        Color topColor = new Color(0x5d94d6);
        Color bottomColor = new Color(0x1956ad);
        GradientWithBorderPainter painter =  new GradientWithBorderPainter(topLineColor, bottomColor, topColor, bottomColor);

        private JPanel selected = new JPanel(new FormLayout("p:grow, center:p, 20px", "p:grow, center:p, p:grow")){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.
                painter.paint((Graphics2D)g, this, getWidth(), getHeight());
            }
        };

        public MyListRenderer() {
            unselected.setOpaque(false);
            selected.setBackground(Color.BLACK);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            Theme theme = ThemeManager.getInstance().getTheme();
            label.setUI(new EmphasizedLabelUI());
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = isSelected ? selected : unselected;
            label.setText(value.toString());
            panel.add(label, cc.xy(2, 2));
            return panel;
        }
    }

}
