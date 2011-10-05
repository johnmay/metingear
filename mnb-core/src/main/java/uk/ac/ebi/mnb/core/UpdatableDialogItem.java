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
import uk.ac.ebi.mnb.interfaces.Updatable;
import uk.ac.ebi.mnb.view.ContextDialog;
import uk.ac.ebi.mnb.view.DropdownDialog;

/**
 * @name    SelectionMenuItem - 2011.10.03 <br>
 *          Allows easy creation of Updatable dialogs in menus
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class UpdatableDialogItem extends JMenuItem {

    public UpdatableDialogItem(final JFrame frame, final Updatable updatable, final Class<?> clazz) {
        //super(frame, name, constructor);

        super(new DelayedBuildAction(clazz.getSimpleName()) {

            private DropdownDialog dialog;

            @Override
            public void buildComponents() {
                try {
                    Constructor constructor = clazz.getConstructors()[0];
                    dialog = (DropdownDialog) constructor.newInstance(frame, updatable);
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
