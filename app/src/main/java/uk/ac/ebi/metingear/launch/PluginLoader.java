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

package uk.ac.ebi.metingear.launch;

import uk.ac.ebi.metingear.view.PlugableAction;
import uk.ac.ebi.metingear.view.PlugableDialog;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.ContextMenu;
import uk.ac.ebi.mnb.menu.MainMenuBar;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import java.awt.Component;
import java.util.Iterator;
import java.util.ServiceLoader;

/** @version $Rev$ */
public class PluginLoader {


    private ServiceLoader<PlugableDialog> dialogs;
    private ServiceLoader<PlugableAction> actions;
    private MainView view;
    private MainMenuBar menu;
    private DialogLauncherFactory factory;
    private ActionLauncherFactory actionFactory;

    public PluginLoader(MainView view, MainMenuBar menu) {
        this.view = view;
        this.menu = menu;
        this.dialogs = ServiceLoader.load(PlugableDialog.class);
        this.actions = ServiceLoader.load(PlugableAction.class);
        this.factory = new DialogLauncherFactory(view,
                                                 view,
                                                 view.getViewController(),
                                                 view.getUndoManager(),
                                                 view.getMessageManager(),
                                                 view);
        this.actionFactory = new ActionLauncherFactory(view,
                                                       view,
                                                       view.getViewController(),
                                                       view.getUndoManager(),
                                                       view.getMessageManager(),
                                                       view);
    }

    public void load() {
        System.out
              .println("[PLUGIN] Loading Extensions from META-INF/services");
        Iterator<PlugableDialog> plugin = dialogs.iterator();
        while (plugin.hasNext()) {
            PlugableDialog plugableDialog = plugin.next();
            System.out
                  .println("[PLUGIN] " + load(plugableDialog) + " (" + plugableDialog
                          .getClass() + ") loaded");
        }

        for (final PlugableAction action : actions) {
            System.out
                  .println("[PLUGIN] " + load(action) + " loaded");
        }
    }

    public String load(PlugableAction plugin) {
        JMenu menu = getMenu(plugin.getMenuPath().iterator());
        AbstractAction action = actionFactory.getLauncher(plugin.action());

        if (menu instanceof ContextMenu) {
            ((ContextMenu) menu).add(action, plugin.getContext());
        } else {
            menu.add(action);
        }

        Object name = action.getValue(Action.NAME);
        return name != null ? name.toString()
                            : plugin.action().getSimpleName();
    }

    public String load(PlugableDialog plugin) {

        JMenu menu = getMenu(plugin.getMenuPath().iterator());

        AbstractAction action = factory.getLauncher(plugin.getDialogClass());

        if (menu instanceof ContextMenu) {
            ((ContextMenu) menu).add(action, plugin.getContext());
        } else {
            menu.add(action);
        }

        Object name = action.getValue(Action.NAME);
        return name != null ? name.toString()
                            : plugin.getDialogClass().getSimpleName();

    }

    public JMenu getMenu(Iterator<String> path) {

        JComponent menu = this.menu;
        while (path.hasNext()) {
            menu = getMenu(menu, path.next());
        }

        return (JMenu) menu;

    }

    public JMenu getMenu(JComponent root, String query) {

        for (Component component : root instanceof JMenu ? ((JMenu) root)
                .getMenuComponents() : root.getComponents()) {
            if (component instanceof JMenu) {
                String subject = ((JMenu) component).getText();
                if (subject.equalsIgnoreCase(query)) {
                    return (JMenu) component;
                }
            }
        }

        ContextMenu menu = new ContextMenu(query, view);

        if (root instanceof ContextMenu) {
            ((ContextMenu) root).add(menu);
        } else {
            root.add(menu);
        }

        return menu;

    }

}
