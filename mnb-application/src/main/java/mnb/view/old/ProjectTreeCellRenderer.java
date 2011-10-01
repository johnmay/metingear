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

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.core.Reconstruction;

/**
 * TreeRenderer.java
 *
 *
 * @author johnmay
 * @date Apr 18, 2011
 */
public class ProjectTreeCellRenderer
        extends DefaultTreeCellRenderer {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ProjectTreeCellRenderer.class );
    private DefaultTreeCellRenderer activeProjectRenderer;

    public ProjectTreeCellRenderer() {
        super();

        activeProjectRenderer = new DefaultTreeCellRenderer();
        activeProjectRenderer.setFont( ViewUtils.HELVATICA_NEUE_BOLD_15 );

    }

    @Override
    public Component getTreeCellRendererComponent( JTree tree ,
                                                   Object node ,
                                                   boolean selected ,
                                                   boolean expanded ,
                                                   boolean leaf ,
                                                   int row ,
                                                   boolean hasFocus ) {

        super.getTreeCellRendererComponent( tree , node , selected ,
                                            expanded , leaf , row ,
                                            hasFocus );

        this.setFont( ViewUtils.HELVATICA_NEUE_PLAIN_13 );

        // need to fix this
        if ( node instanceof Reconstruction ) {
            if ( ReconstructionManager.getInstance().getActiveReconstruction().equals( node ) ) {
                //return activeProject( tree , node , selected , expanded , leaf , row , hasFocus );
                this.setFont( ViewUtils.HELVATICA_NEUE_BOLD_13 );
            }
            this.setIcon( projectIcon );

        } else if ( node instanceof DefaultMutableTreeNode ) {
            DefaultMutableTreeNode dmtNode = ( DefaultMutableTreeNode ) node;
            if ( dmtNode.getChildCount() != 0 ) {
                return new ProjectContentsContainer( dmtNode.toString() , productIcon , dmtNode.getChildCount() );

            }
        }



        return this;
    }

    public Component activeProject( JTree tree ,
                                    Object value ,
                                    boolean selected ,
                                    boolean expanded ,
                                    boolean leaf ,
                                    int row ,
                                    boolean hasFocus ) {
        activeProjectRenderer.getTreeCellRendererComponent( tree , value , selected ,
                                                            expanded , leaf , row ,
                                                            hasFocus );
        activeProjectRenderer.setIcon( projectIcon );
        return activeProjectRenderer;
    }
    private ImageIcon projectIcon = ViewUtils.icon_32x32;
    private ImageIcon productIcon = ViewUtils.createImageIcon( "images/geneproduct_16x16.png" , "Product Icon" );
}
