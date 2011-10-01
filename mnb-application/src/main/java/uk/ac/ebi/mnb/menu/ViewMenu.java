/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import uk.ac.ebi.mnb.menu.view.ReactionGraphAction;
import uk.ac.ebi.mnb.menu.view.ToggleInspectorToolbar;


/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class ViewMenu
  extends ClearMenu {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(ViewMenu.class);
    private ReactionGraphAction reactionGraphAction;
    private DynamicMenuItem items[] = new DynamicMenuItem[1];


    public ViewMenu() {

        super("View");

        reactionGraphAction = new ReactionGraphAction();

        items[0] = new DynamicMenuItem(reactionGraphAction);

        for( DynamicMenuItem mnbMenuItem : items ) {
            add(mnbMenuItem);
            mnbMenuItem.reloadEnabled();
        }

        add(new JSeparator());
        JMenuItem item = new JCheckBoxMenuItem(new ToggleInspectorToolbar());
        item.setSelected(true);
        add(item);
    }


    public void setActiveDependingOnRequirements() {
        for( DynamicMenuItem mnbMenuItem : items ) {
            mnbMenuItem.reloadEnabled();
        }
    }


    public ReactionGraphAction getReactionGraphAction() {
        return reactionGraphAction;
    }


}

