/**
 * CollapseStructures.java
 *
 * 2011.10.28
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
package uk.ac.ebi.mnb.dialog.tools;

import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.chemical.ChemicalStructure;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.tools.MolecularHashCode;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

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

    public CollapseStructures(JFrame frame, TargetedUpdate updater, MessageManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");
        setDefaultLayout();
    }

    @Override
    public JPanel getOptions() {
        return super.getOptions();
    }

    @Override
    public void process() {
        if (getSelection().hasSelection(Metabolite.class) == false) {
            return;
        }
        Collection<Metabolite> metabolites = getSelection().get(Metabolite.class);
        for (Metabolite m : metabolites) {
            for (ChemicalStructure structure : m.getChemicalStructures()) {
                System.out.println(MolecularHashCode.hashCode(structure.getMolecule()));
            }
        }

    }
}