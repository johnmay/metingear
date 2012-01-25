/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.mnb.io.TaxonomyMap;
import uk.ac.ebi.resource.organism.Kingdom;
import uk.ac.ebi.resource.organism.Taxonomy;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.resource.ReconstructionIdentifier;
import uk.ac.ebi.caf.component.factory.FieldFactory;

/**
 * NewProjectDialog.java
 *
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
    private JTextField codeField;
    private JTextField taxonField;
    private JTextField nameField;
    private JTextField kingdomField;

    public NewProject() {

        super(MainView.getInstance(), "NewProject");

        idField = FieldFactory.newField(15);
        codeField = FieldFactory.newField(5);
        taxonField = FieldFactory.newField(5);
        nameField = FieldFactory.newField(20);
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
    public JPanel getOptions() {

        FormLayout layout = new FormLayout(
                "right:p, 4dlu, p, 4dlu, p, 4dlu, p",
                "p, 4dlu, p, 4dlu, p");
        JPanel panel = super.getOptions();
        panel.setLayout(layout);
        CellConstraints cc = new CellConstraints();

        panel.add(LabelFactory.newFormLabel("Identifier:"), cc.xyw(1, 1, 3));
        panel.add(idField, cc.xyw(5, 1, 3));


        panel.add(LabelFactory.newFormLabel("Code:"), cc.xy(1, 3));
        panel.add(codeField, cc.xy(3, 3));
        panel.add(LabelFactory.newFormLabel("Name:"), cc.xy(5, 3));
        panel.add(nameField, cc.xy(7, 3));

        panel.add(LabelFactory.newFormLabel("Taxon:"), cc.xy(1, 5));
        panel.add(taxonField, cc.xy(3, 5));
        panel.add(LabelFactory.newFormLabel("Kingdom:"), cc.xy(5, 5));
        panel.add(kingdomField, cc.xy(7, 5));



        // load this here (doesn't take that long but saves the user waiting for a response)
        TaxonomyMap.getInstance();


        codeField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                autoSetFromCode();
            }

            public void removeUpdate(DocumentEvent e) {
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });


        return panel;


    }
    private boolean autoSetting = false;

    private void autoSetFromCode() {
        String code = codeField.getText().trim().toUpperCase(Locale.ENGLISH);
        if (code.length() == 5) {
            final Taxonomy details = TaxonomyMap.getInstance().get(code);
            if (details != null) {
                taxonField.setText(Integer.toString(details.getTaxon()));
                kingdomField.setText(details.getKingdom().name());
                nameField.setText(details.getOfficialName());
                nameField.setCaretPosition(0);
                String[] nameFragments = details.getOfficialName().split("\\s+");
                if (nameFragments.length >= 2) {
                    idField.setText("i" + nameFragments[0].substring(0, 3) + nameFragments[1].substring(0, 1).toUpperCase() + nameFragments[1].substring(1, 2));
                }
            }
        }
        autoSetting = false;
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

    public Kingdom getKingdom() {
        String type = kingdomField.getText();
        if (type.isEmpty()) {
            flagAsInvalidInput("Kingdom type", kingdomField.getText(),
                               "type of organism (A) Archea, (B) Bacteria, (V) Virus etc.");
            return Kingdom.UNDEFINED;
        }
        return Kingdom.getKingdom(type);


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
        Kingdom kingdom = getKingdom();
        String name = getOfficialName();

        if (fieldsAreValid) {

            Taxonomy orgId = new Taxonomy(taxon, code, kingdom, name, name);
            ReconstructionIdentifier proId = new ReconstructionIdentifier(getProjectIdentifier());

            Reconstruction proj = new Reconstruction(proId, orgId);
            ReconstructionManager.getInstance().setActiveReconstruction(proj);

        }
    }

    @Override
    public boolean update() {
        MainView.getInstance().getJMenuBar().updateContext();
        return MainView.getInstance().update();
    }
}
