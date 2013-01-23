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

import javax.swing.JMenuBar;
import org.apache.log4j.Logger;

/**
 * MainMenuBar.java
 *
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class MainMenuBar
        extends JMenuBar {

    private static final Logger LOGGER = Logger.getLogger(MainMenuBar.class);
    private FileMenu file = new FileMenu();
    private BuildMenu build = new BuildMenu();
    private ViewMenu view = new ViewMenu();
    private EditMenu edit = new EditMenu();
    private RunMenu run = new RunMenu();
    private ToolsMenu tools = new ToolsMenu();

    public MainMenuBar() {
        add(file);
        add(edit);
        add(view);
        add(tools);
        add(run);

//        setBorderPainted(false);
//        WindowUtils.installJComponentRepainterOnWindowFocusChanged(this);
    }

    public EditMenu getEditMenu() {
        return edit;
    }

    public BuildMenu getBuildMenu() {
        return build;
    }

    public FileMenu getFileMenu() {
        return file;
    }

    public RunMenu getRunMenu() {
        return run;
    }

    public void updateContext() {
        long start = System.currentTimeMillis();
        file.updateContext();
        edit.updateContext();
        tools.updateContext();
        long end = System.currentTimeMillis();
        LOGGER.debug("Menu context updated " + (end - start) + " (ms)");
    }

}
