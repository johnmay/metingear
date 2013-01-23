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

package uk.ac.ebi.metingear;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * PreMain - 13.03.2012 <br/>
 * <p/>
 * Class descriptions.
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class PreMain {

    private static final Logger LOGGER = Logger.getLogger(PreMain.class);

    public static void main(String[] args) throws IOException, URISyntaxException {

        final String javaBin    = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            // command.add(jvmArg);
        }
        command.add("-cp");
        command.add(ManagementFactory.getRuntimeMXBean().getClassPath());
        command.add(Main.class.getName());

        final ProcessBuilder builder = new ProcessBuilder(command);

        System.out.println(command);
        builder.start();
        System.exit(0);

    }
    


}
