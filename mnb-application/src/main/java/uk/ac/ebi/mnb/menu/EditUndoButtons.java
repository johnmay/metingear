/**
 * EditUndoButtons.java
 *
 * 2011.12.09
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.menu;

import com.explodingpixels.macwidgets.LabeledComponentGroup;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.undo.UndoManager;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.chemet.render.ViewUtilities;


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
        back.setIcon(ViewUtilities.getIcon("images/toolbar/back.png"));
        forward.setAction(new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                UndoManager manager = MainView.getInstance().getUndoManager();
                if (manager.canRedo()) {
                    manager.redo();
                    MainView.getInstance().update();
                }
            }
        });
        forward.setIcon(ViewUtilities.getIcon("images/toolbar/forward.png"));



    }


    public LabeledComponentGroup getButtonGroup() {
        return new LabeledComponentGroup("Edit", back, forward);
    }
}
