/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
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

package uk.ac.ebi.metingear.tools.structure;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tautomer.TautomerStream;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.service.ProgressListener;
import uk.ac.ebi.metingear.view.AbstractControlAction;
import uk.ac.ebi.mnb.edit.ReplaceAnnotationEdit;

import javax.swing.undo.CompoundEdit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author John May
 */
public final class CanonicalTautomer extends AbstractControlAction {
    @Override public void process(ProgressListener listener) {

        
        List<ReplaceAnnotationEdit> edits = new ArrayList<ReplaceAnnotationEdit>();

        
        for (Metabolite m : getSelection(Metabolite.class)) {
            for (ChemicalStructure cs : m.getStructures()) {
                try {
                    IAtomContainer ac = cs.getStructure().clone();
                    IAtomContainer taut = new TautomerStream(ac).next();
                    if (taut != null) {
                        ChemicalStructure rep = (ChemicalStructure) cs.newInstance();
                        rep.setStructure(taut);
                        edits.add(new ReplaceAnnotationEdit(m, cs, rep));
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }

        CompoundEdit compoundEdit = new CompoundEdit();
        for (ReplaceAnnotationEdit edit : edits) {
            edit.apply();
            compoundEdit.addEdit(edit);
        }
        compoundEdit.end();
        addEdit(compoundEdit);        
    }
}
