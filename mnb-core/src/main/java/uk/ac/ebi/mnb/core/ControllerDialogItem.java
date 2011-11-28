/**
 * SelectionMenuItem.java
 *
 * 2011.10.03
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
package uk.ac.ebi.mnb.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.event.UndoableEditListener;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;


/**
 * @name    SelectionMenuItem - 2011.10.03 <br>
 *          Similar to drop down menu item but with SelectionDialog
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ControllerDialogItem extends JMenuItem {

    public ControllerDialogItem(MainController controller, Class<? extends ControllerDialog> clazz) {
        this((JFrame) controller,
             (TargetedUpdate) controller.getViewController(),
             controller.getMessageManager(),
             (SelectionController) controller.getViewController(),
             controller.getUndoManager(),
             clazz);
    }

    /**
     * Constructs a SelectionMenuItem using a frame (to place dialog – frame should implement dialog controller) and
     * a ViewController for getting/setting selection. The class should have one constructor only as reflection
     * is used to build the dialog. The classes simple name is what is used to lookup properties in the ActionProperties
     * class.
     * 
     * @param frame
     * @param controller
     * @param clazz
     */
    public ControllerDialogItem(final JFrame frame,
                                final TargetedUpdate updater,
                                final MessageManager mesg,
                                final SelectionController selection,
                                final UndoableEditListener editListener,
                                final Class<? extends ControllerDialog> clazz) {

        super(new DelayedBuildAction(clazz.getSimpleName()) {

            private ControllerDialog dialog;

            @Override
            public void buildComponents() {
                try {
                    Constructor constructor = clazz.getConstructors()[0];
                    dialog = (ControllerDialog) constructor.newInstance(frame,
                                                                        updater,
                                                                        mesg,
                                                                        selection,
                                                                        editListener);
                } catch (InstantiationException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void activateActions() {
                dialog.setVisible(true);
            }
        });
    }
}
