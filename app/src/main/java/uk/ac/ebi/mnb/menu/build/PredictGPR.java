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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu.build;

import uk.ac.ebi.caf.action.DelayedBuildAction;

/**
 * PredictGPR.java
 * Class builds enzyme annotations for gene products
 * provided the SwissProt Homology has been run
 *
 * This action will launch a dialog with options for
 * assigning the enzyme based on classification
 *
 * ProjectRequires=EnzymeHomology,ProteinProducts
 * @author johnmay
 * @date Apr 29, 2011
 */
public class PredictGPR
        extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( PredictGPR.class );
    private PredictGPRDialog dialog;

    public PredictGPR( ) {
        super( "EnzymeAnnotation" );
    }

    @Override
    public void buildComponents() {
        dialog = new PredictGPRDialog();
    }

    @Override
    public void activateActions() {
        dialog.setVisible( true );
    }
}
