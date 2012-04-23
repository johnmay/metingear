package uk.ac.ebi.metingear.launch;

import org.apache.log4j.PropertyConfigurator;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.MainMenuBar;

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

    public ApplicationLauncher(){

        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/config/metingeer-log.properties"));
            PropertyConfigurator.configure(properties);
        } catch (IOException ex) {
            System.err.println("Unable to load logging configuration file");
        }

    }

    @Override
    public void run() {

        MainView view = MainView.getInstance();
        view.setJMenuBar(new MainMenuBar());

        PluginLoader loader = new PluginLoader(view, view.getJMenuBar());
        loader.load();

        view.setVisible(true);

    }

}
