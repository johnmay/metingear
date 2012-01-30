/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import uk.ac.ebi.mnb.menu.build.PredictGPR;
import uk.ac.ebi.mnb.menu.build.CatFamAction;
import uk.ac.ebi.mnb.menu.build.PriamAction;
import uk.ac.ebi.mnb.menu.build.StoichiometryAction;
import uk.ac.ebi.mnb.menu.build.SwissProtHomology;

/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class BuildMenu
    extends JMenu {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( BuildMenu.class );
    private SwissProtHomology enzymeHomologyAction;
    private JComponent items[] = new JComponent[ 7 ];

    public BuildMenu() {

        super( "Build" );


        enzymeHomologyAction = new SwissProtHomology();
        int index = 0;
        items[index++] = new JMenuItem( enzymeHomologyAction );
        items[index++] = new JMenuItem( new PredictGPR() );
        items[index++] = new JMenuItem( new PriamAction() );
        items[index++] = new JMenuItem( new CatFamAction() );
        items[index++] = new JSeparator();
        items[index++] = new JMenuItem( new StoichiometryAction() );
        items[index++] = new JSeparator();

        
    }

    public SwissProtHomology getEnzymeHomologyDialogAction() {
        return enzymeHomologyAction;
    }
   
}
