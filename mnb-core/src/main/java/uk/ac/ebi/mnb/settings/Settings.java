/*
 *     This file is part of Metabolic Network Builder
 * 
 *     Metabolic Network Builder is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.settings;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.ac.ebi.chemet.io.external.HomologySearchFactory;
import uk.ac.ebi.mnb.view.theme.DefaultTheme;
import uk.ac.ebi.mnb.interfaces.Theme;

/**
 * ApplicationPreferences.java
 * Class to store the application properties
 *
 * @author johnmay
 * @date May 19, 2011
 */
public class Settings
        extends Properties {

    private static final org.apache.log4j.Logger LOGGER =
                                                 org.apache.log4j.Logger.getLogger(
            Settings.class);
    private static final String PROPERTIES_FILE = "preferences...";

    private static class ApplicationPreferencesHolder {

        private static Settings INSTANCE = new Settings();
    }

    public static Settings getInstance() {
        return ApplicationPreferencesHolder.INSTANCE;
    }

    private Settings() {
        put(VIEW_TOOLBAR_INSPECTOR, Boolean.TRUE);
        put(VIEW_SOURCE_METABOLITE, SourceItemDisplayType.NAME);
        put(VIEW_SOURCE_REACTION, SourceItemDisplayType.NAME);
        put(VIEW_SOURCE_PRODUCT, SourceItemDisplayType.NAME);
        put(VIEW_SOURCE_TASK, SourceItemDisplayType.NAME);
    }

    public Theme getTheme() {
        return theme;
    }

    public String getBlastVersion() {
        return Preferences.userNodeForPackage(HomologySearchFactory.class).get("blastall.version", "");
    }

    public String getBlastPath() {
        return Preferences.userNodeForPackage(HomologySearchFactory.class).get("blastall.path", "");
    }

    /**
     * Sets blast.path and blast.version preferences
     * @param path Path to a blast executable
     * @return
     */
    public void setBlastPreferences(String path) throws InvalidParameterException {
        File file = new File(path);
        if (file.exists()) {
            Preferences.userNodeForPackage(HomologySearchFactory.class).put("blastall.path", path);
            Process process;
            try {
                process = Runtime.getRuntime().exec(path + " ?");
                Scanner scanner = new Scanner(process.getInputStream());
                Pattern version = Pattern.compile("(\\d+.\\d+.\\d+)");
                String versionNumber = null;
                while (scanner.hasNext()) {
                    Matcher regex = version.matcher(scanner.nextLine());
                    if (regex.find()) {
                        versionNumber = regex.group(1);
                        break;
                    }
                }
                scanner.close();

                if (versionNumber == null) {
                    throw new InvalidParameterException("Unable to determine blast version");
                } else {
                    Preferences.userNodeForPackage(HomologySearchFactory.class).put("blastall.version", versionNumber);
                }

            } catch (IOException ex) {
                throw new InvalidParameterException("Unable to determine blast version");
            }
        } else {
            throw new InvalidParameterException("Unable to find blastall exectuable at specified path");
        }
    }

    public Boolean getOption(String key) {
        return (Boolean) get(key);
    }

    public SourceItemDisplayType getDisplayType(String key) {
        return (SourceItemDisplayType) get(key);
    }
    private Theme theme = new DefaultTheme();
    public static final String VIEW_TOOLBAR_INSPECTOR = "view.toolbar.inspector";
    public static final String VIEW_SOURCE_METABOLITE = "view.source.metabolite"; // display accession, name or abbreviation?
    public static final String VIEW_SOURCE_REACTION = "view.source.reaction"; // display accession, name or abbreviation?
    public static final String VIEW_SOURCE_PRODUCT = "view.source.product"; // display accession, name or abbreviation?
    public static final String VIEW_SOURCE_TASK = "view.source.task"; // display accession, name or abbreviation?
}
