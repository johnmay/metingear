/**
 * ExportMetabolitesMDL.java
 *
 * 2011.10.20
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
package uk.ac.ebi.mnb.dialog.file;

import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Writer;
import uk.ac.ebi.annotation.chemical.AtomContainerAnnotation;
import uk.ac.ebi.core.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;


/**
 *          ExportMetabolitesMDL - 2011.10.20 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ExportMetabolitesMDL extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(ExportMetabolitesMDL.class);

    private JFileChooser fileChooser;

    private MainController controller;


    public ExportMetabolitesMDL(MainController controller) {
        super(ExportMetabolitesMDL.class.getSimpleName(), controller);
        this.controller = controller;
    }


    public void actionPerformed(ActionEvent e) {

        EntityCollection selection = getSelection();

        Reconstruction recon = DefaultReconstructionManager.getInstance().getActive();

        Collection<Metabolite> metabolites = selection.hasSelection(Metabolite.class)
                                             ? selection.get(Metabolite.class)
                                             : recon.getMetabolome();


        fileChooser = fileChooser == null ? new JFileChooser() : fileChooser;


        fileChooser.showSaveDialog((JFrame) controller);
        File file = fileChooser.getSelectedFile();

        if (file.exists()) {

            int option = JOptionPane.showConfirmDialog(fileChooser, "Are you sure you want to save overwrite '" + file + "' ?");

            if (option == JOptionPane.NO_OPTION) {
                actionPerformed(e);
                return;
            }

        } else {

            try {
                // check if overwritting

                MDLV2000Writer writer = new MDLV2000Writer(new FileOutputStream(file));

                for (Metabolite m : metabolites) {
                    if (m.hasStructure()) {
                        for (AtomContainerAnnotation structure : m.getAnnotations(AtomContainerAnnotation.class)) {
                            writer.write(structure.getStructure());
                        }
                    }
                }

            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ExportMetabolitesMDL.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CDKException ex) {
            }
        }


    }
}
