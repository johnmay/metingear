/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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



        add(new JMenuItem(new AbstractAction("Genes") {

            @Override
            public void actionPerformed(ActionEvent e) {
                entityView.setGeneView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Products") {

            @Override
            public void actionPerformed(ActionEvent e) {
                entityView.setProductView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Metabolites") {

            @Override
            public void actionPerformed(ActionEvent e) {
                entityView.setMetaboliteView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Reactions") {

            @Override
            public void actionPerformed(ActionEvent e) {
                entityView.setReactionView();
            }
        }));
        add(new JMenuItem(new AbstractAction("Tasks") {

            @Override
            public void actionPerformed(ActionEvent e) {
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
