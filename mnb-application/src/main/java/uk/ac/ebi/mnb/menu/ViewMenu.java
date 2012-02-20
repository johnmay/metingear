/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
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
        extends ContextMenu {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(ViewMenu.class);

    private ReactionGraphAction reactionGraphAction;


    public ViewMenu() {

        super("View", MainView.getInstance());

        reactionGraphAction = new ReactionGraphAction();

//        items[0] = new DynamicMenuItem(reactionGraphAction);
//
//        for (DynamicMenuItem mnbMenuItem : items) {
//            add(mnbMenuItem);
//            mnbMenuItem.reloadEnabled();
//        }


        add(new JSeparator());


        final MainView view = MainView.getInstance();
        final ProjectView entityView = (ProjectView) MainView.getInstance().getViewController();


        add(new JMenuItem(new AbstractAction("Entity View") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showMainPanel();
            }
        }));
        add(new JMenuItem(new AbstractAction("Genes") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showMainPanel();
                entityView.setGeneView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Products") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showMainPanel();

                entityView.setProductView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Metabolites") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showMainPanel();

                entityView.setMetaboliteView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Reactions") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showMainPanel();

                entityView.setReactionView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Tasks") {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showMainPanel();

                entityView.setTaskView();
            }
        }));
        add(new JSeparator());
        JMenuItem item = new JCheckBoxMenuItem(new ToggleInspectorToolbar());
        item.setSelected(true);
        add(item);
    }


    public ReactionGraphAction getReactionGraphAction() {
        return reactionGraphAction;
    }
}
