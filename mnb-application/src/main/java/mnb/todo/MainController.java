/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.todo;

import uk.ac.ebi.mnb.main.MainView;

/**
 * MainController.java
 *
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public class MainController {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( MainController.class );
    private MainView f;

    private MainController() {
    }

    public static MainController getInstance() {
        return MainControllerHolder.INSTANCE;
    }

    public void addMainFrame( MainView instance ) {
        f = instance;
    }

    private static class MainControllerHolder {

        public static MainController INSTANCE = new MainController();
    }

   

}
