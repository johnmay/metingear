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
package uk.ac.ebi.mnb.dialog.edit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.Identifier;
import uk.ac.ebi.mnb.interfaces.Updatable;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.resource.chemical.BasicChemicalIdentifier;

/**
 * @name    NewMetabolite - 2011.10.04 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class NewMetabolite extends NewEntity {

    private static final Logger LOGGER = Logger.getLogger(NewMetabolite.class);

    public NewMetabolite(JFrame frame, Updatable updatable) {
        super(frame, updatable, new BasicChemicalIdentifier());
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Create a new metabolite");
        return label;
    }

    @Override
    public void process() {
        ReconstructionManager manager = ReconstructionManager.getInstance();
        if (manager.hasProjects()) {
            Reconstruction reconstruction = manager.getActiveReconstruction();
            Metabolite m = new Metabolite(getIdentifier(), getAbbreviation(), getName());
            reconstruction.addMetabolite(m);
        }
    }


}
