/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
