/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.core;

import java.awt.event.ActionEvent;

/**
 * DelayedBuildAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public abstract class DelayedBuildAction extends GeneralAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( DelayedBuildAction.class );

    private  boolean built = false;

    public DelayedBuildAction(String command)
    {
        super( command );
    }

    public abstract void buildComponents();
    public abstract void activateActions();

    public void actionPerformed( ActionEvent e ) {
        if(built == false){
            buildComponents();
            built = true;
        }
        activateActions();
    }





}
