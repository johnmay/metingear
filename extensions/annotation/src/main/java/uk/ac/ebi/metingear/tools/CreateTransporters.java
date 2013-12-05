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
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.tool.transport.AminoAcid;
import uk.ac.ebi.mdk.tool.transport.LibraryStructure;
import uk.ac.ebi.mdk.tool.transport.Misc;
import uk.ac.ebi.mdk.tool.transport.Nucleo;
import uk.ac.ebi.mdk.tool.transport.Saccharides;
import uk.ac.ebi.mdk.tool.transport.TransportFactory;
import uk.ac.ebi.metingear.edit.entity.AddEntitiesEdit;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.ExpandableComponentGroup;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Dialog for specifying transport reactions.
 *
 * @author John May
 */
public final class CreateTransporters extends AbstractControlDialog {

    Map<JCheckBox, LibraryStructure> symportCompounds = new HashMap<JCheckBox, LibraryStructure>();
    Map<JCheckBox, LibraryStructure> abcCompounds    = new HashMap<JCheckBox, LibraryStructure>();

    public CreateTransporters(Window window) {
        super(window);
    }

    @Override public JComponent createForm() {
        JComponent component = super.createForm();

        FormLayout layout = new FormLayout("left:p",
                                           "p, 4dlu, p");
        component.setLayout(layout);

        CellConstraints cc = new CellConstraints();

        JComponent protonSymport = PanelFactory.create();
        {
            FormLayout symportLayout = new FormLayout("p, 4dlu, p, 4dlu, p");
            protonSymport.setLayout(symportLayout);
            symportLayout.appendRow(new RowSpec(Sizes.DLUX14));
            protonSymport.add(LabelFactory.newLabel("Amino acids"), cc.xyw(1, symportLayout.getRowCount(), 5));
            AminoAcid[] aas = AminoAcid.values();
            Nucleo[]    ncs = Nucleo.values();
            
            int aaIdx = 0;
            while (aaIdx < aas.length) {
                symportLayout.appendRow(new RowSpec(Sizes.PREFERRED));
                for (int x = 0; aaIdx < aas.length && x < 3; x++) {
                    AminoAcid aa = aas[aaIdx++];
                    JCheckBox cb = CheckBoxFactory.newCheckBox(aa.structureName());
                    protonSymport.add(cb, cc.xy(2 * x + 1, symportLayout.getRowCount())); 
                    symportCompounds.put(cb, aa);
                }
            }
            
            symportLayout.appendRow(new RowSpec(Sizes.DLUX14));                
            protonSymport.add(LabelFactory.newLabel("Nucleobases / Nucleosides"), cc.xyw(1, symportLayout.getRowCount(), 5));
            int ncIdx = 0;
            while (ncIdx < ncs.length) {
                symportLayout.appendRow(new RowSpec(Sizes.PREFERRED));
                for (int x = 0; ncIdx < ncs.length && x < 3; x++) {
                    Nucleo nucleo = ncs[ncIdx++];
                    JCheckBox cb = CheckBoxFactory.newCheckBox(nucleo.structureName());
                    protonSymport.add(cb, cc.xy(2 * x + 1, symportLayout.getRowCount()));
                    symportCompounds.put(cb, nucleo);
                }
            }
        }

        component.add(new ExpandableComponentGroup("Proton Symport",
                                                   protonSymport,
                                                   this), cc.xy(1, 1));

        JComponent abc = PanelFactory.create();
        {

            AminoAcid[]   aas   = AminoAcid.values();
            Saccharides[] schs  = Saccharides.values();
            Misc[]        mscs = Misc.values();
            
            FormLayout abcLayout = new FormLayout("p, 4dlu, p, 4dlu, p");
            abc.setLayout(abcLayout);
            abcLayout.appendRow(new RowSpec(Sizes.DLUX14));
            abc.add(LabelFactory.newLabel("Amino acids"), cc.xyw(1, abcLayout.getRowCount(), 5));
            int aaIdx = 0;
            while (aaIdx < aas.length) {
                abcLayout.appendRow(new RowSpec(Sizes.PREFERRED));
                for (int x = 0; aaIdx < aas.length && x < 3; x++) {
                    AminoAcid aa = aas[aaIdx++];
                    JCheckBox cb = CheckBoxFactory.newCheckBox(aa.structureName());
                    abc.add(cb, cc.xy(2 * x + 1, abcLayout.getRowCount()));
                    abcCompounds.put(cb, aa);
                }
            }
            abcLayout.appendRow(new RowSpec(Sizes.DLUX14));
            abc.add(LabelFactory.newLabel("Saccharides"), cc.xyw(1, abcLayout.getRowCount(), 5));

            int schIdx = 0;
            while (schIdx < schs.length) {
                abcLayout.appendRow(new RowSpec(Sizes.PREFERRED));
                for (int x = 0; schIdx < schs.length && x < 3; x++) {
                    Saccharides sch = schs[schIdx++];
                    JCheckBox cb = CheckBoxFactory.newCheckBox(sch.structureName());
                    abc.add(cb, cc.xy(2 * x + 1, abcLayout.getRowCount()));
                    abcCompounds.put(cb, sch);
                }
            }

            abcLayout.appendRow(new RowSpec(Sizes.DLUX14));
            abc.add(LabelFactory.newLabel("Misc (Minerals, Ions, etc.)"), cc.xyw(1, abcLayout.getRowCount(), 5));

            int mscIdx = 0;
            while (mscIdx < mscs.length) {
                abcLayout.appendRow(new RowSpec(Sizes.PREFERRED));
                for (int x = 0; mscIdx < mscs.length && x < 3; x++) {
                    Misc msc = mscs[mscIdx++];
                    JCheckBox cb = CheckBoxFactory.newCheckBox(msc.structureName());
                    abc.add(cb, cc.xy(2 * x + 1, abcLayout.getRowCount()));
                    abcCompounds.put(cb, msc);
                }
            }
        }

        component.add(new ExpandableComponentGroup("ATP Binding Casettee (ABC) Transporters",
                                                   abc,
                                                   this), cc.xy(1, 3));

        return component;
    }

    @Override public void process() {
        try {
            List<LibraryStructure> symportStructures = new ArrayList<LibraryStructure>();
            List<LibraryStructure> abcStructures = new ArrayList<LibraryStructure>();

            for (Map.Entry<JCheckBox, LibraryStructure> e : symportCompounds.entrySet()) {
                if (e.getKey().isSelected()) {
                    symportStructures.add(e.getValue());
                }
            }

            for (Map.Entry<JCheckBox, LibraryStructure> e : abcCompounds.entrySet()) {
                if (e.getKey().isSelected()) {
                    abcStructures.add(e.getValue());
                }
            }

            TransportFactory tf = new TransportFactory(getEntityFactory());
            Collection<AnnotatedEntity> reactions = new ArrayList<AnnotatedEntity>();

            for (MetabolicReaction mr : tf.protonSymport(symportStructures, Organelle.CYTOPLASM, Organelle.EXTRACELLULAR))
                reactions.add(mr);
            for (MetabolicReaction mr : tf.abc(abcStructures, Organelle.EXTRACELLULAR, Organelle.CYTOPLASM))
                reactions.add(mr);

            ReconstructionManager reconstructionManager = DefaultReconstructionManager.getInstance();
            Reconstruction reconstruction = reconstructionManager.active();

            AddEntitiesEdit edit = new AddEntitiesEdit(reconstruction, reactions);
            addEdit(edit);
            edit.apply();
        } catch (Exception e) {
            addReport(new ErrorMessage("Internal error: " + e.getMessage() + " cause: " + e.getCause()));
        }
    }
}
