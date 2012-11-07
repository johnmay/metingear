/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.file;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.xml.stax.SBMLWriter;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.io.xml.sbml.SBMLIOUtil;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.main.MainView;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;


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
            int version = 4;

            SBMLIOUtil util = new SBMLIOUtil(DefaultEntityFactory.getInstance(), level, version);

            SBMLDocument document = util.getDocument(DefaultReconstructionManager.getInstance().getActive());

            SBMLWriter writer = new SBMLWriter();

            writer.write(document, sbmlOut);

        } catch( IOException ex ) {
            MainView.getInstance().addErrorMessage("Invalid file " + ex.getMessage());
        } catch( SBMLException ex ) {
            MainView.getInstance().addErrorMessage("There was an unknown error when exporting reconstruction to SBML" +
                                                   ex);
        } catch( XMLStreamException ex ) {
            MainView.getInstance().addErrorMessage("Error writing SBML document to disk");
        }

    }


}

