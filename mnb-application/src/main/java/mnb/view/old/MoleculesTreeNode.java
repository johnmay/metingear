/*
 *     This file is part of Metabolic Network Builder
 *
 *     Metabolic Network Builder is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package mnb.view.old;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import uk.ac.ebi.core.Reconstruction;


/**
 * MoleculesTreeNode.java
 *
 *
 * @author johnmay
 * @date May 18, 2011
 */
public class MoleculesTreeNode
  extends DefaultMutableTreeNode {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      MoleculesTreeNode.class);
    private Reconstruction project;


    public MoleculesTreeNode(Reconstruction project) {
        super("Metabolites", true);
        this.project = project;
    }


    @Override
    public TreeNode getChildAt(int childIndex) {
        return new DefaultMutableTreeNode(project.getMetabolites().get(childIndex).getIdentifier());
    }


    @Override
    public int getChildCount() {
        return project.getMetabolites().size();
    }


    @Override
    public int getIndex(TreeNode node) {
        return project.getMetabolites().indexOf(((DefaultMutableTreeNode) node).getUserObject());
    }


}

