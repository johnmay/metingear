/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.main;

import com.explodingpixels.macwidgets.LabeledComponentGroup;
import com.explodingpixels.macwidgets.MacUtils;
import com.explodingpixels.macwidgets.UnifiedToolBar;
import com.jgoodies.forms.layout.CellConstraints;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import mnb.todo.MainController;
import uk.ac.ebi.mnb.menu.reconciliation.AddCrossReference;
import uk.ac.ebi.mnb.menu.file.NewProjectAction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.AboutDialog;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.LockObtainFailedException;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.labels.IconButton;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.search.SearchManager;
import uk.ac.ebi.search.SearchableIndex;


/**
 *
 * @author johnmay
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String os = System.getProperty("os.name");

        MainController controller = MainController.getInstance();

        // set the OS X properties for screen menubar etc to make it better integrated
        if( os.equals("Mac OS X") ) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                               "Metabolic Network Builder");
            // MainView.getInstance().getRootPane().putClientProperty( "apple.awt.brushMetalLook" , Boolean.TRUE );
            // Set the doc image
            com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
            app.setDockIconImage(ViewUtils.logo_512x512);
            app.setAboutHandler(new com.apple.eawt.AboutHandler() {

                public void handleAbout(com.apple.eawt.AppEvent.AboutEvent ae) {
                    AboutDialog dialog = new AboutDialog(false);
                    dialog.setVisible(true);
                }


            });
        }

        MacUtils.makeWindowLeopardStyle(MainView.getInstance().getRootPane());

        UnifiedToolBar toolbar = new UnifiedToolBar();
        CellConstraints cc = new CellConstraints();
        MainView.getInstance().add(toolbar.getComponent(), BorderLayout.NORTH);
        textField = new JTextField(10);
        textField.putClientProperty("JTextField.variant", "search");
        toolbar.addComponentToRight(new LabeledComponentGroup("Search", textField).getComponent());


        textField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                final Reconstruction recon = ReconstructionManager.getInstance().
                  getActiveReconstruction();
                if( recon != null ) {
                    try {
                        final String text =
                                     e.getDocument().getText(0, e.getDocument().getLength());


                        new Thread(new Runnable() {

                            public void run() {
                                try {

                                    SearchableIndex index = SearchManager.getInstance().
                                      getCurrentIndex();

                                    if( index == null ) {
                                        Thread t = SearchManager.getInstance().updateCurrentIndex(
                                          recon);
                                        t.wait();
                                        index = SearchManager.getInstance().getCurrentIndex();
                                    }

                                    final List<AnnotatedEntity> entities =
                                                                index.getRankedEntities(SearchManager.
                                      getInstance().getQuery(
                                      text +
                                      "~"));

                                    if( entities != null ) {
                                        SwingUtilities.invokeLater(new Runnable() {

                                            public void run() {
                                                SearchManager.getInstance().setPreviousEntries(
                                                  entities);
                                                MainView.getInstance().getProjectPanel().
                                                  getSearchView().update();
                                            }


                                        });
                                    }


                                } catch( InterruptedException ex ) {
                                    ex.printStackTrace();
                                } catch( ParseException ex ) {
                                    ex.printStackTrace();
                                } catch( CorruptIndexException ex ) {
                                    ex.printStackTrace();
                                } catch( LockObtainFailedException ex ) {
                                    ex.printStackTrace();
                                } catch( IOException ex ) {
                                    ex.printStackTrace();
                                }
                            }


                        }).start();


                        MainView.getInstance().getProjectPanel().setSearchView();
                    } catch( BadLocationException ex ) {
                        ex.printStackTrace();
                    }



                }
            }


            public void removeUpdate(DocumentEvent e) {
            }


            public void changedUpdate(DocumentEvent e) {
            }


        });

        toolbar.addComponentToCenter(new IconButton(new NewProjectAction()));
        toolbar.addComponentToCenter(new IconButton(new AddCrossReference()));
        MainView.getInstance().setToolbar(toolbar);

        controller.addMainFrame(MainView.getInstance());

        MainView.getInstance().setVisible(true);


        // hide splsh screen after a short delay
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.schedule(new Runnable() {
//
//            public void run() {
//                MainView.getInstance().getSplash().setVisible(false);
//            }
//
//
//        }, 200, TimeUnit.MILLISECONDS);

    }


    private static JTextField textField;
}

