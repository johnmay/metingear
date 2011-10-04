/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import uk.ac.ebi.mnb.importer.xls.wizzard.ExcelWizzard;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.parser.ExcelImporter;
import uk.ac.ebi.mnb.parser.ExcelXLSImporter;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.core.Reconstruction;


/**
 * ImportSBMLAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class ImportXLSAction
  extends FileChooserAction {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      ImportXLSAction.class);


    public ImportXLSAction() {
        super("ImportXLS");
    }


    @Override
    public void activateActions() {

        // XLS/XLSX file filter
        getChooser().setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {

                // allow selection of directories for browsing
                if( f.isDirectory() ) {
                    return true;
                }
                String name = f.getName();
                return name.endsWith(".xls") || name.endsWith(".xlsx");
            }


            @Override
            public String getDescription() {
                return "Microsoft Excel Workbooks";
            }


        });

        getChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
        File choosenFile = getFile(showOpenDialog());

        if( choosenFile != null && choosenFile.isFile() ) {
            String name = choosenFile.getName();
            try {
                Reconstruction reconstruction = ReconstructionManager.getInstance().
                  getActiveReconstruction();
                // todo. add XLSX implementation
                InputStream stream = new FileInputStream(choosenFile);
                ExcelImporter importer = name.endsWith(".xls") ?
                                         new ExcelXLSImporter(stream) : null;
                ExcelWizzard wizzard = new ExcelWizzard(reconstruction, choosenFile, importer,
                                                        MainView.getInstance());
                wizzard.setVisible(true);

                // update the views
                MainView.getInstance().getSourceListController().update();
                MainView.getInstance().getViewController().update();
                System.out.println("done..");

            } catch( IOException ex ) {
                MainView.getInstance().addErrorMessage("Unable to import Excel file: " + ex.
                  getMessage());
            }
        }

    }


}

