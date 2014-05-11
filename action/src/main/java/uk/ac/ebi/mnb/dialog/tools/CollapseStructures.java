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
package uk.ac.ebi.mnb.dialog.tools;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.hash.BasicAtomEncoder;
import org.openscience.cdk.hash.HashGeneratorMaker;
import org.openscience.cdk.hash.MoleculeHashGenerator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.edit.RemoveAnnotationEdit;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * CollapseStructures - 2011.10.28 <br> A dialog providing options to collapse
 * multiple structures to a single annotation
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class CollapseStructures
        extends ControllerDialog {

    public CollapseStructures(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");
        setDefaultLayout();
    }


    @Override
    public JPanel getForm() {
        return super.getForm();
    }


    @Override
    public void process() {

        MoleculeHashGenerator hashGen = new HashGeneratorMaker().depth(16)
                                                                .elemental()
                                                                .charged()
                                                                .isotopic()
                                                                .chiral()
                                                                .suppressHydrogens()
                                                                .perturbed()
                                                                .encode(BasicAtomEncoder.BOND_ORDER_SUM)
                                                                .molecular();

        CompoundEdit edit = new CompoundEdit();
        Collection<Metabolite> metabolites = getSelection().get(Metabolite.class);
        for (Metabolite m : metabolites) {
            Set<Long> seen = new HashSet<Long>();
            Collection<ChemicalStructure> remove = new ArrayList<ChemicalStructure>();

            for (ChemicalStructure cs : m.getStructures()) {
                
                IAtomContainer ac = cs.getStructure();

                try {
                    // XXX: needed for stereo
                    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
                } catch (CDKException e) {
                    continue;
                }

                long hash = hashGen.generate(ac);
                if (!seen.add(hash))
                    remove.add(cs);
            }

            RemoveAnnotationEdit removeEdit = new RemoveAnnotationEdit(m, remove);
            removeEdit.apply();
            edit.addEdit(removeEdit);
            
        }
        edit.end();

        addEdit(edit);

    }
}
