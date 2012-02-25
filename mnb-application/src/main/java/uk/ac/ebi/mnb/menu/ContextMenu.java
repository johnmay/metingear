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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.core.ControllerDialog;


import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import javax.swing.event.UndoableEditListener;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.interfaces.entities.EntityCollection;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

/**
 *          ContextMenu - 2011.11.28 <br>
 *          The context menu holds actions/drop-down dialog classes with optional
 *          context associations
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ContextMenu extends JMenu {

    private static final Logger LOGGER = Logger.getLogger(ContextMenu.class);
    private Map<JMenuItem, ContextResponder> items = new HashMap<JMenuItem, ContextResponder>();
    private final MainController controller;

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
                    System.out.println(constructor.getDeclaringClass());
                    System.out.println(Arrays.asList(constructor.getParameterTypes()));
                    dialog = (ControllerDialog) constructor.newInstance((JFrame) controller,
                                                                        (TargetedUpdate) update,
                                                                        (ReportManager) message,
                                                                        (SelectionController) selection,
                                                                        (UndoableEditListener) undo);                    
                } catch (Exception ex) {
                    LOGGER.error("Unable to construct dialog:" + ex.getMessage());
                }
            }

            @Override
            public void activateActions() {
                dialog.setVisible(true);
            }
        };

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

        ReconstructionManager manager = ReconstructionManager.getInstance();
        Reconstruction reconstruction = manager.getActive();
        EntityCollection selection = controller.getViewController().getSelection();

        for (Entry<JMenuItem, ContextResponder> e : items.entrySet()) {

            JMenuItem item = e.getKey();
            ContextResponder context = e.getValue();

            item.setEnabled(context.getContext(manager, reconstruction, selection));

        }
    }
}
