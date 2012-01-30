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
        add(build);
        add(run);
        add(view);
        add(tools);
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
