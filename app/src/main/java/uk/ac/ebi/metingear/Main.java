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
