/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.metingear.launch;

import org.apache.log4j.PropertyConfigurator;
import uk.ac.ebi.caf.utility.preference.type.FilePreference;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.MainMenuBar;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ApplicationLauncher - 13.03.2012 <br/> <p/> Class descriptions.
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class ApplicationLauncher implements Runnable {

    private static final String LOG4J_CONFIG = "/config/metingear-log.properties";

    public ApplicationLauncher() {

        // configure loader
        InputStream in = null;
        try {
            Properties properties = new Properties();
            in = getClass().getResourceAsStream(LOG4J_CONFIG);
            properties.load(in);
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
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ex) {
                // nothing we can do
            }
        }

    }

    public void loadLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager
                                                 .getSystemLookAndFeelClassName());
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
        view.getJMenuBar().getEditMenu().addPreferenceItem();
        beforeVisible();

        view.setVisible(true);


    }

    public void beforeVisible() {

    }

}
