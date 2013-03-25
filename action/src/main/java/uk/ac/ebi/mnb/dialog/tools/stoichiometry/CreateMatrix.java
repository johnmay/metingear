/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
package uk.ac.ebi.mnb.dialog.tools.stoichiometry;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.Direction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.matrix.DefaultStoichiometricMatrix;
import uk.ac.ebi.mdk.ui.component.matrix.MatrixPane;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.awt.*;
import java.util.Collection;


/**
 * CreateMatrix - 2011.11.24 <br> Class creates a stoichiometric matrix
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date: 2011-12-13 16:45:11 +0000 (Tue, 13 Dec
 *          2011) $
 */
public class CreateMatrix
        extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(CreateMatrix.class);

    private DefaultStoichiometricMatrix matrix; // tempoary storage


    public CreateMatrix(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
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
    public JPanel getForm() {
        JPanel panel = super.getForm();

        return panel;
    }


    @Override
    public void process() {

        EntityCollection manager = getSelection();
        Reconstruction recon = DefaultReconstructionManager.getInstance().active();

        Collection<MetabolicReaction> rxns =
                manager.hasSelection(MetabolicReaction.class) && manager.get(MetabolicReaction.class).size() > 1
                ? manager.get(MetabolicReaction.class)
                : recon.getReactome();

        LOGGER.info("Creating reaction matrix for " + rxns.size() + " reactions");
        matrix = DefaultStoichiometricMatrix.create((int) (rxns.size() * 1.5),
                                                    rxns.size());
        for (MetabolicReaction rxn : rxns) {

            // transpose
            if (rxn.getDirection() == Direction.BACKWARD) {
                rxn.transpose();
                rxn.setDirection(Direction.FORWARD);
            }

            matrix.addReaction(rxn);
        }
    }


    @Override
    public boolean update() {

        Reconstruction reconstruction = DefaultReconstructionManager.getInstance().active();

        JFrame frame = new JFrame("Stoichiometric Matrix ("
                                          + reconstruction.getAccession()
                                          + ")");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new MatrixPane(matrix));
        frame.setVisible(true);

        Reconstruction active = DefaultReconstructionManager.getInstance().active();

        active.setMatrix(matrix);

        updateMenuContext();

        return true;
    }
}
