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

package uk.ac.ebi.metingear;

import uk.ac.ebi.metingear.launch.ApplicationLauncher;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Main - 13.03.2012 <br/>
 * <p/>
 * Launches the Metingear application.
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class Main {

    public static void main(String[] args) throws Exception {

        SwingUtilities.invokeAndWait(getLauncher());
        
    }

    /**
     * Hack until we remove the MainView singleton :-(
     */
    public static void relaunch() throws IOException, URISyntaxException {
        PreMain.main(new String[0]);
        System.exit(0);
    }

    public static Runnable getLauncher(){
        
        String os = System.getProperty("os.name");
        
        if(os.equals("Mac OS X")){
            try{
                return (Runnable) Class.forName("uk.ac.ebi.metingear.launch.MacApplicationLauncher").newInstance();
            } catch (Exception ex){
                System.err.println("Could not create OS X application launcher - using default");
            }
        }
        
        return new ApplicationLauncher();
        
    }

}
