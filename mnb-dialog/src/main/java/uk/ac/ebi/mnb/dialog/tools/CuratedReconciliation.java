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

import uk.ac.ebi.interfaces.entities.EntityCollection;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import mnb.io.resolve.CandidateSelector;
import uk.ac.ebi.core.metabolite.MetaboliteClassImplementation;
import uk.ac.ebi.interfaces.entities.Metabolite;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.*;


/**
 *
 * ResolveMissingInfo 2012.01.11
 *
 * @version $Rev$ : Last Changed $Date$
 * @author johnmay
 * @author $Author$ (this version)
 *
 * Class provides resolution to small molecule metabolites that are not assigned
 * to a database or structure
 *
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

            if (metabolite.getType() != MetaboliteClassImplementation.PROTEIN) {
                dialog.setup(metabolite);
                dialog.setVisible(true);
            }

        }

        super.update(manager);

    }
}
