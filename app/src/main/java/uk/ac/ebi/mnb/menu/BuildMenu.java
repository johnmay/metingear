/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import uk.ac.ebi.mnb.menu.build.CatFamAction;
import uk.ac.ebi.mnb.menu.build.PredictGPR;
import uk.ac.ebi.mnb.menu.build.PriamAction;
import uk.ac.ebi.mnb.menu.build.StoichiometryAction;

import javax.swing.*;

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
    private JComponent items[] = new JComponent[ 6 ];

    public BuildMenu() {

        super( "Build" );


        int index = 0;
        items[index++] = new JMenuItem( new PredictGPR() );
        items[index++] = new JMenuItem( new PriamAction() );
        items[index++] = new JMenuItem( new CatFamAction() );
        items[index++] = new JSeparator();
        items[index++] = new JMenuItem( new StoichiometryAction() );
        items[index++] = new JSeparator();

        
    }

   
}
