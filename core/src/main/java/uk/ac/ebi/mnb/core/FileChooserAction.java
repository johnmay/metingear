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
package uk.ac.ebi.mnb.core;

import uk.ac.ebi.caf.action.DelayedBuildAction;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * FileChooserAction.java
 *
 *
 * @author johnmay
 * @date Apr 14, 2011
 */
public abstract class FileChooserAction extends DelayedBuildAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( FileChooserAction.class );
    private JFileChooser chooser;

    public FileChooserAction( String command ) {
        super( command );
    }

    @Override
    public void buildComponents() {
        chooser = new JFileChooser();
        chooser.setName( getName() );
    }

    public JFileChooser getChooser() {
        return chooser;
    }

    public int showOpenDialog() {
        return getChooser().showOpenDialog( null );
    }

    public int showSaveDialog() {
        return getChooser().showSaveDialog( null);
    }

    public File getFile( int returnValue ) {
        if ( JFileChooser.APPROVE_OPTION == returnValue ) {
            return chooser.getSelectedFile();
        }
        return null;
    }
}
