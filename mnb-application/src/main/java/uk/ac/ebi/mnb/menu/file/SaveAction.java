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
import uk.ac.ebi.mdk.io.*;
import uk.ac.ebi.mdk.domain.DomainPreferences;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.main.MainView;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Properties;


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
            DefaultReconstructionManager manager = DefaultReconstructionManager.getInstance();
            Reconstruction reconstruction = manager.getActive();

            IntegerPreference bufferPref = DomainPreferences.getInstance().getPreference("BUFFER_SIZE");

            reconstruction.getContainer().mkdirs();

            File entities     = new File(reconstruction.getContainer(), "entities");
            File annotations  = new File(reconstruction.getContainer(), "entity-annotations");
            File observations = new File(reconstruction.getContainer(), "entity-observations");
            File info         = new File(reconstruction.getContainer(), "info.properties");

            Version version = IOConstants.VERSION;


            Properties properties = new Properties();

            if(info.exists()) {
                FileInputStream propInput =   new FileInputStream(info);
                properties.load(propInput);
                propInput.close();
                String value = properties.getProperty("chemet.version");
                version = value == null ? version : new Version(value);
            }

            properties.put("chemet.version", version.toString());
            FileWriter writer = new FileWriter(info);
            properties.store(writer, "Project info");
            writer.close();

            DataOutputStream entityDataOut      = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(entities), bufferPref.get()));
            DataOutputStream annotationDataOut  = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(annotations), bufferPref.get()));
            DataOutputStream observationDataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(observations), bufferPref.get()));

            EntityFactory factory = DefaultEntityFactory.getInstance();

            AnnotationOutput annotationOutput  = new AnnotationDataOutputStream(annotationDataOut, version);
            ObservationOutput observationOutput = new ObservationDataOutputStream(observationDataOut, version);
            EntityOutput entityOutput      = new EntityDataOutputStream(version,
                                                                              entityDataOut,
                                                                              factory,
                                                                              annotationOutput,
                                                                              observationOutput);
            
            long start = System.currentTimeMillis();
            entityOutput.write(reconstruction);
            long end = System.currentTimeMillis();

            LOGGER.info("Wrote reconstruction in " + (end - start) + " ms");

            entityDataOut.close();
            annotationDataOut.close();
            observationDataOut.close();


        } catch (Exception ex) {
            MainView.getInstance().getMessageManager().addReport(new ErrorMessage("Unable to save reconstruction: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }
}
