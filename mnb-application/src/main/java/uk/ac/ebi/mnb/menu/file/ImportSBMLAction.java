/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.menu.file;

import uk.ac.ebi.mnb.core.DelayedBuildAction;

/**
 * ImportSBMLAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public class ImportSBMLAction extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ImportSBMLAction.class );

    public ImportSBMLAction() {
        super("ImportSBML");
    }

    @Override
    public void activateActions() {

    }

    @Override
    public void buildComponents() {
    }




}
