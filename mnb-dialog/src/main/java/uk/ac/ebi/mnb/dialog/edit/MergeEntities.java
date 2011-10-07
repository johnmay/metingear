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
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.Updatable;

/**
 * @name    MergeEntities - 2011.10.04 <br>
 *          Class allows merging of entries
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MergeEntities extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(MergeEntities.class);

    public MergeEntities(JFrame frame,
            Updatable updater,
            MessageManager messages,
            SelectionController controller,
            UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, MergeEntities.class.getSimpleName());
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

        Collection<AnnotatedEntity> entities = getSelection();


        // create a new metabolite consisting of the other two.
        // find them in all reactions and update reactions also
        Metabolite m = (Metabolite) entities.iterator().next();
        Metabolite newMetabolite = new Metabolite();

        newMetabolite.setIdentifier(m.getIdentifier());
        newMetabolite.setName(m.getName());
        newMetabolite.setAbbreviation(m.getAbbreviation());
        newMetabolite.addAnnotations(m.getAnnotations());

        // add edit
        Reconstruction recon = ReconstructionManager.getInstance().getActiveReconstruction();
        recon.addMetabolite(newMetabolite);
//        recon.remove // remove metabolite

    }

    @Override
    public void setVisible(boolean visible) {

        // check they're all the same class
        Collection<AnnotatedEntity> entities = getSelection();
        Class entityclass = entities.iterator().next().getClass();
        for (AnnotatedEntity entity : entities) {
            if (entityclass == entity.getClass()) {
                addMessage(new ErrorMessage("Unable to merge items of different type"));
                return;
            }
        }
        super.setVisible(visible);
    }
}
