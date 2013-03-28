/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.dialog.file;

import org.apache.log4j.Logger;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;


/**
 * ExportMetabolitesMDL - 2011.10.20 <br> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
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

        Reconstruction recon = DefaultReconstructionManager.getInstance().active();

        Collection<Metabolite> metabolites =
                selection.hasSelection(Metabolite.class)
                ? selection.get(Metabolite.class)
                : recon.getMetabolome().toList();


        fileChooser = fileChooser == null ? new JFileChooser() : fileChooser;


        fileChooser.showSaveDialog((JFrame) controller);
        File file = fileChooser.getSelectedFile();

        if (file.exists()) {

            int option = JOptionPane.showConfirmDialog(fileChooser, "Are you sure you want to save overwrite '" + file + "' ?");

            if (option == JOptionPane.NO_OPTION) {
                actionPerformed(e);
                return;
            }

        }
        SDFWriter writer = null;

        try {
            writer = new SDFWriter(new BufferedWriter(new FileWriter(file)));


            String recordSeparator = "$$$$" + System.getProperty("line.separator");

            for (Metabolite m : metabolites) {
                if (m.hasStructure()) {
                    for (AtomContainerAnnotation structure : m.getAnnotations(AtomContainerAnnotation.class)) {

                        IAtomContainer container = structure.getStructure();
                        container.setProperty(CDKConstants.TITLE, m.getAccession());

                        if (!m.getName().isEmpty())
                            container.setProperty("name", m.getName());
                        if (!m.getAbbreviation().isEmpty())
                            container.setProperty("abbreviation", m.getAbbreviation());

                        writer.write(container);


                    }
                }
            }

        } catch (IOException ex) {
            addMessage(new ErrorMessage("internal error: " + ex.getMessage()));
        } catch (CDKException ex) {
            addMessage(new ErrorMessage("internal error: " + ex.getMessage()));
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e1) {
                // can't do anything
            }
        }


    }
}
