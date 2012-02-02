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

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.zip.GZIPOutputStream;
import javax.swing.Action;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.caf.utility.preference.type.IntegerPreference;
import uk.ac.ebi.caf.utility.version.Version;
import uk.ac.ebi.core.CorePreferences;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.io.core.ReconstructionOutputStream;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.main.MainView;


/**
 *
 *          SaveAction 2012.01.31
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *
 *          Class description
 *
 */
public class SaveAction extends GeneralAction {

    private static final Logger LOGGER = Logger.getLogger(SaveAction.class);


    public SaveAction() {
        super("SaveProject");
        putValue(Action.NAME, "Alternate save");
    }


    public void actionPerformed(ActionEvent e) {
        try {
            ReconstructionManager manager = ReconstructionManager.getInstance();
            Reconstruction reconstruction = manager.getActive();

            IntegerPreference bufferPref = CorePreferences.getInstance().getPreference("BUFFER_SIZE");
            OutputStream out = new GZIPOutputStream(new FileOutputStream(new File("/Users/johnmay/" + reconstruction.getContainer(), "data")),
                                                    bufferPref.get());

            ReconstructionOutputStream ros = new ReconstructionOutputStream(out, new Version(0, 8, 5, 0));

            long start = System.currentTimeMillis();
            ros.write(reconstruction);
            long end = System.currentTimeMillis();

            LOGGER.info("Wrote reconstruction in " + (end - start) + " ms");

            ros.close();


        } catch (Exception ex) {
            MainView.getInstance().getMessageManager().addReport(new ErrorMessage("Unable to save reconstruction: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }
}
