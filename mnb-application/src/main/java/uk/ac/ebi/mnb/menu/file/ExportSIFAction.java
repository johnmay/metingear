/*
 *     This file is part of Metabolic Network Builder
 *
 *     Metabolic Network Builder is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.menu.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import uk.ac.ebi.mnb.view.ViewUtils;
import mnb.view.old.MatrixModel;
import mnb.view.old.MatrixView;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.core.FileChooserAction;

/**
 * ExportCytoScape.java
 *
 *
 * @author johnmay
 * @date May 19, 2011
 */
public class ExportSIFAction
        extends FileChooserAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ExportSIFAction.class );

    public ExportSIFAction() {
        super( "ExportCytoScape" );
    }

    @Override
    public void activateActions() {
        throw new UnsupportedOperationException("Old method");
//        MatrixView matrixView = MainFrame.getInstance().getViewController().getMatrixView();
//        if ( matrixView == null ) {
//            MainFrame.getInstance().addErrorMessage( "Unable to obtain the matrix from the project. Have you used Build > StoichiometricMatrix before exporting"  );
//            return;
//        }
//        MatrixModel model = matrixView.getModel();
//        File choosenFile = getFile( showSaveDialog() );
//
//        if ( choosenFile.exists() ) {
//            String mesg = choosenFile.toString() + " already exists. Are you sure you want to save over this file?";
//            String[] values = new String[]{ "Yes" , "No" };
//            String value = "Yes";
//            int selected = JOptionPane.showOptionDialog( null , mesg , "Warning" , JOptionPane.YES_NO_CANCEL_OPTION , JOptionPane.WARNING_MESSAGE , ViewUtils.icon_64x64 , values , value );
//            if ( selected == -1 ) {
//                logger.debug( "user cancelled" );
//                return;
//            } else if ( selected == 1 ) {
//                activateActions();
//            }
//        }
//
//
//        try {
//            FileWriter fw = new FileWriter( choosenFile );
//            model.toTextFile( fw );
//            fw.close();
//        } catch ( IOException ex ) {
//            MainFrame.getInstance().addErrorMessage("There was a problem when writing " + choosenFile);
//        }


    }
}
