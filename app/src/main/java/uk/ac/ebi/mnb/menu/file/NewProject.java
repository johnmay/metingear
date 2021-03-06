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
package uk.ac.ebi.mnb.menu.file;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.lucene.queryParser.QueryParser;
import uk.ac.ebi.caf.component.ReplacementHandler;
import uk.ac.ebi.caf.component.SuggestionField;
import uk.ac.ebi.caf.component.SuggestionHandler;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.mdk.domain.entity.ReconstructionImpl;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.Taxonomy;
import uk.ac.ebi.mdk.domain.identifier.basic.ReconstructionIdentifier;
import uk.ac.ebi.mdk.service.query.taxonomy.TaxonomyQueryService;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.DropdownDialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * NewProjectDialog.java
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class NewProject extends DropdownDialog {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
                    NewProject.class);
    private boolean fieldsAreValid = true;
    private JTextField idField;
    private SuggestionField codeField;
    private SuggestionField taxonField;
    private SuggestionField nameField;
    private JTextField kingdomField;

    private TaxonomyQueryService service = new TaxonomyQueryService();

    public NewProject() {

        super(MainView.getInstance());


        service.setMaxResults(10);


        ReplacementHandler handler = new ReplacementHandler() {
            @Override
            public void replace(final JTextField field, final Object value) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        codeField.setSuggest(false);
                        taxonField.setSuggest(false);
                        nameField.setSuggest(false);
                        Taxonomy taxonomy = (Taxonomy) value;
                        if (idField.getText().isEmpty()) {
                            String[] nameTokens = taxonomy.getOfficialName().split("\\s+");
                            if (nameTokens.length > 1)
                                idField.setText("i" + nameTokens[0].substring(0, 1).toUpperCase() + nameTokens[1].substring(0, 2).toLowerCase() + "%n");
                            else
                                idField.setText("i" + taxonomy.getCode().toLowerCase());
                        }
                        codeField.setText(taxonomy.getCode());
                        taxonField.setText(taxonomy.getAccession());
                        kingdomField.setText(taxonomy.getKingdom().toString());
                        nameField.setText(taxonomy.getOfficialName());
                        codeField.setSuggest(true);
                        taxonField.setSuggest(true);
                        nameField.setSuggest(true);
                    }
                });
            }
        };

        idField = FieldFactory.newField(15);
        codeField = new SuggestionField(this, 5, 10, new SuggestionHandler() {

            @Override
            public ListCellRenderer getRenderer() {
                return new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        Taxonomy taxonomy = (Taxonomy) value;
                        this.setText(taxonomy.getCode());
                        this.setBackground(
                                isSelected ? list.getSelectionBackground()
                                           : list.getBackground());
                        this.setForeground(
                                isSelected ? list.getSelectionForeground()
                                           : list.getForeground());
                        return this;
                    }
                };
            }


            @Override
            public Collection<Object> getSuggestions(String s) {
                return service.startup()
                       ? new ArrayList<Object>(service.searchCode(s, true))
                       : new ArrayList<Object>();
            }
        }, handler);
        taxonField = new SuggestionField(this, 5, 10, new SuggestionHandler() {
            @Override
            public Collection<Object> getSuggestions(String s) {
                return service.startup()
                       ? new ArrayList<Object>(service.searchTaxonomyIdentifier(QueryParser.escape(s), true))
                       : Collections.emptyList();
            }
        }, handler);
        nameField = new SuggestionField(this, 35, 10, new SuggestionHandler() {

            @Override
            public ListCellRenderer getRenderer() {
                return new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        Taxonomy taxonomy = (Taxonomy) value;
                        this.setText(taxonomy.getOfficialName());
                        this.setBackground(
                                isSelected ? list.getSelectionBackground()
                                           : list.getBackground());
                        this.setForeground(
                                isSelected ? list.getSelectionForeground()
                                           : list.getForeground());
                        return this;
                    }
                };
            }

            @Override
            public Collection<Object> getSuggestions(String s) {
                return service.startup()
                       ? new ArrayList<Object>(service.searchName(QueryParser.escape(s.trim()), true))
                       : Collections.emptyList();
            }
        }, handler);
        kingdomField = FieldFactory.newField(10);

        setDefaultLayout();

    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Create a new reconstruction");
        return label;
    }

    @Override
    public JPanel getForm() {

        FormLayout layout = new FormLayout(
                "right:p, 4dlu, left:p",
                "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p");
        JPanel panel = super.getForm();
        panel.setLayout(layout);
        CellConstraints cc = new CellConstraints();

        panel.add(LabelFactory.newFormLabel("Internal Reconstruction Identifier:"), cc.xy(1, 1));
        panel.add(idField, cc.xy(3, 1));

        panel.add(LabelFactory.newFormLabel("Organism Code (e.g. ECOLI):"), cc.xy(1, 3));
        panel.add(codeField, cc.xy(3, 3));
        panel.add(LabelFactory.newFormLabel("Organism Name:"), cc.xy(1, 5));
        panel.add(nameField, cc.xy(3, 5));

        panel.add(LabelFactory.newFormLabel("Taxon Code:"), cc.xy(1, 7));
        panel.add(taxonField, cc.xy(3, 7));
        panel.add(LabelFactory.newFormLabel("Kingdom (e.g. B,E,V,A):"), cc.xy(1, 9));
        panel.add(kingdomField, cc.xy(3, 9));

        return panel;


    }



    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        // try and get a new service is the current one isn't avaialble
        if (!service.startup()) {
            service = new TaxonomyQueryService();
        }
    }

    @Override
    public void clear(){
        codeField.clear();
        taxonField.clear();
        nameField.clear();
        idField.setText("");
        kingdomField.setText("");
    }

    public String getCode() {
        String code = codeField.getText();
        // could check against db also
        if (code.isEmpty() || code.length() != 5) {
            flagAsInvalidInput("Organism code", code,
                               "five letter code for organism (e.g. MYTUB, SPEPQ)");
        }
        return code;
    }

    public Taxonomy.Kingdom getKingdom() {
        String type = kingdomField.getText();
        if (type.isEmpty()) {
            flagAsInvalidInput("Kingdom type", kingdomField.getText(),
                               "type of organism (A) Archea, (B) Bacteria, (V) Virus etc.");
            return Taxonomy.Kingdom.UNDEFINED;
        }
        return Taxonomy.Kingdom.getKingdom(type);


    }

    public String getOfficialName() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            flagAsInvalidInput("Official Name", name, "non empty name for organism");
        }
        return name;
    }

    public Integer getTaxon() {
        try {
            return Integer.parseInt(taxonField.getText());
        } catch (NumberFormatException ex) {
            flagAsInvalidInput("Taxon code", taxonField.getText(),
                               "five digit taxon code (e.g. 12342)");
            return 0;
        }
    }

    public String getProjectIdentifier() {
        if (idField.getText().isEmpty()) {
            return getOfficialName();
        }
        return idField.getText();
    }

    public void flagAsInvalidInput(String label, String found, String expected) {
        JOptionPane.showMessageDialog(this, label
                + " input '"
                + found
                + "' is not a valid. Expected '"
                + expected + "'!");
        fieldsAreValid = false;
    }

    @Override
    public void process() {
        fieldsAreValid = true;

        // create a new project
        int taxon = getTaxon();
        String code = getCode();
        Taxonomy.Kingdom kingdom = getKingdom();
        String name = getOfficialName();

        if (fieldsAreValid) {

            Taxonomy orgId = new Taxonomy(taxon, code, kingdom, name, name);
            ReconstructionIdentifier proId = new ReconstructionIdentifier(getProjectIdentifier());

            ReconstructionImpl proj = new ReconstructionImpl(UUID.randomUUID(), proId, orgId);
            DefaultReconstructionManager.getInstance().activate(proj);

        }
    }

    @Override
    public boolean update() {
        MainView.getInstance().getJMenuBar().updateContext();
        return MainView.getInstance().update();
    }
}
