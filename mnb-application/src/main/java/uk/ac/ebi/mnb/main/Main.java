/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.main;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel;
import uk.ac.ebi.mnb.menu.MainMenuBar;

/**
 *
 * @author johnmay
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new SubstanceDustCoffeeLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
        }


        String os = System.getProperty("os.name");

        // set the OS X properties for screen menubar etc to make it better integrated
        if (os.equals("Mac OS X")) {
//           // System.setProperty("apple.laf.useScreenMenuBar", "true");
//            System.setProperty("com.apple.mrj.application.apple.menu.about.name",
//                    "Metabonator");
//            // MainView.getInstance().getRootPane().putClientProperty( "apple.awt.brushMetalLook" , Boolean.TRUE );
//            // Set the doc image
//            com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
//            app.setDockIconImage(ViewUtils.logo_512x512);
//            app.setAboutHandler(new com.apple.eawt.AboutHandler() {
//
//                public void handleAbout(com.apple.eawt.AppEvent.AboutEvent ae) {
//                    AboutDialog dialog = new AboutDialog(false);
//                    dialog.setVisible(true);
//                }
//            });
//            app.setPreferencesHandler(new com.apple.eawt.PreferencesHandler() {
//
//                public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent pe) {
//                    PreferenceDialog pref = new PreferenceDialog(MainView.getInstance(),
//                            MainView.getInstance());
//                    pref.setVisible(true);
//                }
//            });
        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    MainView.getInstance().setJMenuBar(new MainMenuBar());
                    MainView.getInstance().setVisible(true);
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
