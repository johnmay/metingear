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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.labels.MLabel;
import mnb.view.old.CachedMoleculeRenderer;
import mnb.view.old.ReactionArrow;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import uk.ac.ebi.chemet.entities.reaction.Reaction;


/**
 * ReactionPanel.java
 *
 *
 * @author johnmay
 * @date May 17, 2011
 */
public class ReactionPanel
  extends JComponent {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      ReactionPanel.class);
    private List<Reaction> reactions;
    private static final CachedMoleculeRenderer cachedRenderer = new CachedMoleculeRenderer();
    private int width;


    public ReactionPanel() {
        this.width = 500;
        setBackground(Color.WHITE);

    }


    public void setReaction(List<Reaction> reactions) {
        this.reactions = reactions;
        updateView();
    }


    public void updateView() {

        removeAll();

        if( reactions == null || reactions.isEmpty() ) {
            return;
        }


        // for each reaction
        // get the largest molecule to set the scale of molecules
        for( Reaction r : reactions ) {

            // todo
  //          IMoleculeSet reactants = r.getReactants();
  //          IMoleculeSet products = r.getProducts();
            // get the largest molecule to scale by
//            IMolecule largestReactant = null;
//            List<IMolecule> reactantList = new ArrayList<IMolecule>();
//            for ( int i = 0; i < reactants.getAtomContainerCount(); i++ ) {
//                IMolecule molecule = reactants.getMolecule( i );
//                if ( largestReactant == null ) {
//                    largestReactant = molecule;
//                }
//                if ( largestReactant.getAtomCount() < molecule.getAtomCount() ) {
//                    reactantList.add( largestReactant );
//                    largestReactant = molecule;
//                } else {
//                    reactantList.add( molecule );
//                }
//            }
//
//            IMolecule largestProduct = null;
//            List<IMolecule> productList = new ArrayList<IMolecule>();
//            for ( int i = 0; i < products.getAtomContainerCount(); i++ ) {
//                IMolecule molecule = products.getMolecule( i );
//                if ( largestProduct == null ) {
//                    largestProduct = molecule;
//                }
//                if ( largestProduct.getAtomCount() < molecule.getAtomCount() ) {
//                    productList.add( largestProduct );
//                    largestProduct = molecule;
//                } else {
//                    productList.add( molecule );
//                }
//            }

//            int totalMolecules = reactants.getAtomContainerCount() +
//                                 products.getAtomContainerCount() + 1; // add one for arrow
//
//            StringBuilder widthBuilder = new StringBuilder();
//            for( int i = 0 ; i < totalMolecules ; i++ ) {
//                widthBuilder.append("center:p");
//                if( i < totalMolecules - 1 ) {
//                    widthBuilder.append(", ");
//                }
//            }
//
//            Dimension dimension = new Dimension(40, 40);
//            Rectangle bounds = new Rectangle(0, 0, 40, 40);
//
//            FormLayout layout = new FormLayout(widthBuilder.toString(),
//                                               ViewUtils.goodiesFormHelper(reactions.size() + 3, 2,
//                                                                           false));
//            setLayout(layout);
//            CellConstraints cc = new CellConstraints();
//            int colIndex = 1;
//            for( int i = 0 ; i < reactants.getAtomContainerCount() ; i++ ) {
//                ImageIcon structure = new ImageIcon(cachedRenderer.getImageFromMolecule(reactants.
//                  getMolecule(i), bounds));
//                add(new JLabel(structure), cc.xy(colIndex, 3));
//                Label molLabel = new Label(reactants.getMolecule(i).getID());
//                molLabel.setFont(ViewUtils.HELVATICA_NEUE_PLAIN_11);
//                add(molLabel, cc.xy(colIndex, 5));
//                colIndex++;
//            }
//            add(new ReactionArrow(dimension), cc.xy(colIndex++, 3));
//            for( int i = 0 ; i < products.getAtomContainerCount() ; i++ ) {
//                ImageIcon structure = new ImageIcon(cachedRenderer.getImageFromMolecule(reactants.
//                  getMolecule(i), bounds));
//                add(new JLabel(structure), cc.xy(colIndex, 3));
//                Label molLabel = new Label(products.getMolecule(i).getID());
//                molLabel.setFont(ViewUtils.HELVATICA_NEUE_PLAIN_11);
//                add(molLabel, cc.xy(colIndex, 5));
//                colIndex++;
//            }
        }
    }


}

