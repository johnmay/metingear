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

package uk.ac.ebi.metingear.tools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.tool.transport.TransportFactory;
import uk.ac.ebi.metingear.edit.entity.AddEntitiesEdit;
import uk.ac.ebi.metingear.view.AbstractControlDialog;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.mdk.tool.transport.TransportFactory.AminoAcid;

/**
 * Dialog for specifying transport reactions.
 *
 * @author John May
 */
public final class CreateTransporters extends AbstractControlDialog {

    Map<JCheckBox, AminoAcid> aaSymport = new HashMap<JCheckBox, AminoAcid>();

    public CreateTransporters(Window window) {
        super(window);
    }

    @Override public JComponent createForm() {
        JComponent component = super.createForm();

        FormLayout layout = new FormLayout("p, 4dlu, p", "p");
        component.setLayout(layout);

        CellConstraints cc = new CellConstraints();

        component.add(getLabel("aa.trans"), cc.xy(1, 1));

        AminoAcid[] aas = AminoAcid.values();

        for (AminoAcid aa : aas) {
            layout.appendRow(new RowSpec(Sizes.PREFERRED));
            JCheckBox cb = CheckBoxFactory.newCheckBox();
            component.add(LabelFactory.newFormLabel(aa.name()), cc.xy(1, layout.getRowCount()));
            component.add(cb, cc.xy(3, layout.getRowCount()));
            aaSymport.put(cb, aa);
        }

        return component;
    }

    @Override public void process() {

        List<AminoAcid> aas = new ArrayList<AminoAcid>();

        for (Map.Entry<JCheckBox, AminoAcid> e : aaSymport.entrySet()) {
            if (e.getKey().isSelected()) {
                aas.add(e.getValue());
            }
        }

        TransportFactory tf = new TransportFactory(getEntityFactory());
        Collection<AnnotatedEntity> reactions = new ArrayList<AnnotatedEntity>();
        for (MetabolicReaction mr : tf.aaProtonSymporter(aas, Organelle.CYTOPLASM, Organelle.EXTRACELLULAR))
            reactions.add(mr);

        ReconstructionManager reconstructionManager = DefaultReconstructionManager.getInstance();
        Reconstruction reconstruction = reconstructionManager.active();

        AddEntitiesEdit edit = new AddEntitiesEdit(reconstruction, reactions);
        addEdit(edit);
        edit.apply();
    }
}
