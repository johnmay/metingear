/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.caf.utility.preference.type.BooleanPreference;
import uk.ac.ebi.caf.utility.preference.type.FilePreference;
import uk.ac.ebi.caf.utility.preference.type.IntegerPreference;
import uk.ac.ebi.caf.utility.preference.type.StringPreference;
import uk.ac.ebi.caf.utility.version.Version;
import uk.ac.ebi.mdk.domain.DomainPreferences;
import uk.ac.ebi.mdk.service.ServicePreferences;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.MainMenuBar;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
        loadRequiredFonts();
        configureProxy();

        final MainView view = MainView.getInstance();
        view.setJMenuBar(new MainMenuBar());

        PluginLoader loader = new PluginLoader(view, view.getJMenuBar());
        loader.load();
        view.getJMenuBar().getEditMenu().addPreferenceItem();
        beforeVisible();

        view.setVisible(true);

        // check for update
        Thread t = new Thread(new Runnable() {
            @Override public void run() {
                checkForUpdate(view);
            }
        });
        t.setName("UPDATE-CHECK");
        t.start();

    }

    void configureProxy() {
        ServicePreferences pref = ServicePreferences.getInstance();
        String host = System.getProperty("proxyHost");
        String port = System.getProperty("proxyPort");

        // pick up from http.* proxy settings
        if (host == null)
            host = System.getProperty("http.proxyHost");
        if (port == null)
            port = System.getProperty("http.proxyPort");

        StringPreference hostPref = pref.getPreference("PROXY_HOST");
        IntegerPreference portPref = pref.getPreference("PROXY_PORT");
        BooleanPreference enabled = pref.getPreference("PROXY_SET");

        if (host != null && !host.isEmpty()) {
            enabled.put(true);
            hostPref.put(host);
        }
        if (port != null && !port.isEmpty()) {
            try {
                portPref.put(Integer.parseInt(port));
                enabled.put(true);
            } catch (NumberFormatException e) {

            }
        }

        if (enabled.get()) {
            System.getProperties().put("http.proxyHost",
                                       hostPref.get());
            System.getProperties().put("http.proxyPort",
                                       portPref.get());
        }

    }
    
    void loadRequiredFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        try {
            Font cousineRegular = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/cousine/Cousine-Regular.ttf"));
            Font cousineBold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/cousine/Cousine-Bold.ttf"));
            ge.registerFont(cousineRegular);
            ge.registerFont(cousineBold);
        } catch (FontFormatException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void checkForUpdate(final Window window) {
        final StringPreference updatePref = DomainPreferences.getInstance()
                                                             .getPreference("UPDATE_CHECK");
        final long lastChecked = Long.parseLong(updatePref.get());
        final long now = System.nanoTime();
        final long delta = now - lastChecked;

        if (TimeUnit.NANOSECONDS.toDays(delta) > 5) {
            Logger.getLogger(getClass()).info("Checking for update");
            InputStream in = null;
            try {

                URL url = new URL("http://www.ebi.ac.uk/steinbeck-srv/metingear/download/LATEST/version-info.xml");
                in = url.openStream();
                DocumentBuilder documentBuilder = DocumentBuilderFactory
                        .newInstance().newDocumentBuilder();
                Document doc = documentBuilder.parse(in);

                String latestVersionString = doc.getElementsByTagName("version")
                                                .item(0).getTextContent();
                final String latestMessage = doc.getElementsByTagName("message")
                                                .item(0).getTextContent();

                String currentVersionString = getClass().getPackage()
                        .getImplementationVersion();

                // not running from the jar
                if (currentVersionString == null)
                    return;

                Version latest = new Version(latestVersionString);
                Version current = new Version(currentVersionString);

                if (current.compareTo(latest) > 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {
                            String[] options = new String[]{"Remind Me Later", "Download Now"};
                            int i = JOptionPane.showOptionDialog(window,
                                                                 latestMessage,
                                                                 "A new version is available",
                                                                 JOptionPane.DEFAULT_OPTION,
                                                                 JOptionPane.INFORMATION_MESSAGE,
                                                                 ResourceUtility
                                                                         .getIcon("/uk/ac/ebi/chemet/render/images/networkbuilder_64x64.png"),
                                                                 options,
                                                                 options[1]);
                            if (i == 0) {
                                // write the time to the preferences
                                updatePref.put(Long.toString(now));
                            } else if (i == 1) {
                                String page = "http://johnmay.github.com/metingear";
                                try {
                                    Desktop.getDesktop().browse(new URI(page));
                                } catch (IOException e) {
                                    Logger.getLogger(getClass())
                                          .warn("Unable to open page:", e);
                                } catch (URISyntaxException e) {
                                    Logger.getLogger(getClass())
                                          .warn("Unable to open page:", e);
                                }
                            }
                        }
                    });
                }

            } catch (IOException e) {
                Logger.getLogger(getClass())
                      .warn("unable to check for update:", e);
            } catch (ParserConfigurationException e) {
                Logger.getLogger(getClass())
                      .warn("unable to get dom document builder:", e);
            } catch (SAXException e) {
                Logger.getLogger(getClass())
                      .warn("unable to get dom document builder:", e);
            } catch (Exception e) {
                // got to catch-em all, make sure we fail never
                Logger.getLogger(getClass())
                      .error("high level error on update check:", e);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    // ignore
                }
            }

        } else {
            Logger.getLogger(getClass())
                  .info("Skipping update check, time since user notified < 5 days");
        }
    }

    public void beforeVisible() {

    }

}
