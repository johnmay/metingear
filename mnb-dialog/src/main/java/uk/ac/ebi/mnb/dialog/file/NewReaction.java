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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.interfaces.Updatable;
import uk.ac.ebi.resource.chemical.BasicChemicalIdentifier;

/**
 * @name    NewMetabolite - 2011.10.04 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class NewReaction extends NewEntity {

    private static final Logger LOGGER = Logger.getLogger(NewReaction.class);
    private ReactionTextField equation;
    private static CellConstraints cc = new CellConstraints();

    public NewReaction(JFrame frame, Updatable updatable) {
        super(frame, updatable, new BasicChemicalIdentifier());
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Please specify detail for a new reaction");
        return label;
    }

    @Override
    public JPanel getOptions() {

        JPanel panel = super.getOptions();

        equation = new ReactionTextField(this);

        FormLayout layout = (FormLayout) panel.getLayout();
        layout.appendRow(new RowSpec(Sizes.DLUY4));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));
        panel.add(equation, cc.xyw(1, layout.getRowCount(), 7));

        return panel;
    }

    @Override
    public void process() {
        ReconstructionManager manager = ReconstructionManager.getInstance();
        if (manager.hasProjects()) {
            Reconstruction reconstruction = manager.getActiveReconstruction();
            MetabolicReaction rxn = equation.getReaction(getIdentifier());
            //rxn.setIdentifier();
            rxn.setAbbreviation(getAbbreviation());
            rxn.setName(getName());
            reconstruction.addReaction(rxn);
        }
    }
}
