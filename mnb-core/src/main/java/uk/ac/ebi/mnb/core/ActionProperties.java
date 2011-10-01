/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Singleton class to load action properties from a properties file
 * @author johnmay
 * @date   Apr 13, 2011
 */
public class ActionProperties extends Properties {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ActionProperties.class );

    // TODO(jwmay): move the path of this to another class that manages this ResourceProperties?
    private static URL actionPropertiesURL =  ActionProperties.class.getClassLoader().getResource( "mnb/config/action.properties");

    private ActionProperties() {
        try {
            load( actionPropertiesURL.openStream() );
        } catch ( IOException ex ) {
            logger.error("Could not open action properties", ex);
        }
    }

    public static ActionProperties getInstance() {
        return ActionPropertiesHolder.INSTANCE;
    }

    private static class ActionPropertiesHolder {
        private static final ActionProperties INSTANCE = new ActionProperties();
    }

 }
