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
 */package mnb.view.old;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.metabolomes.core.gene.GeneProduct;

/**
 * GenesTreeNode.java
 *
 *
 * @author johnmay
 * @date Apr 18, 2011
 */
public class GenesTreeNode
        extends DefaultMutableTreeNode {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( GenesTreeNode.class );
    private Reconstruction project;

    public GenesTreeNode( Reconstruction project ) {
        super( "Genes" , true );
        this.project = project;
    }

    @Override
    public TreeNode getChildAt( int childIndex ) {
        return new DefaultMutableTreeNode( project.getGeneProducts().getAllProducts()[childIndex] );
    }

    @Override
    public int getChildCount() {
        return project.getGeneProducts().numberOfProducts();
    }

    @Override
    public int getIndex( TreeNode node ) {
        return project.getGeneProducts().indexof( (GeneProduct) ((DefaultMutableTreeNode)node).getUserObject() );
    }
}
