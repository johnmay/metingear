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

import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.build.RunTasksAction;


/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class RunMenu extends ContextMenu {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RunMenu.class);

    private RunTasksAction runTasksAction;


    public RunMenu() {

        super("Run", MainView.getInstance());

        runTasksAction = new RunTasksAction();

        add(runTasksAction);


    }


    public RunTasksAction getRunTasksAction() {
        return runTasksAction;
    }
}
