
/**
 * ReactionInspector.java
 *
 * 2011.09.26
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.view.entity.reaction;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.List;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.ReactionRenderer;
import uk.ac.ebi.mnb.view.entity.BasicAnnotationCellRenderer;
import uk.ac.ebi.mnb.view.labels.InternalLinkLabel;
import uk.ac.ebi.mnb.view.entity.EntityInspector;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.mnb.main.MainView;


/**
 *          ReactionInspector – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReactionInspector
  extends EntityInspector {

    private static final Logger LOGGER = Logger.getLogger(ReactionInspector.class);
    private CellConstraints cc = new CellConstraints();


    public ReactionInspector() {
        super(new ReactionPanel());

    }


  

    private GeneralPanel getMetabolicXref() {

        GeneralPanel panel = new GeneralPanel();

        MetabolicReaction rxn = (MetabolicReaction) getActiveComponent();

        int size = rxn.getAllReactionMolecules().size();

        List<Metabolite> reactants = rxn.getReactantMolecules();
        List<Metabolite> products = rxn.getProductMolecules();
        String columnLayout = "";
        for( int i = 0 ; i < reactants.size() ; i++ ) {
            columnLayout = columnLayout + 128 + "px" + ", ";
            if( i + 1 < reactants.size() ) {
                columnLayout = columnLayout + +15 + "px" + ", ";
            }
        }
        columnLayout = columnLayout + 128 + "px";
        for( int i = 0 ; i < products.size() ; i++ ) {
            columnLayout = columnLayout + ", " + 128 + "px";
            if( i + 1 < products.size() ) {
                columnLayout = columnLayout + ", " + 15 + "px";
            }
        }

        panel.setLayout(new FormLayout(columnLayout, "p"));


        int columnIndex = 1;
        for( int i = 0 ; i < reactants.size() ; i++ ) {
            Metabolite m = reactants.get(i);
            panel.add(
              new InternalLinkLabel(m, m.getName(), MainView.getInstance().getProjectPanel()),
              cc.xy(
              columnIndex, 1));
            columnIndex += i + 1 < reactants.size() ? 2 : 1;
        }
        columnIndex += 1; // hop over reaction arrow
        for( int i = 0 ; i < products.size() ; i++ ) {
            Metabolite m = products.get(i);
            panel.add(
              new InternalLinkLabel(m, m.getName(), MainView.getInstance().getProjectPanel()),
              cc.xy(
              columnIndex, 1));
            columnIndex += i + 1 < products.size() ? 2 : 1;
        }


        return panel;

    }


    @Override
    public void store() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }


}

