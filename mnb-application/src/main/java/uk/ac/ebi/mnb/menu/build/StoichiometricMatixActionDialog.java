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

package uk.ac.ebi.mnb.menu.build;

import com.google.common.collect.Iterables;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.Collection;
import javax.swing.JSeparator;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.view.DropdownDialog;
import mnb.view.old.MatrixModel;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.metabolomes.core.gene.GeneProteinProduct;
import uk.ac.ebi.resource.classification.ECNumber;
import uk.ac.ebi.mnb.main.MainView;


/**
 * StoichiometricMatixActionDialog.java
 *
 *
 * @author johnmay
 * @date May 19, 2011
 */
public class StoichiometricMatixActionDialog
  extends DropdownDialog {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      StoichiometricMatixActionDialog.class);


    public StoichiometricMatixActionDialog() {
        super(MainView.getInstance(), MainView.getInstance(), "BuildStoichiometricMatrix");
        layoutForm();
    }


    private void layoutForm() {
        FormLayout layout = new FormLayout("p,4dlu,p", "p,4dlu,p,4dlu,p,4dlu,p,4dlu");
        CellConstraints cc = new CellConstraints();
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        builder.add(new JSeparator(), cc.xyw(1, 1, 3));
        builder.add(getClose(), cc.xy(1, 5));
        builder.add(getActivate(), cc.xy(3, 5));
        builder.add(new JSeparator(), cc.xyw(1, 7, 3));
        add(builder.getPanel());
    }


    @Override
    public void process() {

        // build tasks and add to queue or just do it on our own without the task manaager
//        MatrixModel s = new MatrixModel();
//
//        Reconstruction activeProject = ReconstructionManager.getInstance().getActiveReconstruction();
//
//        GeneProteinProduct[] proteinProducts = activeProject.getGeneProducts().getProteinProducts();
//
//        for( GeneProteinProduct geneProteinProduct : proteinProducts ) {
//            Collection<EnzymeClassification> ecs =
//                                             geneProteinProduct.getAnnotations(
//              EnzymeClassification.class);
//            if( ecs.size() == 1 ) {
//                ECNumber ec = Iterables.get(ecs, 0).getIdentifier();
////todo                s.addReaction(ec, (BiochemicalReaction) geneProteinProduct.getReactions().get(0));
//            }
//        }
        // to fix

        
        
//        MainFrame.getInstance().getViewController().addMatrixView(s);



    }


    @Override
    public boolean update() {
        return true;
    }


}

