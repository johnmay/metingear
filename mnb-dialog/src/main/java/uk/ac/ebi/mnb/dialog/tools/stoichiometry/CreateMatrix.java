/**
 * CreateMatrix.java
 *
 * 2011.11.24
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
package uk.ac.ebi.mnb.dialog.tools.stoichiometry;

import java.awt.Dimension;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.metabolomes.core.reaction.matrix.StoichiometricMatrix;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.ContextAction;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.SelectionManager;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.visualisation.matrix.MatrixPane;

/**
 *          CreateMatrix - 2011.11.24 <br>
 *          Class creates a stoichiometric matrix
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class CreateMatrix
        extends ControllerDialog
        implements ContextAction {

    private static final Logger LOGGER = Logger.getLogger(CreateMatrix.class);
    private StoichiometricMatrix<Metabolite, MetabolicReaction> matrix; // tempoary storage

    public CreateMatrix(JFrame frame, TargetedUpdate updater, MessageManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");
        setDefaultLayout();
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("<html>Create the stoichiometric matrix for this model</html>");
        label.setPreferredSize(new Dimension(300, 32));
        return label;
    }

    @Override
    public JPanel getOptions() {
        JPanel panel = super.getOptions();

        return panel;
    }

    @Override
    public void process() {

        SelectionManager manager = getSelection();
        Reconstruction recon = ReconstructionManager.getInstance().getActive();

        Collection<MetabolicReaction> rxns = manager.hasSelection(MetabolicReaction.class)
                                             ? manager.get(MetabolicReaction.class)
                                             : recon.getReactions();

        LOGGER.info("Creating reaction matrix for " + rxns.size() + " reactions");
        matrix = new StoichiometricMatrix<Metabolite, MetabolicReaction>((int) (rxns.size() * 1.5),
                                                                         rxns.size());
        for (MetabolicReaction rxn : rxns) {
            matrix.addReaction(rxn, rxn.getAllReactionMolecules().toArray(new Metabolite[0]), getStoichiometries(rxn));
        }
    }

    @Override
    public boolean update() {
        JFrame frame = new JFrame("Matrix");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new MatrixPane(matrix));
        frame.setVisible(true);
        return true;
    }

    public Double[] getStoichiometries(MetabolicReaction rxn) {

        Double[] coefs = new Double[rxn.getAllReactionParticipants().size()];
        int i = 0;
        for (Double d : rxn.getReactantStoichiometries()) {
            coefs[i++] = -d;
        }
        for (Double d : rxn.getProductStoichiometries()) {
            coefs[i++] = +d;
        }

        return coefs;
    }

    public boolean setContext() {

        ReconstructionManager manager = ReconstructionManager.getInstance();

        return getSelection().hasSelection(MetabolicReaction.class)
               || (manager.hasProjects()
                   && manager.getActive().getReactions().isEmpty());
    }

    public boolean setContext(Object obj) {
        return setContext();
    }
}
