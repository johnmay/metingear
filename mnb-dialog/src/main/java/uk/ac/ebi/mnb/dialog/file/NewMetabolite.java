/**
 * NewMetabolite.java
 *
 * 2011.10.04
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
package uk.ac.ebi.mnb.dialog.file;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.chemet.resource.basic.BasicChemicalIdentifier;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.entities.Metabolite;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;


/**
 * @name    NewMetabolite - 2011.10.04 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class NewMetabolite extends NewEntity {

    private static final Logger LOGGER = Logger.getLogger(NewMetabolite.class);


    public NewMetabolite(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits);
        setDefaultLayout();
    }


    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Please specify detail for a new metabolite");
        return label;
    }

    @Override
    public Identifier getIdentifier() {
        return new BasicChemicalIdentifier();
    }

    @Override
    public void process() {
        ReconstructionManager manager = ReconstructionManager.getInstance();
        if (manager.hasProjects()) {
            Reconstruction reconstruction = manager.getActive();
            Metabolite m = DefaultEntityFactory.getInstance().newInstance(Metabolite.class, getIdentifier(), getName(), getAbbreviation());
            reconstruction.addMetabolite(m);
        }
    }
}
