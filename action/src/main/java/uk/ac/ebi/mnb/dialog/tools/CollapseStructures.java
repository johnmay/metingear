/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
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

import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.tool.domain.MolecularHashFactory;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.util.Collection;


/**
 *          CollapseStructures - 2011.10.28 <br>
 *          A dialog providing options to collapse multiple structures to a
 *          single annotation
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
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
        if (getSelection().hasSelection(Metabolite.class) == false) {
            return;
        }
        Collection<Metabolite> metabolites = getSelection().get(Metabolite.class);
        for (Metabolite m : metabolites) {
            for (AtomContainerAnnotation structure : m.getAnnotations(AtomContainerAnnotation.class)) {
                System.out.println(MolecularHashFactory.getInstance().getHash(structure.getStructure()));
            }
        }

    }
}
