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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.build;

import java.awt.event.ActionEvent;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.mnb.core.TaskManager;
import uk.ac.ebi.mnb.main.MainView;

/**
 * RunTasksAction.java
 *
 *
 * @author johnmay
 * @date Apr 28, 2011
 */
public class RunTasksAction extends GeneralAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( RunTasksAction.class );

    public RunTasksAction() {
        super("RunTasks");
    }

    public void actionPerformed( ActionEvent e ) {
        // start the task manager thread
        TaskManager tm = TaskManager.getInstance();
        Thread t = new Thread(tm);
        t.start();
        MainView.getInstance().update();
    }


}
