/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
package uk.ac.ebi.mnb.dialog.tools.curate;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.component.list.MutableJListController;
import uk.ac.ebi.caf.component.theme.ThemeManager;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.MolecularFormula;
import uk.ac.ebi.mdk.domain.annotation.Source;
import uk.ac.ebi.mdk.domain.annotation.Synonym;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.identifier.type.ChemicalIdentifier;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.observation.Candidate;
import uk.ac.ebi.mdk.service.ServiceManager;
import uk.ac.ebi.mdk.service.query.data.MolecularChargeService;
import uk.ac.ebi.mdk.service.query.data.MolecularFormulaService;
import uk.ac.ebi.mdk.service.query.name.NameService;
import uk.ac.ebi.mdk.tool.resolve.ChemicalFingerprintEncoder;
import uk.ac.ebi.mdk.tool.resolve.NameCandidateFactory;
import uk.ac.ebi.mdk.ui.component.MetaboliteMatchIndication;
import uk.ac.ebi.mdk.ui.component.ResourceList;
import uk.ac.ebi.mdk.ui.component.table.MoleculeTable;
import uk.ac.ebi.mdk.ui.component.table.accessor.AccessionAccessor;
import uk.ac.ebi.mdk.ui.component.table.accessor.AnnotationAccess;
import uk.ac.ebi.mdk.ui.component.table.accessor.NameAccessor;
import uk.ac.ebi.mdk.ui.tool.annotation.CrossreferenceModule;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


/**
 * DatabaseSearch 2012.02.01
 *
 * @author johnmay
 * @author $Author$ (this version) <p/> Class description
 * @version $Rev$ : Last Changed $Date$
 */
