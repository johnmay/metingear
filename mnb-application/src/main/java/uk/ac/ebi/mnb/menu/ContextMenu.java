/**
 * MenuManager.java
 *
 * 2011.11.28
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

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * ContextMenu - 2011.11.28 <br>
 * The context menu holds actions/drop-down dialog classes with optional
 * context associations
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class ContextMenu extends JMenu {

    private static final Logger LOGGER = Logger.getLogger(ContextMenu.class);
    private Map<JMenuItem, ContextResponder> items = new HashMap<JMenuItem, ContextResponder>();
    private final MainController controller;
    private Set<ContextMenu> subMenus = new HashSet<ContextMenu>();

    public ContextMenu(String name, MainController controller) {
        super(name);
        this.controller = controller;
    }

    public DelayedBuildAction create(final Class<? extends ControllerDialog> dialogClass,
                                     final TargetedUpdate update,
                                     final ReportManager message,
                                     final SelectionController selection,
                                     final UndoableEditListener undo) {

        return new DelayedBuildAction(dialogClass, dialogClass.getSimpleName()) {

            private ControllerDialog dialog;

            @Override
            public void buildComponents() {
                try {
                    Constructor constructor = dialogClass.getConstructors()[0];
                    LOGGER.debug("Building dialog: " + dialogClass.getSimpleName());
                    dialog = (ControllerDialog) constructor.newInstance((JFrame) controller,
                                                                        (TargetedUpdate) update,
                                                                        (ReportManager) message,
                                                                        (SelectionController) selection,
                                                                        (UndoableEditListener) undo);
                } catch (Exception ex) {
                    LOGGER.error("Unable to construct dialog " + dialogClass.getSimpleName(), ex);
                }
            }

            @Override
            public void activateActions() {
                dialog.pack();
                dialog.setVisible(true);
            }
        };

    }


    public void add(JMenu menu) {
        if (menu instanceof ContextMenu) {
            LOGGER.info("Adding sub-menu: " + menu.getText());
            subMenus.add((ContextMenu) menu);
        }
        super.add(menu);
    }

    public DelayedBuildAction create(final Class<? extends ControllerDialog> clazz) {

        return create(clazz,
                      (TargetedUpdate) controller,
                      controller.getMessageManager(),
                      controller.getViewController(),
                      controller.getUndoManager());

    }

    /**
     * Using the controller specified in the menu constructor a dialog is created using the controller
     * as the update and selection manager
     */
    public void add(AbstractAction action,
                    ContextResponder context) {

        JMenuItem item = new JMenuItem(action);

        // add to menu and store in map
        add(item, context);

    }


    /**
     * Using the controller specified in the menu constructor a dialog is created using the controller
     * as the update and selection manager
     */
    public void add(JMenuItem item,
                    ContextResponder context) {


        // add to menu and store in map
        add(item);
        item.setEnabled(false);
        items.put(item, context);

    }

    /**
     * Updates all item states based on the associated context responders
     */
    public void updateContext() {

        ReconstructionManager manager = DefaultReconstructionManager.getInstance();
        Reconstruction reconstruction = DefaultReconstructionManager.getInstance().getActive();
        EntityCollection selection = controller.getViewController().getSelection();

        for (Entry<JMenuItem, ContextResponder> e : items.entrySet()) {

            JMenuItem item = e.getKey();
            ContextResponder context = e.getValue();

            item.setEnabled(context.getContext(manager, reconstruction, selection));

        }

        // update children also
        for (ContextMenu menu : subMenus) {
            menu.updateContext();
        }
    }
}
