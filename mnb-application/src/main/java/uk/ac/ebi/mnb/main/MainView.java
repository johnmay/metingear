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

import com.apple.laf.AquaRootPaneUI;
import uk.ac.ebi.mnb.interfaces.DialogController;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

import uk.ac.ebi.core.*;
import uk.ac.ebi.mnb.interfaces.SelectionManager;
import uk.ac.ebi.mnb.menu.MainMenuBar;
import uk.ac.ebi.mnb.view.*;
import uk.ac.ebi.mnb.view.entity.ProjectView;
import uk.ac.ebi.search.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.BadLocationException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.LockObtainFailedException;

import com.explodingpixels.macwidgets.*;
import com.explodingpixels.widgets.WindowUtils;
import com.jgoodies.forms.factories.Borders;
import javax.swing.undo.UndoManager;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.bar.MessageBar;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.TaskManager;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.menu.EditUndoButtons;
import uk.ac.ebi.mnb.menu.ViewInfo;

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
        implements DialogController,
                   MainController {

    private static final Logger LOGGER = Logger.getLogger(MainView.class);
    private UndoManager undoManager;
    private Toolbar toolbar; //TODO: wrap in class
    private JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); //TODO wrap
    private ProjectView project = new ProjectView();
    private ReportManager messages = new MessageBar();
    private SourceController sourceController; //TODO:  move to SourceList wrapping class
    private JTextField searchField = new JTextField(10); //TODO:  move to a toolbar wraping class
    private final SourceList source;

    /**
     * Inner class holding the instance
     */
    private static class MainViewHolder {

        private static final MainView INSTANCE = new MainView();
    }

    private MainView() {

        super("METINGEAR");

        // mac widgets
        getLayeredPane().getRootPane().setUI(new AquaRootPaneUI());

        MacUtils.makeWindowLeopardStyle(getRootPane());

        // toolbar
        toolbar = new Toolbar();
        //  searchField.putClientProperty("JTextField.variant", "search"); // makes the search bar rounded


        ViewInfo selector = new ViewInfo(project);
        final JComponent spacer = MacWidgetFactory.createSpacer(205, 0);
        toolbar.addComponentToLeft(new EditUndoButtons().getButtonGroup().getComponent());
        toolbar.addComponentToLeft(spacer);
        toolbar.addComponentToLeft(selector.getButtonGroup());
        toolbar.addComponentToRight("Search", searchField);

        // search field
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                final Reconstruction recon = ReconstructionManager.getInstance().getActive();
                if (recon != null) {
                    try {
                        final String text =
                                     e.getDocument().getText(0, e.getDocument().getLength());


                        new Thread(new Runnable() {

                            public void run() {
                                try {

                                    SearchableIndex index = SearchManager.getInstance().
                                            getCurrentIndex();

                                    if (index == null) {
                                        Thread t = SearchManager.getInstance().updateCurrentIndex(
                                                recon);
                                        t.join();
                                        index = SearchManager.getInstance().getCurrentIndex();
                                    }

                                    final List<AnnotatedEntity> entities =
                                                                index.getRankedEntities(SearchManager.getInstance().getQuery(
                                            text
                                            + "~"));

                                    if (entities != null) {
                                        SwingUtilities.invokeLater(new Runnable() {

                                            public void run() {
                                                SearchManager.getInstance().setPreviousEntries(
                                                        entities);
                                                ((ProjectView) getViewController()).getSearchView().update();
                                            }
                                        });
                                    }


                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                } catch (CorruptIndexException ex) {
                                    ex.printStackTrace();
                                } catch (LockObtainFailedException ex) {
                                    ex.printStackTrace();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }).start();


                        ((ProjectView) getViewController()).setSearchView();
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }



                }
            }

            public void removeUpdate(DocumentEvent e) {
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });


        // default size currently: 4 x 3
        Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) ((float) dimensions.height * (4f / 3f)), dimensions.height);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // source list (todo: wrap in a class MNBSourceList)
        sourceController = new SourceController();
        source = new SourceList(sourceController.model);
        WindowUtils.installJComponentRepainterOnWindowFocusChanged(source.getComponent());
        SourceListControlBar controlBar = new SourceListControlBar();
        controlBar.installDraggableWidgetOnSplitPane(pane);
        controlBar.createAndAddButton(MacIcons.PLUS, null);
        controlBar.createAndAddPopdownButton(MacIcons.GEAR, sourceController);
        pane.setDividerLocation((int) ((float) getWidth() * 0.25)); // 25 % of the width
        pane.setBorder(Borders.EMPTY_BORDER);
        pane.add(source.getComponent(), JSplitPane.LEFT);
        source.installSourceListControlBar(controlBar);
        source.addSourceListSelectionListener(sourceController);
        source.addSourceListClickListener(sourceController);
        //list.setColorScheme(new SourceListDarkColorScheme());
        // setup the pane
        pane.add(project, JSplitPane.RIGHT);
        pane.setContinuousLayout(true);
        pane.setDividerSize(1);
        pane.setBorder(Borders.EMPTY_BORDER);
        // paint hairline border
        ((BasicSplitPaneUI) pane.getUI()).getDivider().setBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(0xa5a5a5)));

        Box topbar = Box.createVerticalBox();
        topbar.add(toolbar.getComponent());
        topbar.add((MessageBar) messages);


        // main layout
        this.add(topbar, BorderLayout.NORTH);
        this.add(pane, BorderLayout.CENTER);
        this.add(selector.getBottomBar().getComponent(), BorderLayout.SOUTH);
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

        undoManager = new UndoManager();

        // links the task manager with this
        TaskManager.getInstance().setController(this);

        addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                source.getComponent().repaint();
                project.repaint();
            }

            public void focusLost(FocusEvent e) {
                source.getComponent().repaint();
                project.repaint();
            }
        });

    }

    public void listColors(Container component) {
        System.out.println(component.getClass() + " " + component.getBackground() + " " + component.isOpaque());
        while (component.getParent() != null) {
            component = component.getParent();
            System.out.println(component.getClass() + " " + component.getBackground() + " " + component.isOpaque());
        }
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    /** Dialog placement **/
    /**
     * 
     * Updates the currently displayed dialogs to the correct position of the frame
     *
     */
    public void updateDialogLocations() {

        for (Window window : getWindows()) {
            if (window instanceof DropdownDialog) {
                DropdownDialog dialog = ((DropdownDialog) window);
                dialog.setLocation();
                dialog.validate();
            }
        }

    }

    /**
     *
     * Places the provided dialog in a drop down location under the toolbar
     * @param dialog
     *
     */
    public void place(JDialog dialog) {

        int x = toolbar.getComponent().getLocationOnScreen().x
                + toolbar.getComponent().getWidth() / 2 - dialog.getWidth() / 2;
        int y = toolbar.getComponent().getLocationOnScreen().y
                + toolbar.getComponent().getHeight(); // height should change only screen location

        dialog.setLocation(x, y);

        return;

    }

    /**
     *
     * Sends update signal to project items and source list
     * 
     */
    public boolean update() {
        project.update();
        sourceController.update();
        source.getComponent().repaint();
        return true; // need way of neatly combinding
    }

    public boolean update(SelectionManager selection) {
        project.update(selection);
        sourceController.update();
        return true;
    }

    /** Message delegation **/
    /**
     * 
     * Adds a warning message in the message controller
     * @param mesg Short and concise description of the warning
     *
     */
    public void addWarningMessage(String mesg) {
        messages.addReport(new WarningMessage(mesg));
    }

    /**
     *
     * Adds an error message in the message controller
     * @param mesg Short and concise description of the error
     *
     */
    public void addErrorMessage(String mesg) {
        messages.addReport(new ErrorMessage(mesg));
    }

    public ReportManager getMessageManager() {
        return messages;
    }

    /* Getters/Setters */
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
     * Access the attached menu bar
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
    public ViewController getViewController() {
        return project;
    }

    /**
     * Access the displayed tool-bar
     * @return
     */
    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * Access the source list controller
     * @return
     */
    public SourceController getSourceListController() {
        return sourceController;
    }

    public DialogController getDialogController() {
        return this;
    }

    public void updateMenuContext() {
        if (getJMenuBar() != null) {
            getJMenuBar().updateContext();
        }
    }
}
