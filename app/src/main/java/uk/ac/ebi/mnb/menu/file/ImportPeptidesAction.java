/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava3.core.sequence.io.FastaReader;
import org.biojava3.core.sequence.io.GenericFastaHeaderParser;
import org.biojava3.core.sequence.io.ProteinSequenceCreator;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.ProteinProductImpl;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicProteinIdentifier;
import uk.ac.ebi.mdk.domain.identifier.type.ProteinIdentifier;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * ImportSBMLAction.java
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class ImportPeptidesAction extends FileChooserAction {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
                    ImportPeptidesAction.class);
    private              FileFilter              filter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            String path = f.getPath();
            int lastIndex = path.lastIndexOf(".");
            if (lastIndex != -1) {
                String extension = path.substring(lastIndex);
                if (extension.matches(".fa|.fasta|.faa|.fna")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Fasta Files (.fa, .fasta, .faa, .fna)";
        }

    };

    public ImportPeptidesAction() {
        super("ImportFastaAA");
    }

    @Override
    public void activateActions() {

        // add the peptides to the active project and updateObservations the annotations table
        Reconstruction recon = DefaultReconstructionManager.getInstance().active();

        if (recon == null) {
            MainView.getInstance().addErrorMessage("No active reconstruction to import peptides into");
            return;
        }


        getChooser().setFileFilter(filter);
        getChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
        File choosenFile = getFile(showOpenDialog());

        Collection<GeneProduct> peptides = new ArrayList<GeneProduct>();

        if (choosenFile != null) {


            FileInputStream in = null;

            try {
                in = new FileInputStream(choosenFile);


                FastaReader<ProteinSequence, AminoAcidCompound> reader =
                        new FastaReader<ProteinSequence, AminoAcidCompound>(
                                in,
                                // we could have a custom header parser but we're not concerned with handling protein source etc atm
                                new GenericFastaHeaderParser<ProteinSequence, AminoAcidCompound>(),
                                new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));

                LinkedHashMap<String, ProteinSequence> sequences = reader.process();

                for (Entry<String, ProteinSequence> entry : sequences.entrySet()) {

                    String header = entry.getValue().getOriginalHeader();

                    if (header.contains(" ")) {
                        ProteinIdentifier identifer = new BasicProteinIdentifier(header.substring(0,
                                                                                                  header.indexOf(" ")));
                        String description = header.substring(header.indexOf(" ") + 1);
                        GeneProduct p = new ProteinProductImpl(identifer,
                                                               "",
                                                               description);
                        p.addSequence(entry.getValue());
                        peptides.add(p);
                    } else {
                        ProteinIdentifier identifer = new BasicProteinIdentifier(header);
                        GeneProduct p = new ProteinProductImpl(identifer,
                                                               "",
                                                               "");
                        p.addSequence(entry.getValue());
                        peptides.add(p);
                    }

                }

            } catch (Exception ex) {
                logger.error("error reading Fasta file: ", ex);
                MainView.getInstance().addErrorMessage("Unable to read peptides from fasta file: " + ex.getMessage());
            } finally {

                try {
                    if(in != null)
                        in.close();
                } catch (IOException ex) {
                    logger.error("could not close stream");
                }

            }
        }

        for(GeneProduct p : peptides){
            recon.addProduct(p);
        }

        ////        AbstractIdentifier[] clashingIdentifiers = ReconstructionManager.getInstance().
        ////          getActiveReconstruction().getProducts().addAll(project);
        //
        //        if( clashingIdentifiers.length > 0 ) {
        //            JOptionPane.showMessageDialog(MainView.getInstance(),
        //                                          clashingIdentifiers.length +
        //                                          " products had matching identifiers and were not imported",
        //                                          "Warning",
        //                                          JOptionPane.WARNING_MESSAGE);
        //        }
        MainView.getInstance().getSourceListController().update();
        MainView.getInstance().getViewController().update();


    }
}
