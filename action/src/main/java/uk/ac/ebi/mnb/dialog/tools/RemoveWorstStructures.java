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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.Charge;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.annotation.MolecularFormula;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.tool.domain.StructuralValidity;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.edit.RemoveAnnotationEdit;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.undo.CompoundEdit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static uk.ac.ebi.mdk.tool.domain.StructuralValidity.Category;

/**
 * Utility to remove structures which are invalid/unknown when an okay/average
 * scoring structure is available.
 *
 * @author John May
 */
public class RemoveWorstStructures extends ControllerAction {

    /**
     * Create the action
     *
     * @param controller main controller
     */
    public RemoveWorstStructures(MainController controller) {
        super("remove.worst", controller);
    }

    /**
     * @inheritDoc
     */
    @Override public void actionPerformed(ActionEvent e) {

        Collection<Metabolite> metabolites = getSelection()
                .get(Metabolite.class);

        CompoundEdit edit = new CompoundEdit();

        for (Metabolite m : metabolites) {

            if (m.hasAnnotation(MolecularFormula.class) && m
                    .hasAnnotation(Charge.class)) {

                Multimap<Category, ChemicalStructure> map = HashMultimap
                        .create();

                Charge charge = m.getAnnotations(Charge.class).iterator()
                                 .next();
                Collection<MolecularFormula> formulas = m
                        .getAnnotations(MolecularFormula.class);
                Set<ChemicalStructure> structures = m
                        .getAnnotationsExtending(ChemicalStructure.class);
                Category best = Category.UNKNOWN;

                for (ChemicalStructure structure : structures) {

                    Category validity = StructuralValidity
                            .getValidity(formulas, structure, charge)
                            .getCategory();

                    map.put(validity, structure);

                    if (validity.ordinal() > best.ordinal()) {
                        best = validity;
                    }
                }

                if (best == Category.CORRECT) {
                    map.removeAll(Category.CORRECT);
                    Collection<Annotation> worse = new ArrayList<Annotation>(map.values());
                    edit.addEdit(new RemoveAnnotationEdit(m, worse));
                    for (Annotation annotation : worse)
                        m.removeAnnotation(annotation);
                } else if (best == Category.WARNING) {
                    map.removeAll(Category.WARNING);
                    Collection<Annotation> worse = new ArrayList<Annotation>(map.values());
                    edit.addEdit(new RemoveAnnotationEdit(m, worse));
                    for (Annotation annotation : worse)
                        m.removeAnnotation(annotation);
                }
            }
        }

        edit.end();

        getController().getUndoManager().addEdit(edit);
        update(getSelection());

    }
}
