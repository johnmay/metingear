/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package uk.ac.ebi.mnb.main;

import com.explodingpixels.macwidgets.*;
import java.awt.event.ComponentEvent;
import com.jgoodies.forms.factories.Borders;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import uk.ac.ebi.mnb.view.AboutDialog;
import uk.ac.ebi.mnb.menu.MainMenuBar;
import uk.ac.ebi.mnb.view.DialogController;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.mnb.view.entity.ProjectPanel;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.menu.file.OpenProjectAction;
import uk.ac.ebi.mnb.view.MessageManager;
import uk.ac.ebi.mnb.view.ViewUtils;


/**
 * MainView.java
 * Singleton class holding the MainView of the application. The main view contains access methods
 * to the project panel and the source tree. In addition it provides utilties for displaying waring/
 * error messages and 
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public class MainView
  extends JFrame
  implements DialogController {

    private static final org.apache.log4j.Logger LOGGER =
                                                 org.apache.log4j.Logger.getLogger(MainView.class);
    private UnifiedToolBar toolbar;
    private JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private ProjectPanel project = new ProjectPanel();
    private MessageManager messages = new MessageManager();
    private SourceController sourceController;


    /**
     *
     * Singleton access method
     * 
     * @return Instance of MainView
     *
     */
    public static MainView getInstance() {
        return MainViewHolder.INSTANCE;
    }


    /**
     * Inner class holding the instance
     */
    private static class MainViewHolder {

        private static final MainView INSTANCE = new MainView();
    }


    private MainView() {

        super("Metabolic Network Builder");

        // 4 x 3 size
        Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) ((float) dimensions.height * (4f / 3f)), dimensions.height);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // menu bar
        setJMenuBar(new MainMenuBar());

        // source list
        sourceController = new SourceController();
        SourceList list = new SourceList(sourceController.model);
        SourceListControlBar controlBar = new SourceListControlBar();
        controlBar.installDraggableWidgetOnSplitPane(pane);
        controlBar.createAndAddButton(ViewUtils.createImageIcon("images/cutout/plus_16x16.png", ""), null);
        controlBar.createAndAddPopdownButton(ViewUtils.createImageIcon("images/cutout/arrow_cog_16x16.png", ""), sourceController);
        pane.setDividerLocation(300);
        pane.setBorder(Borders.EMPTY_BORDER);
        pane.add(list.getComponent(), JSplitPane.LEFT);
        list.installSourceListControlBar(controlBar);
        list.addSourceListSelectionListener(sourceController);
        list.addSourceListClickListener(sourceController);
        //list.setColorScheme(new SourceListDarkColorScheme());

        messages.setVisible(false);

        //
        this.add(pane, BorderLayout.CENTER);
        this.add(messages, BorderLayout.SOUTH);

        // setup the pane
        pane.add(project, JSplitPane.RIGHT);
        pane.setContinuousLayout(true);
        pane.setDividerSize(1);
        pane.setBorder(Borders.EMPTY_BORDER);
        // paint hairline border
        ((BasicSplitPaneUI) pane.getUI()).getDivider().setBorder(
          BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(0xa5a5a5)));

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e) {
                updateDialogLocations();
            }


            @Override
            public void componentResized(ComponentEvent e) {
                messages.update();
            }


        });

    }


    /**
     * Access the source list controller
     * @return
     */
    public SourceController getSourceListController() {
        return sourceController;
    }


    /**
     * 
     * Updates the currently displayed dialogs to the correct position of the frame
     *
     */
    public void updateDialogLocations() {

        for( Window window : getWindows() ) {
            if( window instanceof DropdownDialog ) {
                DropdownDialog dialog = ((DropdownDialog) window);
                dialog.setLocation();
                dialog.validate();
            }
        }

    }


    /**
     * Returns the main menu bar (MainMenuBar) from the MainView
     * @return
     */
    @Override
    public MainMenuBar getJMenuBar() {
        return (MainMenuBar) super.getJMenuBar();
    }


    /**
     *
     * Returns the project panel associated with the view
     * @return Instance of the project panel
     *
     */
    public ProjectPanel getProjectPanel() {
        return project;
    }


    /**
     * Sends update signal to project items
     */
    public void update() {
        project.update();
        sourceController.update();
    }


    public void setToolbar(UnifiedToolBar toolbar) {
        this.toolbar = toolbar;
    }


    public UnifiedToolBar getToolbar() {
        return toolbar;
    }

    //***** message delegation *****//

    public void showWarningDialog(String mesg) {
        messages.addMessage(new WarningMessage(mesg));
    }


    public void showErrorDialog(String mesg) {
        messages.addMessage(new ErrorMessage(mesg));
    }

    /**
     *
     * Places the provided dialog in a drop down location under the toolbar
     * @param dialog
     * 
     */
    public void place(DropdownDialog dialog) {

        int x = toolbar.getComponent().getLocationOnScreen().x + toolbar.getComponent().getWidth() / 2 - dialog.getWidth() / 2;
        int y = toolbar.getComponent().getLocationOnScreen().y + toolbar.getComponent().getHeight();

        dialog.setLocation(x, y);

        return;

    }


}

