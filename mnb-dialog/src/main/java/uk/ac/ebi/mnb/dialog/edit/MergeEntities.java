/**
 * MergeEntities.java
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

import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.entities.EntityCollection;
import uk.ac.ebi.interfaces.entities.MetabolicParticipant;
import uk.ac.ebi.interfaces.entities.MetabolicReaction;
import uk.ac.ebi.interfaces.entities.Metabolite;
import uk.ac.ebi.interfaces.reaction.Participant;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.resource.chemical.BasicChemicalIdentifier;


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
    public JPanel getOptions() {
        return super.getOptions();
    }


    @Override
    public void process() {

        selection = getSelection();
        Collection<Metabolite> entities = selection.get(Metabolite.class);
        // create a new metabolite consisting of the other two.
        // find them in all reactions and update reactions also
        Metabolite n = DefaultEntityFactory.getInstance().newInstance(Metabolite.class);
        ;
        Reconstruction recon = ReconstructionManager.getInstance().getActive();

        StringBuilder accessionBuilder = new StringBuilder();
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder abbrBuilder = new StringBuilder();

        for (Metabolite m : entities) {

            accessionBuilder.append(m.getAccession());
            nameBuilder.append(m.getName());
            abbrBuilder.append(m.getAbbreviation());

            n.addAnnotations(m.getAnnotations());

            for (MetabolicReaction rxn : recon.getReactions().getReactions(m)) {
                for (MetabolicParticipant p : rxn.getReactants()) {
                    if (p.getMolecule() == m) { // do a direct reference compare
                        p.setMolecule(n);
                    }
                }
            }
            recon.getMetabolites().remove(m);
        }

        n.setIdentifier(new BasicChemicalIdentifier(accessionBuilder.toString()));
        n.setName(nameBuilder.toString());
        n.setAbbreviation(abbrBuilder.toString());


        recon.addMetabolite(n);


        recon.getReactions().rebuildParticipantMap();
        //        recon.remove // remove metabolite

    }


    @Override
    public void setVisible(boolean visible) {

//        // check they're all the same class

        super.setVisible(visible);
    }
}
