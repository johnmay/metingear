/**
 * DialoggedMenuItem.java
 *
 * 2011.10.02
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import uk.ac.ebi.mnb.view.DropdownDialog;

/**
 * @name    DialoggedMenuItem - 2011.10.02 <br>
 *          A menu item that will produce a dialog
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class DropdownMenuItem extends JMenuItem {

    public DropdownMenuItem(final JFrame frame,
            final String name,
            final Constructor constructor) {
        super(new DelayedBuildAction(name) {

            private DropdownDialog dialog;

            @Override
            public void buildComponents() {
                try {
                    dialog = (DropdownDialog) constructor.newInstance(frame);
                } catch (InstantiationException ex) {
                    Logger.getLogger(DropdownMenuItem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(DropdownMenuItem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(DropdownMenuItem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(DropdownMenuItem.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void activateActions() {
                dialog.setVisible(true);
            }
        });

        System.out.println(frame.getName());
    }
}
