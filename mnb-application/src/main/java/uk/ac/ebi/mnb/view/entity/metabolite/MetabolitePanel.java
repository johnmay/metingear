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
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.utility.TextUtility;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.core.metabolite.MetaboliteClassImplementation;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.entities.Metabolite;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.mnb.view.entity.AbstractEntityPanel;
import uk.ac.ebi.render.molecule.MoleculeRenderer;


/**
 *          MetabolitePanel – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetabolitePanel
        extends AbstractEntityPanel {

    private static final Logger LOGGER = Logger.getLogger(MetabolitePanel.class);

    private Metabolite entity;
    // chemical structure

    private JLabel structureWarning = LabelFactory.newLabel("No Structure");

    private JLabel structure = LabelFactory.newLabel("No Structure");
    // for isGeneric

    private JLabel markush = LabelFactory.newFormLabel("Markush:");

    private JLabel markushViewer = LabelFactory.newLabel("");

    private JComboBox markushEditor = ComboBoxFactory.newComboBox(Arrays.asList("Yes", "No"));
    // metabolic class

    private JLabel type = LabelFactory.newFormLabel("Type:");

    private JLabel typeViewer = LabelFactory.newLabel("");

    private JComboBox typeEditor = ComboBoxFactory.newComboBox(MetaboliteClassImplementation.values());
    // molecular formula

    private JTextField formulaEditor = FieldFactory.newTransparentField(15, false);

    private JLabel formularViewer = LabelFactory.newLabel("");
    // cell constraints

    private CellConstraints cc = new CellConstraints();


    public MetabolitePanel() {
        super("Metabolite", new AnnotationRenderer());

        buildSynopsis();
    }


    @Override
    public boolean update() {

        // update all fields and labels...
        if (super.update()) {

            if (entity.hasStructure()) {
                IAtomContainer atomcontainer = entity.getStructures().iterator().next().getStructure();
                try {
                    structure.setIcon(new ImageIcon(MoleculeRenderer.getInstance().getImage(atomcontainer,
                                                                                            new Rectangle(256, 256))));
                } catch (CDKException ex) {
                    MainView.getInstance().addErrorMessage("Could not render structure");
                }
                structure.setText("");
            } else {
                structure.setText("No Structure");
                structure.setIcon(null);
            }

            Collection<MolecularFormula> formulas = entity.getAnnotations(MolecularFormula.class);
            if (formulas.iterator().hasNext()) {
                MolecularFormula mf = formulas.iterator().next();
                formularViewer.setText(mf.getFormula() != null ? TextUtility.html(mf.toHTML()) : mf.toString());
                formulaEditor.setText(formulas.iterator().next().toString());
            } else {
                formularViewer.setText("");
                formulaEditor.setText("");
            }

            boolean generic = entity.isGeneric();

            markushViewer.setText(generic ? "Yes" : "No");
            markushEditor.setSelectedIndex(generic ? 0 : 1);

            typeViewer.setText(entity.getType().toString());
            typeEditor.setSelectedItem(entity.getType());

            return true;
        }

        return false;
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
    public JPanel getSynopsis() {
        return specific;
    }


    /**
     * Builds entity specific panel
     */
    private void buildSynopsis() {
        specific = PanelFactory.createInfoPanel();
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


    @Override
    public Collection<? extends AnnotatedEntity> getReferences() {
        Reconstruction recon = ReconstructionManager.getInstance().getActive();
        if (entity != null && recon != null) {
            return recon.getReactions().getReactions(entity);
        }
        return new ArrayList();
    }


    @Override
    public void store() {

        super.store();

        // formula
        if (formulaEditor.getText().isEmpty() == false) {
            if (entity.getAnnotations(MolecularFormula.class).iterator().hasNext()) {
                MolecularFormula formula = entity.getAnnotations(MolecularFormula.class).iterator().next();
                formula.setFormula(formulaEditor.getText());
            } else {
                entity.addAnnotation(new MolecularFormula(formulaEditor.getText()));
            }
        }

        entity.setGeneric(((String) markushEditor.getSelectedItem()).equals("Yes") ? true : false);
        entity.setType((MetaboliteClassImplementation) typeEditor.getSelectedItem());
    }
}
