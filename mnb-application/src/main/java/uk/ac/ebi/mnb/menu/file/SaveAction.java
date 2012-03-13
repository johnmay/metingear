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
import uk.ac.ebi.chemet.io.annotation.AnnotationDataOutputStream;
import uk.ac.ebi.chemet.io.annotation.AnnotationOutput;
import uk.ac.ebi.chemet.io.entity.EntityDataOutputStream;
import uk.ac.ebi.chemet.io.entity.EntityOutput;
import uk.ac.ebi.chemet.io.observation.ObservationDataOutputStream;
import uk.ac.ebi.chemet.io.observation.ObservationOutput;
import uk.ac.ebi.core.CorePreferences;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.entities.EntityFactory;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.main.MainView;

import java.awt.event.ActionEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;


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
    }


    public void actionPerformed(ActionEvent e) {
        try {
            ReconstructionManager manager = ReconstructionManager.getInstance();
            Reconstruction reconstruction = manager.getActive();

            IntegerPreference bufferPref = CorePreferences.getInstance().getPreference("BUFFER_SIZE");

            reconstruction.getContainer().mkdirs();

            File entities     = new File(reconstruction.getContainer(), "entities");
            File annotations  = new File(reconstruction.getContainer(), "entity-annotations");
            File observations = new File(reconstruction.getContainer(), "entity-observations");

            DataOutputStream entityDataOut = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(entities), bufferPref.get()));
            DataOutputStream annotationDataOut = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(annotations), bufferPref.get()));
            DataOutputStream observationDataOut = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(observations), bufferPref.get()));

            Version version       = new Version("0.9");
            EntityFactory factory = DefaultEntityFactory.getInstance();

            AnnotationOutput   annotationOutput  = new AnnotationDataOutputStream(annotationDataOut, version);
            ObservationOutput  observationOutput = new ObservationDataOutputStream(observationDataOut, version);
            EntityOutput       entityOutput      = new EntityDataOutputStream(version,
                                                                              entityDataOut,
                                                                              factory,
                                                                              annotationOutput,
                                                                              observationOutput);
            
            long start = System.currentTimeMillis();
            entityOutput.write(reconstruction);
            long end = System.currentTimeMillis();

            LOGGER.info("Wrote reconstruction in " + (end - start) + " ms");

            entityDataOut.close();


        } catch (Exception ex) {
            MainView.getInstance().getMessageManager().addReport(new ErrorMessage("Unable to save reconstruction: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }
}
