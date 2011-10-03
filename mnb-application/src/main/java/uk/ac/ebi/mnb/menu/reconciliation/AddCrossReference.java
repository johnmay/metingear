/**
 * AddCrossReference.java
 *
 * 2011.09.26
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
package uk.ac.ebi.mnb.menu.reconciliation;

import uk.ac.ebi.mnb.core.DelayedBuildAction;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.EntityView;
import uk.ac.ebi.mnb.main.MainFrame;

/**
 *          AddCrossReference – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AddCrossReference
        extends DelayedBuildAction {

    private static final Logger LOGGER = Logger.getLogger(AddCrossReference.class);
    private AddCrossReferenceDialog dialog;

    public AddCrossReference() {
        super("AddCrossReference");
    }

    @Override
    public void buildComponents() {
        dialog = new AddCrossReferenceDialog();
    }

    @Override
    public void activateActions() {

        // check the items are vaild first
        EntityView view = MainFrame.getInstance().getViewController().
                getActiveView();
        if (view == null) {
            MainFrame.getInstance().addErrorMessage("Unable to add cross reference, no active view available");

        } else {
            AnnotatedEntity reconComponent = view.getSelectedEntity();
            if (reconComponent == null) {
                MainFrame.getInstance().addErrorMessage("Unable to add cross reference, no active component available");
            } else {
                dialog.setComponent(reconComponent);
                dialog.setVisible(true);
            }
        }
    }
}
