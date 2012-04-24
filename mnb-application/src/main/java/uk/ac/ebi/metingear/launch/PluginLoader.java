package uk.ac.ebi.metingear.launch;

import uk.ac.ebi.metingear.view.PlugableDialog;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.ContextMenu;
import uk.ac.ebi.mnb.menu.MainMenuBar;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @version $Rev$
 */
public class PluginLoader {


    private ServiceLoader<PlugableDialog> loader;
    private MainView view;
    private MainMenuBar menu;
    private DialogLauncherFactory factory;

    public PluginLoader(MainView view, MainMenuBar menu) {
        this.view = view;
        this.menu = menu;
        this.loader = ServiceLoader.load(PlugableDialog.class);
        this.factory = new DialogLauncherFactory(view,
                                                 view,
                                                 view.getViewController(),
                                                 view.getUndoManager(),
                                                 view.getMessageManager(),
                                                 view);
    }

    public void load() {
        System.out.println("Loading Extensions:");
        Iterator<PlugableDialog> plugin = loader.iterator();
        while (plugin.hasNext()) {
            System.out.println("\t-" + load(plugin.next()));
        }
    }

    public String load(PlugableDialog plugin) {

        JMenu menu = getMenu(plugin.getMenuPath().iterator());

        if (menu instanceof ContextMenu) {
            ((ContextMenu) menu).add(factory.getLauncher(plugin.getDialogClass()), plugin.getContext());
        } else {
            menu.add(factory.getLauncher(plugin.getDialogClass()));
        }

        return plugin.getDialogClass().getSimpleName();

    }

    public JMenu getMenu(Iterator<String> path) {

        JComponent menu = this.menu;
        while (path.hasNext()) {
            menu = getMenu(menu, path.next());
        }

        return (JMenu) menu;

    }

    public JMenu getMenu(JComponent root, String query) {

        for (Component component : root.getComponents()) {
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
