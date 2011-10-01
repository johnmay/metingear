/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.file;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.mnb.io.TaxonomyMap;
import uk.ac.ebi.resource.organism.Kingdom;
import uk.ac.ebi.resource.organism.Taxonomy;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.resource.ReconstructionIdentifier;


/**
 * NewProjectDialog.java
 *
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class NewProjectDialog extends DropdownDialog {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      NewProjectDialog.class);
    private boolean fieldsAreValid = true;
    private JTextField idField;
    private JTextField codeField;
    private JTextField taxonField;
    private JTextField nameField;
    private JTextField kingdomField;


    public NewProjectDialog() {

        super(MainFrame.getInstance(), MainFrame.getInstance(), "NewProject");

        idField = new JTextField();
        codeField = new JTextField();
        taxonField = new JTextField();
        nameField = new JTextField();
        kingdomField = new JTextField();



        FormLayout layout = new FormLayout(
          "right:max(50dlu;p), 4dlu, 75dlu, 7dlu, right:p, 4dlu, 75dlu",
          "p, 2dlu, p, 3dlu, p, 3dlu, p, 7dlu, " +
          "p, 2dlu, p, 3dlu, p, 3dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addSeparator("New Project", cc.xyw(1, 1, 7));
        builder.addLabel("Identifier", cc.xy(1, 3));
        builder.add(idField, cc.xy(3, 3));

        builder.addSeparator("Organism Details", cc.xyw(1, 9, 7));
        builder.addLabel("Code", cc.xy(1, 11));
        builder.add(codeField, cc.xy(3, 11));
        builder.addLabel("Taxon", cc.xy(5, 11));
        builder.add(taxonField, cc.xy(7, 11));

        builder.addLabel("Official Name", cc.xy(1, 13));
        builder.add(nameField, cc.xy(3, 13));
        builder.addLabel("Kingdom", cc.xy(5, 13));
        builder.add(kingdomField, cc.xy(7, 13));


        builder.add(getCloseButton(), cc.xy(3, 15));
        builder.add(getRunButton(), cc.xy(7, 15));

        add(builder.getPanel());

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



    }


    private boolean autoSetting = false;


    private void autoSetFromCode() {
        String code = codeField.getText().trim().toUpperCase(Locale.ENGLISH);
        if( code.length() == 5 ) {
            final Taxonomy details = TaxonomyMap.getInstance().get(code);
            if( details != null ) {
                taxonField.setText(Integer.toString(details.getTaxon()));
                kingdomField.setText(details.getKingdom().name());
                nameField.setText(details.getOfficialName());
            }
        }
        autoSetting = false;
    }


    public String getCode() {
        String code = codeField.getText();
        // could check against db also
        if( code.isEmpty() || code.length() != 5 ) {
            flagAsInvalidInput("Organism code", code,
                               "five letter code for organism (e.g. MYTUB, SPEPQ)");
        }
        return code;
    }


    public Kingdom getKingdom() {
        String type = kingdomField.getText();
        if( type.isEmpty() ) {
            flagAsInvalidInput("Kingdom type", kingdomField.getText(),
                               "type of organism (A) Archea, (B) Bacteria, (V) Virus etc.");
            return Kingdom.UNDEFINED;
        }
        return Kingdom.getKingdom(type);


    }


    public String getOfficialName() {
        String name = nameField.getText();
        if( name.isEmpty() ) {
            flagAsInvalidInput("Official Name", name, "non empty name for organism");
        }
        return name;
    }


    public Integer getTaxon() {
        try {
            return Integer.parseInt(taxonField.getText());
        } catch( NumberFormatException ex ) {
            flagAsInvalidInput("Taxon code", taxonField.getText(),
                               "five digit taxon code (e.g. 12342)");
            return 0;
        }
    }


    public String getProjectIdentifier() {
        if( idField.getText().isEmpty() ) {
            return getOfficialName();
        }
        return idField.getText();
    }


    public void flagAsInvalidInput(String label, String found, String expected) {
        JOptionPane.showMessageDialog(this, label +
                                            " input '" +
                                            found +
                                            "' is not a valid. Expected '" +
                                            expected + "'!");
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

        if( fieldsAreValid ) {

            Taxonomy orgId = new Taxonomy(taxon, code, kingdom, name, name);
            ReconstructionIdentifier proId = new ReconstructionIdentifier(getProjectIdentifier());

            Reconstruction proj = new Reconstruction(proId, orgId);
            ReconstructionManager.getInstance().setActiveReconstruction(proj);

            setVisible(false);
        }
    }


    @Override
    public void update() {
        MainFrame.getInstance().getProjectPanel().update();
        MainFrame.getInstance().getSourceListController().update();
    }


}

