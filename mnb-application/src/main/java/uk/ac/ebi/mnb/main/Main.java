/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.main;

import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.PreferencesHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import uk.ac.ebi.mnb.menu.MainMenuBar;
import uk.ac.ebi.mnb.menu.file.Preferences;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.AboutDialog;

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

        // set the OS X properties for screen menubar etc to make it better integrated
        if (os.equals("Mac OS X")) {
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
            app.setPreferencesHandler(new PreferencesHandler() {

                public void handlePreferences(PreferencesEvent pe) {
                    Preferences pref = new Preferences(MainFrame.getInstance(),
                            MainFrame.getInstance());
                    pref.setVisible(true);
                }
            });
        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    MainFrame.getInstance().setJMenuBar(new MainMenuBar());
                    MainFrame.getInstance().setVisible(true);
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }













    }
    private static JTextField textField;
}
