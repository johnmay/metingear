/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package mnb.view.old;

import java.io.File;
import javax.swing.JTree;

/**
 * FileBrowserView.java
 *
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public class FileBrowserView extends JTree {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( FileBrowserView.class );
    private FileSystemModel fileSystemModel;

    public FileBrowserView( File root ) {
        super();
        fileSystemModel = new FileSystemModel( root );
        setModel( fileSystemModel );
        setScrollsOnExpand( true );
    }

}
