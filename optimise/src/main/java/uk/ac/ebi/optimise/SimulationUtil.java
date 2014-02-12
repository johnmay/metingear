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
package uk.ac.ebi.optimise;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.utility.preference.type.FilePreference;
import uk.ac.ebi.mdk.domain.DomainPreferences;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;


/**
 * SimulationUtil - 2011.12.02 <br> Class provides utility methods for the
 * simulation module
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class SimulationUtil {

    private static final Logger LOGGER = Logger.getLogger(SimulationUtil.class);

    private static String pathFromPreferences() {
        String path = System.getProperty("mdk.cplex.path");
        if (path != null) return path;
        FilePreference pref = DomainPreferences.getInstance().getPreference("CPLEX_LIBRARY_PATH");
        return pref.get().getPath();
    }

    /**
     * Adds the CPLEX library path specified in the user preferences {@see
     * setCPLEXLibraryPath(String)}
     */
    public static boolean setup() {

        String path = pathFromPreferences();

        List<String> paths = Arrays.asList(System.getProperty("java.library.path").split(File.pathSeparator));

        if (path != null
                && !path.isEmpty()
                && new File(path).exists()) {

            try {
                if (paths.contains(path)) {
                    return true;
                }
                LOGGER.info("Configuring library cplex path " + path);
                addLibraryPath(path);
                return true;
            } catch (IOException ex) {
                LOGGER.error("Unable to add CPLEX library path " + path + " to 'java.libary.path'");
            }

        }

        return false;

    }

    public static boolean isAvailable() {
        return setup() && isCPLEXinClassPath();
    }

    private static Boolean ilog = null;

    public static boolean isCPLEXinClassPath() {

        if (ilog != null) return ilog;

        try {
            Class.forName("ilog.concert.IloNumVar", false, ClassLoader.getSystemClassLoader());
            ilog = true;
        } catch (ClassNotFoundException ex) {
            ilog = false;
        }
        return ilog;
    }

    /**
     * Sets the CPLEX library path in the preferences and calls {@see setup()}
     * adding this to the system property library paths. Note: No paths are not
     * removed
     */
    public static void setCPLEXLibraryPath(String path) {
        FilePreference pref = DomainPreferences.getInstance().getPreference("CPLEX_LIBRARY_PATH");
        pref.put(new File(path));
        setup();
    }


    /**
     * Adds a path to the system property java.library.path at runtime
     *
     * @param s
     * @throws IOException
     */
    private static void addLibraryPath(String s) throws IOException {
        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = s;
            field.set(null, tmp);

        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }
}
