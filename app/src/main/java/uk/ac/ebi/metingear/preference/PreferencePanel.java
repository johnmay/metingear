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

package uk.ac.ebi.metingear.preference;

import com.explodingpixels.macwidgets.plaf.EmphasizedLabelUI;
import com.explodingpixels.painter.GradientWithBorderPainter;
import com.google.common.collect.Multimap;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.component.factory.PreferencePanelFactory;
import uk.ac.ebi.caf.component.theme.ComponentPreferences;
import uk.ac.ebi.caf.component.theme.Theme;
import uk.ac.ebi.caf.component.theme.ThemeManager;
import uk.ac.ebi.caf.utility.preference.Preference;
import uk.ac.ebi.mdk.ResourcePreferences;
import uk.ac.ebi.mdk.domain.DefaultIdentifierFactory;
import uk.ac.ebi.mdk.domain.DomainPreferences;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.service.ServicePreferences;
import uk.ac.ebi.mdk.service.loader.crossreference.ChEBICrossReferenceLoader;
import uk.ac.ebi.mdk.service.loader.crossreference.UniProtCrossReferenceLoader;
import uk.ac.ebi.mdk.service.loader.data.ChEBIDataLoader;
import uk.ac.ebi.mdk.service.loader.location.DefaultLocationFactory;
import uk.ac.ebi.mdk.service.loader.multiple.HMDBMetabocardsLoader;
import uk.ac.ebi.mdk.service.loader.multiple.HMDBXMLLoader;
import uk.ac.ebi.mdk.service.loader.multiple.KEGGCompoundLoader;
import uk.ac.ebi.mdk.service.loader.multiple.LipidMapsLoader;
import uk.ac.ebi.mdk.service.loader.multiple.MetaCycCompoundLoader;
import uk.ac.ebi.mdk.service.loader.name.ChEBINameLoader;
import uk.ac.ebi.mdk.service.loader.single.TaxonomyLoader;
import uk.ac.ebi.mdk.service.loader.structure.ChEBIStructureLoader;
import uk.ac.ebi.mdk.service.loader.structure.HMDBStructureLoader;
import uk.ac.ebi.mdk.service.loader.structure.KEGGCompoundStructureLoader;
import uk.ac.ebi.mdk.service.loader.structure.LipidMapsSDFLoader;
import uk.ac.ebi.mdk.service.loader.structure.MetaCycStructureLoader;
import uk.ac.ebi.mdk.ui.component.service.LoaderGroupFactory;
import uk.ac.ebi.metingear.Main;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;


