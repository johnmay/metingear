/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.view.ReactionGraphAction;
import uk.ac.ebi.mnb.menu.view.ToggleInspectorToolbar;
import uk.ac.ebi.mnb.view.entity.ProjectView;

/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class ViewMenu
        extends JMenu {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(ViewMenu.class);
    private ReactionGraphAction reactionGraphAction;
    private DynamicMenuItem items[] = new DynamicMenuItem[1];

    public ViewMenu() {

        super("View");

        reactionGraphAction = new ReactionGraphAction();

        items[0] = new DynamicMenuItem(reactionGraphAction);

        for (DynamicMenuItem mnbMenuItem : items) {
            add(mnbMenuItem);
            mnbMenuItem.reloadEnabled();
        }

        add(new JSeparator());


        final ProjectView view = (ProjectView) MainView.getInstance().getViewController();

        add(new JMenuItem(new AbstractAction("Genes") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.setGeneView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Products") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.setProductView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Metabolites") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.setMetaboliteView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Reactions") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.setReactionView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Tasks") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.setTaskView();
            }
        }));
        add(new JSeparator());
        JMenuItem item = new JCheckBoxMenuItem(new ToggleInspectorToolbar());
        item.setSelected(true);
        add(item);
    }

    public void setActiveDependingOnRequirements() {
        for (DynamicMenuItem mnbMenuItem : items) {
            mnbMenuItem.reloadEnabled();
        }
    }

    public ReactionGraphAction getReactionGraphAction() {
        return reactionGraphAction;
    }
}
