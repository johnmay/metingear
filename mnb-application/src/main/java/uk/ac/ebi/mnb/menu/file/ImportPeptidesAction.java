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
import uk.ac.ebi.chemet.resource.basic.BasicProteinIdentifier;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.ProteinProductImpl;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
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
    private FileFilter filter = new FileFilter() {
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
        Reconstruction recon = DefaultReconstructionManager.getInstance().getActive();

        if (recon == null) {
            MainView.getInstance().addErrorMessage("No active reconstruction to import peptides into");
            return;
        }


        getChooser().setFileFilter(filter);
        getChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
        File choosenFile = getFile(showOpenDialog());

        Collection<GeneProduct> peptides = new ArrayList();

        if (choosenFile != null) {


            FileInputStream inStream = null;
            try {
                inStream = new FileInputStream(choosenFile);
                FastaReader<ProteinSequence, AminoAcidCompound> fastaReader =
                        new FastaReader<ProteinSequence, AminoAcidCompound>(
                                inStream,
                                new GenericFastaHeaderParser<ProteinSequence, AminoAcidCompound>(),
                                new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
                LinkedHashMap<String, ProteinSequence> b = fastaReader.process();

                for (Entry<String, ProteinSequence> entry : b.entrySet()) {

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
                    }

                }
            } catch (Exception ex) {
                logger.error("error reading Fasta file: ", ex);
            } finally {
                try {
                    inStream.close();
                } catch (IOException ex) {
                    logger.error("could not close stream");
                }
            }
        }

        recon.getProducts().addAll(peptides);

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
