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
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.view.ContextDialog;

/**
 * @name    SelectionMenuItem - 2011.10.03 <br>
 *          Similar to drop down menu item but with SelectionDialog
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ContextDialogItem extends JMenuItem {

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
    public ContextDialogItem(final JFrame frame, final ViewController controller, final Class<? extends ContextDialog> clazz) {
        //super(frame, name, constructor);

        super(new DelayedBuildAction(clazz.getSimpleName()) {

            private ContextDialog dialog;

            @Override
            public void buildComponents() {
                try {
                    Constructor constructor = clazz.getConstructors()[0];
                    dialog = (ContextDialog) constructor.newInstance(frame, controller);
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
                if (dialog.getSelection().hasSelection()) {
                    dialog.setVisible(true);
                }
            }
        });
    }

}
