/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import org.apache.log4j.PropertyConfigurator;
import uk.ac.ebi.chemet.render.ViewUtilities;
import uk.ac.ebi.mnb.menu.MainMenuBar;
import uk.ac.ebi.mnb.menu.file.PreferenceDialog;
import uk.ac.ebi.mnb.view.AboutDialog;


/**
 *
 * @author johnmay
 */
public class Main {

    private static String os = System.getProperty("os.name");


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        loadLogging();

        setupOSX();
//        try {
//            UIManager.setLookAndFeel(new SubstanceCremeCoffeeLookAndFeel());
//        } catch (UnsupportedLookAndFeelException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    
                    MainView view = MainView.getInstance();
                    JMenuBar menubar = new MainMenuBar();

                    view.setJMenuBar(menubar);
                    view.setVisible(true);

                }
            });
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {            
            ex.printStackTrace();
        }
       
    }


    /**
     * Ensure use of our own Log4J file
     */
    private static void loadLogging() {
        try {
            Properties properites = new Properties();
            properites.load(Main.class.getResourceAsStream("/config/metingeer-log.properties"));
            PropertyConfigurator.configure(properites);
        } catch (IOException ex) {
            System.err.println("Unable to load logging configuration file");
        }
    }


    private static void setupOSX() {

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Metingeer");
        // Set the doc image

        com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
        app.setDockIconImage(ViewUtilities.logo_512x512);
        app.setAboutHandler(new com.apple.eawt.AboutHandler() {

            public void handleAbout(com.apple.eawt.AppEvent.AboutEvent ae) {
                AboutDialog dialog = new AboutDialog(false);
                dialog.setVisible(true);
            }
        });
        app.setPreferencesHandler(new com.apple.eawt.PreferencesHandler() {

            public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent pe) {
                PreferenceDialog pref = new PreferenceDialog(MainView.getInstance(),
                                                             MainView.getInstance());
                pref.setVisible(true);
            }
        });
    }
}
