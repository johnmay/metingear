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
package uk.ac.ebi.mnb.menu.view;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import uk.ac.ebi.core.ReconstructionManager;
import mnb.view.old.ReactionGraph;
import mnb.view.old.GraphPanel;
import uk.ac.ebi.metabolomes.core.gene.GeneProduct;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.core.GeneralAction;
import uk.ac.ebi.chemet.entities.reaction.Reaction;

/**
 * ReactionGraphAction.java
 *
 *
 * @author johnmay
 * @date May 27, 2011
 */
public class ReactionGraphAction
        extends GeneralAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ReactionGraphAction.class );

    public ReactionGraphAction() {
        super( "ReactionGraph" );
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        throw new UnsupportedOperationException("Old method");
//        //SelectionManager selectionManager = MainController.getInstance().getSelectionManager();
//        //GeneProductSelection selection = selectionManager.getGeneProductSelection();
//        GeneProduct[] products = ReconstructionManager.getInstance().getActiveReconstruction().getGeneProducts().getAllProducts();
//        System.out.println( products.length );
//        List<Reaction> reactionsToShow = new ArrayList();
//        for ( GeneProduct product : products ) {
//            if ( !product.getReactions().isEmpty() ) {
//                reactionsToShow.addAll( product.getReactions() );
//            }
//        }
//        GraphPanel gp = new GraphPanel();
//        MainFrame.getInstance().getViewController().add(gp, "Reaction Graph");
//        ((CardLayout)MainFrame.getInstance().getViewController().getLayout()).show( MainFrame.getInstance().getViewController() , "Reaction Graph");
//        ReactionGraph graph = new ReactionGraph();
//        graph.addReactions( reactionsToShow );
//        gp.setModel( graph );
//        gp.buildGraph();
//        gp.revalidate();
//        gp.repaint();

    }
}
