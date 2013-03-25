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

package uk.ac.ebi.mnb.view;

import com.jgoodies.forms.layout.CellConstraints;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.ui.component.ReconstructionComboBox;
import uk.ac.ebi.mdk.ui.component.ReconstructionFileChooser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * @author John May
 */
public class ReconstructionChooser {

    private static final Logger LOGGER = Logger.getLogger(ReconstructionChooser.class);

    private JPanel component;
    private ReconstructionComboBox combobox = new ReconstructionComboBox();
    private ReconstructionFileChooser chooser;

    public ReconstructionChooser() {
        chooser = new ReconstructionFileChooser();
    }


    public Reconstruction getSelected() {
        return combobox.getSelected();
    }

    public void refresh() {
        combobox.refresh();
    }

    private JPanel createComponent() {
        JPanel panel = PanelFactory.createDialogPanel("p, 4dlu, p", "p");
        CellConstraints cc = new CellConstraints();
        panel.add(combobox.getComponent(), cc.xy(1, 1));
        panel.add(ButtonFactory.newCleanButton(ResourceUtility.getIcon("/uk/ac/ebi/chemet/render/images/cutout/browse_16x16.png"),
                                               new AbstractAction() {
                                                   @Override
                                                   public void actionPerformed(ActionEvent e) {

                                                       int choice = chooser.showOpenDialog(component);

                                                       if (choice == JFileChooser.APPROVE_OPTION) {
                                                           File file = chooser.getSelectedFile();
                                                           try {

                                                               Reconstruction reconstruction = ReconstructionIOHelper.read(file);

                                                               DefaultReconstructionManager.getInstance().add(reconstruction);
                                                               combobox.refresh();
                                                               combobox.setSelected(reconstruction);

                                                           } catch (IOException ex) {
                                                               ex.printStackTrace();
                                                           } catch (ClassNotFoundException ex) {
                                                               ex.printStackTrace();
                                                           }
                                                       }
                                                   }

                                               }, "Open a reconstruction from disk"), cc.xy(3, 1));
        return panel;
    }

    public JComponent getComponent() {
        if (component == null)
            component = createComponent();
        return component;
    }

}

