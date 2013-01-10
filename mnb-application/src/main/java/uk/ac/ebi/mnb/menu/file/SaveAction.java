/**
 * SaveAction.java
 *
 * 2012.01.31
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
package uk.ac.ebi.mnb.menu.file;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.caf.utility.preference.type.IntegerPreference;
import uk.ac.ebi.caf.utility.version.Version;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.DomainPreferences;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.io.IOConstants;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.main.MainView;

import java.awt.event.ActionEvent;
import java.io.File;


/**
 * SaveAction 2012.01.31
 *
 * @author johnmay
 * @author $Author$ (this version)
 *
 *         Class description
 * @version $Rev$ : Last Changed $Date$
 */
public class SaveAction extends GeneralAction {

    private static final Logger LOGGER = Logger.getLogger(SaveAction.class);


    public SaveAction() {
        super("SaveProject");
    }


    public void actionPerformed(ActionEvent e) {
        try {
            DefaultReconstructionManager manager = DefaultReconstructionManager.getInstance();
            Reconstruction reconstruction = manager.getActive();

            Version version = IOConstants.VERSION;

            long start = System.currentTimeMillis();
            ReconstructionIOHelper.write(reconstruction, reconstruction.getContainer());
            long end = System.currentTimeMillis();

            LOGGER.info("Wrote reconstruction in " + (end - start) + " ms");


        } catch (Exception ex) {
            MainView.getInstance().getMessageManager().addReport(new ErrorMessage("Unable to save reconstruction: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }
}
