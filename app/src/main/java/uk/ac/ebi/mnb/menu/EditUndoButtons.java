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
package uk.ac.ebi.mnb.menu;

import com.explodingpixels.macwidgets.LabeledComponentGroup;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.caf.utility.font.EBIIcon;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;


/**
 *          EditUndoButtons - 2011.12.09 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class EditUndoButtons {

    private static final Logger LOGGER = Logger.getLogger(EditUndoButtons.class);

    private JButton back;

    private JButton forward;


    public EditUndoButtons() {

        back = new JButton();
        forward = new JButton();

        back.putClientProperty("JButton.buttonType", "segmentedTextured");
        back.putClientProperty("JButton.segmentPosition", "first");
        back.setFocusable(false);
        forward.putClientProperty("JButton.buttonType", "segmentedTextured");
        forward.putClientProperty("JButton.segmentPosition", "last");
        forward.setFocusable(false);

        back.setAction(new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                UndoManager manager = MainView.getInstance().getUndoManager();
                if (manager.canUndo()) {
                    manager.undo();
                    MainView.getInstance().update();
                }
            }
        });
        back.setIcon(EBIIcon.UNDO.create().icon());
        back.setToolTipText("Undo");
        forward.setAction(new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                UndoManager manager = MainView.getInstance().getUndoManager();
                if (manager.canRedo()) {
                    manager.redo();
                    MainView.getInstance().update();
                }
            }
        });
        forward.setIcon(EBIIcon.REDO.create().icon());
        forward.setToolTipText("Redo");



    }


    public LabeledComponentGroup getButtonGroup() {
        return new LabeledComponentGroup("Undo Redo", back, forward);
    }
}
