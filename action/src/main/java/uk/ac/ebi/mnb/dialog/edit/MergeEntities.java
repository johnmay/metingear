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

package uk.ac.ebi.mnb.dialog.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicChemicalIdentifier;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.util.Collection;


/**
 * @name    MergeEntities - 2011.10.04 <br>
 *          Class allows merging of entries
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MergeEntities extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(MergeEntities.class);

    private EntityCollection selection;


    public MergeEntities(JFrame frame,
                         TargetedUpdate updater,
                         ReportManager messages,
                         SelectionController controller,
                         UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, MergeEntities.class.getSimpleName());
        setDefaultLayout();
    }


    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Merge multiple entries into one");
        return label;
    }


    @Override
    public JPanel getForm() {
        return super.getForm();
    }


    @Override
    public void process() {

        selection = getSelection();
        Collection<Metabolite> entities = selection.get(Metabolite.class);
        // create a new metabolite consisting of the other two.
        // find them in all reactions and update reactions also
        Metabolite n = DefaultEntityFactory.getInstance().newInstance(Metabolite.class);
        ;
        Reconstruction recon = DefaultReconstructionManager.getInstance().getActive();

        StringBuilder accessionBuilder = new StringBuilder();
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder abbrBuilder = new StringBuilder();

        for (Metabolite m : entities) {

            accessionBuilder.append(m.getAccession());
            nameBuilder.append(m.getName());
            abbrBuilder.append(m.getAbbreviation());

            n.addAnnotations(m.getAnnotations());

            for (MetabolicReaction rxn : recon.getReactome().getReactions(m)) {
                for (MetabolicParticipant p : rxn.getReactants()) {
                    if (p.getMolecule() == m) { // do a direct reference compare
                        p.setMolecule(n);
                    }
                }
            }
            recon.getMetabolome().remove(m);
        }

        n.setIdentifier(new BasicChemicalIdentifier().newInstance());
        n.setName(nameBuilder.toString());
        n.setAbbreviation(abbrBuilder.toString());


        recon.addMetabolite(n);


        recon.getReactome().rebuildMaps();
        //        recon.remove // remove metabolite

    }


    @Override
    public void setVisible(boolean visible) {

//        // check they're all the same class

        super.setVisible(visible);
    }
}