/**
 * PreferencePanel 2012.02.16 <br/> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class PreferencePanel extends JPanel {

    private static final Logger LOGGER = Logger
            .getLogger(PreferencePanel.class);

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
        model.addElement("General");
        model.addElement("Rendering");
        model.addElement("Tools");
        model.addElement("Databases");


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
        add(bottomfill, cc
                .xy(1, 3, CellConstraints.FILL, CellConstraints.FILL));


        category.setMaximumSize(new Dimension(50, 2000));

        options.setLayout(new CardLayout());
        options.setBackground(Color.WHITE);

        Multimap<String, Preference> map = ComponentPreferences.getInstance()
                                                               .getCategoryMap();

        options.add(new CenteringPanel(new GeneralPanel()), "General");
        options.add(new CenteringPanel(PreferencePanelFactory
                                               .getPreferencePanel(map.get("Rendering"))), "Rendering");
        options.add(new CenteringPanel(new ResourceLoading(window)), "Resources");
        options.add(new CenteringPanel(new Tools()), "Tools");


        add(new JScrollPane(options), cc.xywh(2, 1, 1, 3));


        category.setSelectedValue("Resources", true);


    }

    private class GeneralPanel extends Box {
        private GeneralPanel() {
            super(BoxLayout.PAGE_AXIS);

            DomainPreferences domainPref = DomainPreferences.getInstance();
            ServicePreferences servicePref = ServicePreferences.getInstance();
            ResourcePreferences resourcePref = ResourcePreferences
                    .getInstance();
            JLabel label = LabelFactory
                    .newLabel("Saving", LabelFactory.Size.HUGE);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            add(DefaultComponentFactory.getInstance().createSeparator(label));
            add(PreferencePanelFactory.getPreferencePanel(domainPref
                                                                  .getPreference("SAVE_LOCATION")));
            add(PreferencePanelFactory.getPreferencePanel(resourcePref
                                                                  .getPreference("IDENTIFIERS_DOT_ORG_URL")));

            JLabel proxyLabel = LabelFactory
                    .newLabel("HTTP Proxy", LabelFactory.Size.HUGE);
            proxyLabel.setHorizontalAlignment(SwingConstants.LEFT);
            add(DefaultComponentFactory.getInstance()
                                       .createSeparator(proxyLabel));
            add(PreferencePanelFactory
                        .getPreferencePanel(servicePref
                                                    .getPreference("PROXY_SET"),
                                            servicePref
                                                    .getPreference("PROXY_HOST"),
                                            servicePref
                                                    .getPreference("PROXY_PORT")));


        }
    }

    private class CenteringPanel extends JPanel {
        public CenteringPanel(JComponent child) {
            super(new FormLayout("p:grow", "p:grow, p, p:grow"));
            setOpaque(false);
            setBorder(Borders.DLU14_BORDER);
            add(child, new CellConstraints(1, 2));
        }
    }

    private class Tools extends Box {

        public Tools() {

            super(BoxLayout.PAGE_AXIS);

            DomainPreferences CORE = DomainPreferences.getInstance();
            ResourcePreferences RESOURCE = ResourcePreferences.getInstance();
            JLabel label = LabelFactory
                    .newLabel("NCBI-BLAST+", LabelFactory.Size.HUGE);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            add(DefaultComponentFactory.getInstance().createSeparator(label));
            add(PreferencePanelFactory
                        .getPreferencePanel(CORE.getPreference("BLASTP_PATH"),
                                            CORE.getPreference("BLASTP_VERSION"),
                                            RESOURCE.getPreference("BLAST_DB_ROOT")));


        }

    }


    private class ResourceLoading extends Box {

        public ResourceLoading(final Window window) {
            super(BoxLayout.PAGE_AXIS);

            LoaderGroupFactory factory = new LoaderGroupFactory(window, DefaultLocationFactory
                    .getInstance());
            try {

                add(Box.createGlue());
                JTextArea area = new JTextArea();
                area.setLineWrap(true);
                area.setEditable(false);
                area.setOpaque(false);
                area.setEnabled(false);
                Theme theme = ThemeManager.getInstance().getTheme();
                area.setForeground(theme.getForeground());
                area.setFont(theme.getBodyFont());
                area.setWrapStyleWord(true);
                area.setBorder(Borders.EMPTY_BORDER);
                area.setText("Load resources from remote and local locations. When a resource is not publically available the download is very large or the location is unreachable " +
                                     "you will not be able to update it. If a resource can not be updated (non-bold arrow) you will " +
                                     "have to configure the loader by specifying local or remove locations of required resources. " +
                                     "Each loader can be configured by clicking the 'gear' icon and entering the required locations. " +
                                     "A description of location is available by hovering over the location name.");
                area.setBackground(Color.WHITE);
                add(Box.createVerticalStrut(5));
                add(PreferencePanelFactory
                            .getPreferenceEditor(ServicePreferences
                                                         .getInstance()
                                                         .getPreference("SERVICE_ROOT"),
                                                 new AbstractAction() {
                                                     @Override
                                                     public void actionPerformed(ActionEvent e) {
                                                         int choice = JOptionPane
                                                                 .showConfirmDialog(window,
                                                                                    "<html>You have changed the storage location of services and you must <br/> " +
                                                                                            "restart Metingear before updating any service data.<br/><br/>" +
                                                                                            "Would you like to restart now? <br/>" +
                                                                                            "If you wish to continue editing, please select 'No'." +
                                                                                            "</html>", "Restart Required", JOptionPane.YES_NO_OPTION);
                                                         if (choice == JOptionPane.OK_OPTION) {
                                                             try {
                                                                 Main.relaunch();
                                                             } catch (Exception e1) {
                                                                 JOptionPane
                                                                         .showMessageDialog(window, "Unable to restart the application! Please restart manually.", "Error", JOptionPane.ERROR_MESSAGE);
                                                             }
                                                         }
                                                     }
                                                 }));
                add(Box.createVerticalStrut(5));
                add(area);
                add(Box.createVerticalStrut(5));
                add(factory.createGroup("ChEBI",
                                        new ChEBIStructureLoader(),
                                        new ChEBINameLoader(),
                                        new ChEBIDataLoader(),
                                        new ChEBICrossReferenceLoader()));
                add(Box.createVerticalStrut(5));
                add(factory.createGroup("KEGG",
                                        new KEGGCompoundLoader(),
                                        new KEGGCompoundStructureLoader()));
                add(Box.createVerticalStrut(5));
                add(factory.createGroup("BioCyc",
                                        new MetaCycCompoundLoader(),
                                        new MetaCycStructureLoader()));
                add(Box.createVerticalStrut(5));
                add(factory.createGroup("LIPID MAPS",
                                        new LipidMapsLoader(),
                                        new LipidMapsSDFLoader()));
                add(Box.createVerticalStrut(5));
                add(factory.createGroup("HMDB",
                                        new HMDBXMLLoader(),
                                        new HMDBStructureLoader(),
                                        new HMDBMetabocardsLoader()));
                add(Box.createVerticalStrut(5));
                add(factory.createGroup("UniProt",
                                        new TaxonomyLoader(),
                                        new UniProtCrossReferenceLoader(DefaultEntityFactory
                                                                                .getInstance(),
                                                                        DefaultIdentifierFactory
                                                                                .getInstance())));
                add(Box.createGlue());
                add(Box.createVerticalStrut(5));

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
        GradientWithBorderPainter painter = new GradientWithBorderPainter(topLineColor, bottomColor, topColor, bottomColor);

        private JPanel selected = new JPanel(new FormLayout("p:grow, center:p, 20px", "p:grow, center:p, p:grow")) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.
                painter.paint((Graphics2D) g, this, getWidth(), getHeight());
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