public class DatabaseSearch
        implements CrossreferenceModule {

    private static final Logger LOGGER = Logger.getLogger(DatabaseSearch.class);

    private JComponent component;
    private MetaboliteMatchIndication match = new MetaboliteMatchIndication();
    private MoleculeTable table;
    private JTextField field;
    private JCheckBox approximate;
    private Metabolite context;
    private ServiceManager serviceManager;
    private ResourceList resourceList = new ResourceList();
    private final UndoManager undoManager;

    private Timer timer = new Timer(500, new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            updateTable(field.getText());
            timer.stop();
        }
    });


    public DatabaseSearch(ServiceManager serviceManager, UndoManager undoManager) {

        this.serviceManager = serviceManager;
        this.undoManager = undoManager;

        component = PanelFactory.createDialogPanel();
        component.setLayout(new FormLayout("p", "p, 4dlu, p"));

        field = FieldFactory.newField(25);
        approximate = new JCheckBox("Approximate");
        table = new MoleculeTable(new NameAccessor(),
                                  new AccessionAccessor(),
                                  new AnnotationAccess(new Source()));
        table.setPreferredSize(new Dimension(300, 185));
        table.setBackground(component.getBackground());
        table.setSelectionBackground(component.getBackground().brighter());
        table.setSelectionForeground(ThemeManager.getInstance().getTheme().getForeground());


        field.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                timer.restart();
            }


            public void removeUpdate(DocumentEvent e) {
                timer.restart();
            }


            public void changedUpdate(DocumentEvent e) {
                timer.restart();
            }
        });
        approximate.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                timer.restart();
            }
        });
        field.addFocusListener(new FocusAdapter() {

            public void focusGained(FocusEvent e) {
                timer.restart();
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                Collection<Metabolite> entites = table.getSelectedEntities();
                if (entites.isEmpty()) {
                    return;
                }
                Metabolite m = entites.iterator().next();
                match.setSubject(m);
            }
        });


        CellConstraints cc = new CellConstraints();
        component.add(match.getComponent(), cc.xy(1, 1, cc.CENTER, cc.CENTER)); // visual inspector

        component.add(getSearchOptions(), cc.xy(1, 3, cc.CENTER, cc.CENTER)); // search options


    }


    public String getDescription() {
        return "Database Search";
    }


    public JComponent getComponent() {


        return component;

    }


    public final JComponent getSearchOptions() {

        JComponent options = Box.createHorizontalBox();
        JComponent config = PanelFactory.createDialogPanel("p:grow, 4dlu, p:grow",
                                                           "p, 4dlu, p, 4dlu, p, 4dlu, p");

        options.add(config); // search box and database selection

        JScrollPane pane = new JScrollPane(getCandidateTable());
        pane.setBorder(Borders.EMPTY_BORDER);
        pane.setPreferredSize(new Dimension(300, 185));
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        options.add(pane); // display candidates

        CellConstraints cc = new CellConstraints();

        config.add(LabelFactory.newFormLabel("Query"), cc.xy(1, 1));
        config.add(field, cc.xy(3, 1));
        config.add(approximate, cc.xyw(1, 3, 3));
        config.add(new MutableJListController(resourceList).getListWithController(), cc.xyw(1, 5, 3));
        config.add(ButtonFactory.newButton(new AbstractAction("Assign") {

            public void actionPerformed(ActionEvent e) {
                transferAnnotations();
            }
        }), cc.xy(1, 7));


        return options;
    }


    public MoleculeTable getCandidateTable() {

        return table;

    }


    public void updateTable(String name) {

        if (name.isEmpty() || !component.isVisible()) {
            return;
        }

        match.setQueryName(name);

        Identifier identifier = resourceList.getSelectedValue();

        if (identifier == null) {
            if (!resourceList.getModel().isEmpty()) {
                identifier = resourceList.getElements().get(0);
                if (identifier == null)
                    return;
            }
        }

        NameService service = serviceManager.getService(identifier,
                                                        NameService.class);

        System.out.println(service);


        NameCandidateFactory factory = new NameCandidateFactory(new ChemicalFingerprintEncoder(),
                                                                service);

        Set<Candidate> candidates = factory.getCandidates(name, approximate.isSelected());

        System.out.println(candidates);

        List<Metabolite> metabolites = new ArrayList<Metabolite>();

        for (Candidate candidate : candidates) {

            Metabolite m = factory.convertToMetabolite(DefaultEntityFactory.getInstance(), candidate);
            if (serviceManager.hasService(candidate.getIdentifier(),
                                          MolecularChargeService.class)) {
                m.setCharge(serviceManager.getService(candidate.getIdentifier(),
                                                      MolecularChargeService.class).getCharge(m.getIdentifier()));

            }
            if (serviceManager.hasService(candidate.getIdentifier(),
                                          MolecularFormulaService.class)) {
                String mf = serviceManager.getService(candidate.getIdentifier(),
                                                      MolecularFormulaService.class).getMolecularFormula(m.getIdentifier());
                m.addAnnotation(new MolecularFormula(mf));

            }
            metabolites.add(m);

        }


        getCandidateTable().getModel().set(metabolites);


    }


    public void setup(Metabolite metabolite) {

        field.setText(metabolite.getName());

        match.setQuery(metabolite);

        this.context = metabolite;

        DefaultListModel model = resourceList.getModel();
        model.removeAllElements();
        List<Identifier> available = new ArrayList<Identifier>();
        for (Identifier identifier : serviceManager.getIdentifiers(NameService.class)) {
            // only add those services which are available
            if (serviceManager.hasService(identifier, NameService.class)
                    && identifier instanceof ChemicalIdentifier) {
                available.add(identifier);
            }
        }
        Collections.sort(available, new Comparator<Identifier>() {
            @Override public int compare(Identifier o1, Identifier o2) {
                // sort by service type local >> remote
                int cmp = serviceManager.getService(o1, NameService.class).getServiceType().compareTo(serviceManager.getService(o2, NameService.class).getServiceType());
                if (cmp != 0) return cmp;
                // sort by name
                return o1.getShortDescription().compareTo(o2.getShortDescription());
            }
        });

        component.revalidate();

    }


    public boolean canTransferAnnotations() {
        return true;
    }


    public void transferAnnotations() {

        for (Metabolite m : table.getSelectedEntities()) {

            if (!context.getName().equals(m.getName())) {
                Annotation synonym = new Synonym(m.getName());
                undoManager.addEdit(new AddAnnotationEdit(context, synonym));
                context.addAnnotation(synonym);
            }

            Annotation xref = CrossReference.create(m.getIdentifier());
            undoManager.addEdit(new AddAnnotationEdit(context, xref));
            context.addAnnotation(xref);

        }

    }
}
