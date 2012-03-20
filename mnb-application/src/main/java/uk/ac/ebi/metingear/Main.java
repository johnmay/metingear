package uk.ac.ebi.metingear;

import uk.ac.ebi.metingear.launch.ApplicationLauncher;

import javax.swing.*;

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
