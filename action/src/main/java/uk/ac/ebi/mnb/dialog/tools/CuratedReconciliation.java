/**
 * ResolveMissingInfo.java
 *
 * 2012.01.11
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.dialog.tools;

import mnb.io.resolve.CandidateSelector;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ResolveMissingInfo 2012.01.11
 *
 * @author johnmay
 * @author $Author$ (this version)
 *         <p/>
 *         Class provides resolution to small molecule metabolites that are not assigned
 *         to a database or structure
 * @version $Rev$ : Last Changed $Date$
 */
public class CuratedReconciliation
        extends ControllerAction {

    private CandidateSelector dialog;

    public CuratedReconciliation(MainController controller) {
        super(CuratedReconciliation.class.getSimpleName(), controller);
        dialog = new CandidateSelector((JFrame) controller);
    }


    public void actionPerformed(ActionEvent e) {

        EntityCollection manager = getSelection();

        dialog.setSkipall(false); // reset the skip-all flag

        for (Metabolite metabolite : manager.get(Metabolite.class)) {
            dialog.setup(metabolite);
            dialog.setVisible(true);
        }

        super.update(manager);

    }
}
