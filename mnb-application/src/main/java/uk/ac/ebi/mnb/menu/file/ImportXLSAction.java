/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import uk.ac.ebi.chemet.service.query.LuceneServiceManager;
import uk.ac.ebi.core.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mnb.core.FileChooserAction;
import uk.ac.ebi.mnb.importer.xls.wizzard.ExcelImportDialog;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.parser.ExcelHelper;
import uk.ac.ebi.mnb.parser.ExcelXLSHelper;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
                if (f.isDirectory()) {
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

        if (choosenFile != null && choosenFile.isFile()) {
            String name = choosenFile.getName();
            try {
                Reconstruction reconstruction = DefaultReconstructionManager.getInstance().getActive();
                // todo. add XLSX implementation
                InputStream stream = new FileInputStream(choosenFile);
                ExcelHelper importer = name.endsWith(".xls")
                                       ? new ExcelXLSHelper(stream) : null;
                MainView view = MainView.getInstance();
                ExcelImportDialog wizzard = new ExcelImportDialog(view, view.getViewController(), view.getMessageManager(), view.getViewController(), view.getUndoManager(), reconstruction, choosenFile, importer, LuceneServiceManager.getInstance());
                wizzard.setVisible(true);

                // update the views
                MainView.getInstance().getSourceListController().update();
                MainView.getInstance().getViewController().update();
                System.out.println("done..");

            } catch (Exception ex) {
                MainView.getInstance().addErrorMessage("Unable to import Excel file: " + ex.getMessage());
            }
        }
        MainView.getInstance().getJMenuBar().updateContext();


    }
}
