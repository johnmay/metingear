package uk.ac.ebi.metingear.launch;

import com.apple.eawt.FullScreenUtilities;
import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.metingear.preference.PreferenceFrame;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.AboutDialog;

/**
 * ApplicationLauncher - 13.03.2012 <br/>
 * <p/>
 * Class descriptions.
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class
MacApplicationLauncher extends ApplicationLauncher {

    PreferenceFrame preferences;

    public MacApplicationLauncher() {

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Metingear");

        // Set the doc image

        com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
        app.setDockIconImage(ResourceUtility.getIcon("/uk/ac/ebi/chemet/render/images/networkbuilder_512x512.png").getImage());
        app.setAboutHandler(new com.apple.eawt.AboutHandler() {

            public void handleAbout(com.apple.eawt.AppEvent.AboutEvent ae) {
                AboutDialog dialog = new AboutDialog(false);
                dialog.setVisible(true);
            }
        });

        app.setPreferencesHandler(new com.apple.eawt.PreferencesHandler() {

            public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent pe) {

                if (preferences == null)
                    preferences = new PreferenceFrame();

                preferences.setVisible(true);
                preferences.pack();

            }
        });

    }

    @Override
    public void loadLookAndFeel() {
        // do nothing default looks good on OS X
    }

    @Override
    public void beforeVisible() {
        FullScreenUtilities.setWindowCanFullScreen(MainView.getInstance(), true);
    }
}
