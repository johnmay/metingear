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

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

import uk.ac.ebi.core.*;
import uk.ac.ebi.mnb.core.*;
import uk.ac.ebi.mnb.menu.MainMenuBar;
import uk.ac.ebi.mnb.menu.file.NewProjectAction;
import uk.ac.ebi.mnb.menu.reconciliation.AddCrossReference;
import uk.ac.ebi.mnb.view.*;
import uk.ac.ebi.mnb.view.entity.ProjectPanel;
import uk.ac.ebi.mnb.view.labels.IconButton;
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
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * MainView.java
 * Singleton class holding the MainView of the application. The main view contains access methods
 * to the project panel and the source tree. In addition it provides utilties for displaying waring/
 * error messages and 
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public class MainFrame
        extends JFrame
        implements DialogController {

    private static final org.apache.log4j.Logger LOGGER =
            org.apache.log4j.Logger.getLogger(MainFrame.class);
    private UnifiedToolBar toolbar;
    private JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private ProjectPanel project = new ProjectPanel();
    private MessageManager messages = new MessageManager();
    private SourceController sourceController;
    private JTextField searchField = new JTextField(10); // move to a toolbar wraping class

    /**
     *
     * Singleton access method
     * 
     * @return Instance of MainView
     *
     */
    public static MainFrame getInstance() {
        return MainViewHolder.INSTANCE;
    }

    /**
     * Inner class holding the instance
     */
    private static class MainViewHolder {

        private static final MainFrame INSTANCE = new MainFrame();
    }

    private MainFrame() {

        super("Metabolic Network Builder");

        // mac widgets
        MacUtils.makeWindowLeopardStyle(getRootPane());

        // toolbar
        UnifiedToolBar toolbar = new UnifiedToolBar();
        CellConstraints cc = new CellConstraints();
        add(toolbar.getComponent(), BorderLayout.NORTH);
        searchField.putClientProperty("JTextField.variant", "search");
        toolbar.addComponentToRight(new LabeledComponentGroup("Search", searchField).getComponent());
        toolbar.addComponentToCenter(new IconButton(new NewProjectAction()));
        toolbar.addComponentToCenter(new IconButton(new AddCrossReference()));
        setToolbar(toolbar);

        // search field
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                final Reconstruction recon = ReconstructionManager.getInstance().
                        getActiveReconstruction();
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
                                        t.wait();
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
                                                getProjectPanel().
                                                        getSearchView().update();
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


                        MainFrame.getInstance().getProjectPanel().setSearchView();
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

        for (Window window : getWindows()) {
            if (window instanceof DropdownDialog) {
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
