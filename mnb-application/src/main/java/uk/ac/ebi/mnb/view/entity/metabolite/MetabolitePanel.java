
/**
 * MetabolitePanel.java
 *
 * 2011.09.30
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.view.entity.metabolite;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.util.Collection;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.metabolite.MetaboliteClass;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.mnb.view.ComboBox;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.TransparentTextField;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.entity.EntityPanelFactory;
import uk.ac.ebi.mnb.view.labels.BoldLabel;
import uk.ac.ebi.mnb.view.labels.Label;
import uk.ac.ebi.mnb.view.labels.WarningLabel;


/**
 *          MetabolitePanel – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetabolitePanel
  extends EntityPanelFactory {

    private static final Logger LOGGER = Logger.getLogger(MetabolitePanel.class);
    private Metabolite entity;
    // chemical structure
    private JLabel structureWarning = new WarningLabel("No Structure");
    private JLabel structure = new WarningLabel("No Structure");
    // for isGeneric
    private JLabel markush = new BoldLabel("Markush:");
    private JLabel markushViewer = new Label("");
    private JComboBox markushEditor = new ComboBox("Yes", "No");
    // metabolic class
    private JLabel type = new BoldLabel("Type:");
    private JLabel typeViewer = new Label("");
    private JComboBox typeEditor = new ComboBox(MetaboliteClass.values());
    // molecular formula
    private JTextField formulaEditor = new TransparentTextField("", 15, false);
    private JLabel formularViewer = new Label();
    // cell constraints
    private CellConstraints cc = new CellConstraints();


    public MetabolitePanel() {
        super("Metabolite", new AnnotationRenderer());

        buildSpecificPanel();
    }


    @Override
    public boolean update() {

        // update all fields and labels...

        if( entity.hasStructureAssociated() ) {
            JLabel label = (JLabel) getRenderer().visit(entity.getFirstChemicalStructure());
            structure.setIcon(label.getIcon());
            structure.setText("");
        } else {
            structure.setText("No Structure");
            structure.setIcon(null);
        }

        Collection<MolecularFormula> formulas = entity.getAnnotations(MolecularFormula.class);
        formularViewer.setText(ViewUtils.htmlWrapper(formulas.iterator().next().toHTML()));
        formulaEditor.setText(formulas.iterator().next().toString());

        boolean generic = entity.isGeneric();

        markushViewer.setText(generic ? "Yes" : "No");
        markushEditor.setSelectedIndex(generic ? 0 : 1);

        typeViewer.setText(entity.getType().toString());
        typeEditor.setSelectedItem(entity.getType());

        return super.update();

    }


    @Override
    public boolean setEntity(AnnotatedEntity entity) {
        this.entity = (Metabolite) entity;
        return super.setEntity(entity);
    }


    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);

        // set editor vissible when editable and vise versa
        formulaEditor.setVisible(editable);
        markushEditor.setVisible(editable);
        formulaEditor.setVisible(editable);
        formulaEditor.setEditable(editable);
        typeEditor.setVisible(editable);

        // set viewers hidden when editable and vise versa
        formularViewer.setVisible(!editable);
        markushViewer.setVisible(!editable);
        typeViewer.setVisible(!editable);
    }


    private JPanel specific;


    /**
     * Returns the specific information panel
     */
    public JPanel getSpecificsPanel() {





        return specific;

    }


    /**
     * Builds entity specific panel
     */
    private void buildSpecificPanel() {
        specific = new GeneralPanel();
        specific.setLayout(new FormLayout("p:grow, 4dlu, p:grow, 4dlu, p:grow, 4dlu, p:grow",
                                          "p, 4dlu, p, 4dlu, p"));


        specific.add(structure, cc.xyw(1, 1, 7, CellConstraints.CENTER, CellConstraints.CENTER));
        specific.add(formularViewer, cc.xyw(1, 3, 7, CellConstraints.CENTER,
                                            CellConstraints.CENTER));
        specific.add(formulaEditor, cc.xyw(1, 3, 7, CellConstraints.CENTER,
                                           CellConstraints.CENTER));
        formulaEditor.setHorizontalAlignment(SwingConstants.CENTER);


        specific.add(markush, cc.xy(1, 5));
        specific.add(markushViewer, cc.xy(3, 5));
        specific.add(markushEditor, cc.xy(3, 5));

        specific.add(type, cc.xy(5, 5));
        specific.add(typeViewer, cc.xy(7, 5));
        specific.add(typeEditor, cc.xy(7, 5));
    }


    /**
     * Returns the internal reference panel information panel
     */
    public JPanel getInternalReferencePanel() {

        JPanel panel = new JPanel();

        panel.setBackground(Color.DARK_GRAY);
        panel.add(new JLabel("Internal references"));

        return panel;

    }


    @Override
    public void store() {
        super.store();
        MolecularFormula formula = entity.getAnnotations(MolecularFormula.class).iterator().next();
        formula.setFormula(formulaEditor.getText());
        entity.setGeneric(((String) markushEditor.getSelectedItem()).equals("Yes") ? true : false);
        entity.setType((MetaboliteClass) typeEditor.getSelectedItem());
    }


}

