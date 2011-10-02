/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.stream.XMLStreamException;
import uk.ac.ebi.core.ReconstructionManager;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.xml.stax.SBMLWriter;

import uk.ac.ebi.metabolomes.core.gene.GeneProteinProduct;
import uk.ac.ebi.metabolomes.core.compound.MetaboliteCollection;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.metabolomes.identifier.UniqueIdentifier;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.core.FileChooserAction;
import org.apache.commons.lang.StringUtils;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.io.sbml.SBMLIOUtil;


/**
 * ExportSBMLAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class ExportSBMLAction
  extends FileChooserAction {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      ExportSBMLAction.class);


    public ExportSBMLAction() {
        super("ExportSBML");
    }


    @Override
    public void activateActions() {
        try {

            File sbmlOut = getFile(showSaveDialog());
            int level = 2;
            int version = 1;

            SBMLIOUtil util = new SBMLIOUtil(level, version);

            SBMLDocument document = util.getDocument(ReconstructionManager.getInstance().getActiveReconstruction());

            SBMLWriter writer = new SBMLWriter();

            writer.write(document, sbmlOut);

        } catch( IOException ex ) {
            MainFrame.getInstance().addErrorMessage("Invalid file " + ex.getMessage());
        } catch( SBMLException ex ) {
            MainFrame.getInstance().addErrorMessage("There was an unknown error when exporting reconstruction to SBML" +
                                                   ex);
        } catch( XMLStreamException ex ) {
            MainFrame.getInstance().addErrorMessage("Error writing SBML document to disk");
        }

    }


}

