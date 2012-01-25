/**
 * NewFromENA.java
 *
 * 2011.10.17
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
import java.util.List;
import javax.swing.JFileChooser;
import javax.xml.stream.XMLStreamException;
import org.apache.log4j.Logger;
import org.biojava3.core.sequence.ChromosomeSequence;
import uk.ac.ebi.core.ChromosomeImplementation;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.Chromosome;
import uk.ac.ebi.interfaces.Gene;
import uk.ac.ebi.interfaces.GeneProduct;
import uk.ac.ebi.io.xml.ENAXMLReader;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.main.MainView;

/**
 *          NewFromENA - 2011.10.17 <br>
 *          Imports ENA XML format genome/proteome
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ImportENAXML extends FileChooserAction {

    private static final Logger LOGGER = Logger.getLogger(ImportENAXML.class);

    public ImportENAXML() {
        super(ImportENAXML.class.getSimpleName());
    }

    @Override
    public void activateActions() {

        getChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
        File choosenFile = getFile(showOpenDialog());

        if (choosenFile instanceof File) {
            try {
                ENAXMLReader reader = new ENAXMLReader(new FileInputStream(choosenFile));

                List<GeneProduct> products = reader.getProducts();

                ReconstructionManager manager = ReconstructionManager.getInstance();
                Reconstruction recon = manager.getActiveReconstruction();
                recon.getProducts().addAll(products);

                Chromosome c = new ChromosomeImplementation(1, new ChromosomeSequence(reader.getGenomeString()));
                List<Gene> genes = reader.getGenes();
                for (Gene gene : genes) {
                    c.add(gene);
                }
                recon.getGenome().add(c);


                MainView.getInstance().update();

            } catch (FileNotFoundException ex) {
                MainView.getInstance().getMessageManager().addReport(new ErrorMessage("File not found " + ex.getMessage()));
            } catch (XMLStreamException ex) {
                MainView.getInstance().getMessageManager().addReport(new ErrorMessage(ex.getMessage()));
            }
        }

    }
}
