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
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.undo.UndoManager;

import org.apache.log4j.Logger;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.entities.EntityCollection;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import uk.ac.ebi.mnb.core.ControllerDialogItem;
import uk.ac.ebi.mnb.dialog.edit.AddAuthorAnnotation;
import uk.ac.ebi.mnb.dialog.edit.CreateSubset;
import uk.ac.ebi.mnb.dialog.edit.DeleteEntities;
import uk.ac.ebi.mnb.dialog.edit.MergeEntities;


/**
 *          EditMenu – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class EditMenu extends ContextMenu {

    private static final Logger LOGGER = Logger.getLogger(EditMenu.class);

    private JComponent items[] = new JComponent[3];


    public EditMenu() {
        super("Edit", MainView.getInstance());

        final MainView view = MainView.getInstance();

        add(new JMenuItem(new GeneralAction("Undo") {

            public void actionPerformed(ActionEvent ae) {
                UndoManager manager = MainView.getInstance().getUndoManager();
                if (manager.canUndo()) {
                    manager.undo();
                    MainView.getInstance().update();
                }
            }
        }));
        add(new JMenuItem(new GeneralAction("Redo") {

            public void actionPerformed(ActionEvent ae) {
                UndoManager manager = MainView.getInstance().getUndoManager();
                if (manager.canRedo()) {
                    manager.redo();
                    MainView.getInstance().update();
                }
            }
        }));

        add(new JSeparator());
        add(create(MergeEntities.class));
        add(new DeleteEntities(MainView.getInstance()));

        add(new JSeparator());
        add(create(CreateSubset.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection.hasSelection();
            }
        });

        add(new JSeparator());
        add(create(AddAuthorAnnotation.class));
        // add citation
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
