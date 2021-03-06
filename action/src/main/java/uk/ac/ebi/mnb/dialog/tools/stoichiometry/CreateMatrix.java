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
package uk.ac.ebi.mnb.dialog.tools.stoichiometry;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.metabolite.CompartmentalisedMetabolite;
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

        if( manager.hasSelection(MetabolicReaction.class)){
            Collection<MetabolicReaction> reactions = manager.get(MetabolicReaction.class);
            LOGGER.info("Creating reaction matrix for " + reactions.size() + " reactions");
            matrix = DefaultStoichiometricMatrix.create((int) (reactions.size() * 1.5),
                                                        reactions.size());
            for (MetabolicReaction rxn : reactions) {

                // transpose
                if (rxn.getDirection() == Direction.BACKWARD) {
                    rxn.transpose();
                    rxn.setDirection(Direction.FORWARD);
                }

                matrix.addReaction(rxn);
            }
        } else {
            LOGGER.info("Creating reaction matrix for " + recon.reactome().size() + " reactions");
            matrix = DefaultStoichiometricMatrix.create((int) (recon.reactome().size() * 1.5),
                                                        recon.metabolome().size());
            for (MetabolicReaction rxn : recon.reactome()) {

                // transpose
                if (rxn.getDirection() == Direction.BACKWARD) {
                    rxn.transpose();
                    rxn.setDirection(Direction.FORWARD);
                }

                matrix.addReaction(rxn);
            }
        }
    }


    @Override
    public boolean update() {

        Reconstruction reconstruction = DefaultReconstructionManager.getInstance().active();

        JFrame frame = new JFrame("Stoichiometric Matrix ("
                                          + reconstruction.getAccession()
                                          + ")");

        Reconstruction active = DefaultReconstructionManager.getInstance().active();
        active.setMatrix(matrix);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new MatrixPane<CompartmentalisedMetabolite, String>(matrix));
        frame.setVisible(true);

        updateMenuContext();

        return true;
    }
}
