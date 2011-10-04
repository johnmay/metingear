/**
 * EditMenu.java
 *
 * 2011.09.26
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

import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import uk.ac.ebi.mnb.view.ViewUtils;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.core.GeneralAction;
import uk.ac.ebi.mnb.main.MainView;

/**
 *          EditMenu – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class EditMenu extends ClearMenu {

    private static final Logger LOGGER = Logger.getLogger(EditMenu.class);
    private JComponent items[] = new JComponent[3];

    public EditMenu() {
        super("Edit");
        setBackground(ViewUtils.CLEAR_COLOUR);
        setBorderPainted(false);

        MainView view = MainView.getInstance();

        add(new JMenuItem(new GeneralAction("Undo") {

            public void actionPerformed(ActionEvent ae) {
                MainView.getInstance().getUndoManager().undo();
                MainView.getInstance().update();
            }
        }));
        add(new JMenuItem(new GeneralAction("Redo") {

            public void actionPerformed(ActionEvent ae) {
                MainView.getInstance().getUndoManager().redo();
                MainView.getInstance().update();
            }
        }));
        add(new JSeparator());
        //add(new ContextDialogItem(view, view.getViewController(), ContextDialog.class));

//        for (JComponent component : items) {
//            add(component);
//            if (component instanceof DynamicMenuItem) {
//                ((DynamicMenuItem) component).reloadEnabled();
//            }
//        }

        //new UndoableEditSupport().addUndoableEditListener(MainView.getInstance().getUndoManager());


    }
}
