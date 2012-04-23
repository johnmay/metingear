/**
 * ImportKGML.java
 *
 * 2011.10.26
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamException;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.DefaultReconstructionManager;
import uk.ac.ebi.core.MetabolicReactionImplementation;
import uk.ac.ebi.core.ReconstructionImpl;
import uk.ac.ebi.io.xml.KGMLReader;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.main.MainView;

/**
 *          ImportKGML - 2011.10.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ImportKGML extends FileChooserAction {

    private static final Logger LOGGER = Logger.getLogger(ImportKGML.class);

    public ImportKGML() {
        super(ImportKGML.class.getSimpleName());
    }

    @Override
    public void activateActions() {

        if(!DefaultReconstructionManager.getInstance().hasProjects()){
            MainView.getInstance().addWarningMessage("No reconstructions available");
            return;
        }

        File f = getFile(getChooser().showOpenDialog(MainView.getInstance()));
        if(f != null){
            InputStream stream = null;
            try {
                stream = new FileInputStream(f);
                KGMLReader reader = new KGMLReader(stream);
                for(MetabolicReactionImplementation rxn : reader.getReactions()){
                    ReconstructionImpl recon = DefaultReconstructionManager.getInstance().getActive();
                    recon.addReaction(rxn);
                }
                MainView.getInstance().update();
            } catch (XMLStreamException ex) {
                java.util.logging.Logger.getLogger(ImportKGML.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(ImportKGML.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    stream.close();
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(ImportKGML.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}
