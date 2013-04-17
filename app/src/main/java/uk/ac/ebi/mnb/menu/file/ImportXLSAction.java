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

import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.service.DefaultServiceManager;
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
                Reconstruction reconstruction = DefaultReconstructionManager.getInstance().active();
                // todo. add XLSX implementation
                InputStream stream = new FileInputStream(choosenFile);
                ExcelHelper importer = name.endsWith(".xls")
                                       ? new ExcelXLSHelper(stream) : null;
                MainView view = MainView.getInstance();
                ExcelImportDialog wizzard = new ExcelImportDialog(view, view.getViewController(), view.getMessageManager(), view.getViewController(), view.getUndoManager(), reconstruction, choosenFile, importer, DefaultServiceManager.getInstance());

                wizzard.pack();
                wizzard.setVisible(true);


                // update the views
                MainView.getInstance().getSourceListController().update();
                MainView.getInstance().getViewController().update();

            } catch (Exception ex) {
                MainView.getInstance().addErrorMessage("Unable to import Excel file: " + ex.getMessage());
            }
        }
        MainView.getInstance().getJMenuBar().updateContext();


    }
}
