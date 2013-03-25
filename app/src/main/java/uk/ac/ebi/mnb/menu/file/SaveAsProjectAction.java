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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.file;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.ui.component.ReconstructionFileChooser;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;


/**
 * SaveAsProjectAction.java
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class SaveAsProjectAction
        extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SaveAsProjectAction.class);

    private static final Logger LOGGER = Logger.getLogger(SaveAsProjectAction.class);
    private ReconstructionFileChooser chooser;

    public SaveAsProjectAction() {
        super("SaveAsProject");
    }

    @Override
    public void buildComponents() {
        chooser = new ReconstructionFileChooser();
    }

    @Override
    public void activateActions() {

        ReconstructionManager manager = DefaultReconstructionManager.getInstance();

        // get the name to choose
        int choice = chooser.showSaveDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();

            // add suffix if missing
            if (!f.getName().endsWith(".mr")) {
                f = new File(f.getPath() + ".mr");
            }

            Reconstruction reconstruction = manager.active();
            try {
                ReconstructionIOHelper.write(reconstruction, f);
            } catch (IOException e) {
                MainView.getInstance().addErrorMessage("unable to save reconstruction: " + e.getMessage());
            }

        }


    }
}
