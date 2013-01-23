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
package uk.ac.ebi.mnb.menu.build;

import uk.ac.ebi.caf.action.DelayedBuildAction;

/**
 * CatFamAction.java
 *
 *
 * @author johnmay
 * @date May 25, 2011
 */
public class CatFamAction
    extends DelayedBuildAction{

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( CatFamAction.class );

    public CatFamAction() {
        super("CatFam");
    }

    @Override
    public void buildComponents() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void activateActions() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

}
