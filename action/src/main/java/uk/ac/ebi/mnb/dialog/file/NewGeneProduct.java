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
package uk.ac.ebi.mnb.dialog.file;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicProteinIdentifier;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicRNAIdentifier;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.*;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.metingear.edit.entity.AddEntitiesEdit;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;

/**
 * @name    NewMetabolite - 2011.10.04 <br>
 *          Provides creating of new gene products
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class NewGeneProduct extends NewEntity {

    private static final Logger LOGGER = Logger.getLogger(NewGeneProduct.class);

    private JRadioButton protein = new JRadioButton("Protein");
    private JRadioButton trna  = new JRadioButton("tRNA");
    private JRadioButton rrna  = new JRadioButton("rRNA");
    private ButtonGroup  group = new ButtonGroup();



    public NewGeneProduct(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {

        super(frame, updater, messages, controller, undoableEdits);

        group.add(protein);
        group.add(trna);
        group.add(rrna);

        setDefaultLayout();

    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Enter the detail for a new gene product");
        return label;
    }

    @Override
    public Identifier getIdentifier() {
        return protein.isSelected() ? BasicProteinIdentifier.nextIdentifier() : BasicRNAIdentifier.nextIdentifier();
    }

    @Override
    public JPanel getForm() {
        JPanel panel = super.getForm();
        
        FormLayout layout = (FormLayout) panel.getLayout();
        layout.appendRow(new RowSpec(Sizes.DLUY4));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));

        CellConstraints cc = new CellConstraints();

        Box buttons = Box.createHorizontalBox();
        buttons.add(protein);
        buttons.add(trna);
        buttons.add(rrna);
        protein.setSelected(true);
        panel.add(buttons, cc.xyw(1, layout.getRowCount(), layout.getColumnCount()));
        
        return panel;
    }

    @Override
    public void process() {
        ReconstructionManager manager = DefaultReconstructionManager.getInstance();
        if (manager.active() != null) {
            Reconstruction reconstruction = manager.active();

            EntityFactory factory = DefaultEntityFactory.getInstance();
            GeneProduct product = factory.newInstance(protein.isSelected() ? ProteinProduct.class : rrna.isSelected() ? RibosomalRNA.class : TransferRNA.class);
            product.setIdentifier(getIdentifier());
            product.setName(getName());
            product.setName(getAbbreviation());
            reconstruction.proteome().add(product);
            AddEntitiesEdit edit = new AddEntitiesEdit(reconstruction, EntityMap
                    .singleton(DefaultEntityFactory.getInstance(),
                               product));
            addEdit(edit);
        }
    }

}
