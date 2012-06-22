package uk.ac.ebi.metingear.launch;

import org.apache.log4j.PropertyConfigurator;
import uk.ac.ebi.caf.utility.preference.type.FilePreference;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.MainMenuBar;

import javax.swing.*;
import java.io.IOException;
import java.util.Properties;

/**
 * ApplicationLauncher - 13.03.2012 <br/>
 * <p/>
 * Class descriptions.
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class ApplicationLauncher implements Runnable {

    public ApplicationLauncher() {

        // configure loader
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/config/metingeer-log.properties"));
            for (Object key : properties.keySet()) {
                String value = properties.getProperty(key.toString());
                if (value != null && value.contains("<os.app.data>")) {
                    properties.put(key,
                                   value.replaceAll("<os.app.data>",
                                                    FilePreference.OS_APP_DATA_PATH));
                }
            }
            PropertyConfigurator.configure(properties);
        } catch (IOException ex) {
            System.err.println("Unable to load logging configuration file");
        }

    }

    public void loadLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Could not set look at feel");
            }
        }
    }

    @Override
    public void run() {

        loadLookAndFeel();

        MainView view = MainView.getInstance();
        view.setJMenuBar(new MainMenuBar());

        PluginLoader loader = new PluginLoader(view, view.getJMenuBar());
        loader.load();

        view.setVisible(true);

    }

}
