/**
 * MetabolitePanel.java
 *
 * 2011.09.30
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

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import java.util.Collection;
import java.util.List;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.apache.axis.utils.tcpmon;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.core.tools.TransportReactionUtil;
import uk.ac.ebi.core.tools.TransportReactionUtil.Classification;
import uk.ac.ebi.mnb.view.ReactionRenderer;
import uk.ac.ebi.mnb.view.entity.AbstractEntityPanel;
import uk.ac.ebi.mnb.view.labels.InternalLinkLabel;

/**
 *          MetabolitePanel – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReactionPanel
        extends AbstractEntityPanel {

    private static final Logger LOGGER = Logger.getLogger(ReactionPanel.class);
    private MetabolicReaction entity;
    private ReactionRenderer renderer = new ReactionRenderer();
    private JLabel reactionLabel = LabelFactory.newLabel("");
    private JComponent participantXref;
    private JLabel tClass = new JLabel();
    private CellConstraints cc = new CellConstraints();

    public ReactionPanel() {
        super("Metabolite", new AnnotationRenderer());
    }

    @Override
    public boolean update() {


        // update all fields and labels...
        reactionLabel.setIcon(renderer.getReaction(entity));
        updateParticipantXref();

        tClass.setIcon(renderer.getTransportClassificationIcon(TransportReactionUtil.getClassification(entity)));

        return super.update();

    }

    @Override
    public boolean setEntity(AnnotatedEntity entity) {
        this.entity = (MetabolicReaction) entity;
        return super.setEntity(entity);
    }

    /**
     * Returns the specific information panel
     */
    public JPanel getSynopsis() {

        JPanel panel = PanelFactory.createInfoPanel();
        panel.setBorder(Borders.DLU4_BORDER);

        panel.add(tClass);

        return panel;

    }

    @Override
    public JPanel getBasicPanel() {

        JPanel panel = super.getBasicPanel();

        FormLayout layout = (FormLayout) panel.getLayout();

        participantXref = PanelFactory.createInfoPanel();

        // add a row
        layout.appendRow(new RowSpec(RowSpec.CENTER, Sizes.DLUY4, RowSpec.DEFAULT_GROW));
        layout.appendRow(new RowSpec(RowSpec.CENTER, Sizes.PREFERRED, RowSpec.DEFAULT_GROW));
        panel.add(reactionLabel, cc.xyw(1, layout.getRowCount(), layout.getColumnCount(), cc.CENTER, cc.CENTER));

        layout.appendRow(new RowSpec(RowSpec.CENTER, Sizes.PREFERRED, RowSpec.NO_GROW));
        panel.add(participantXref, cc.xyw(1, layout.getRowCount(), layout.getColumnCount(), cc.CENTER, cc.CENTER));


        return panel;

    }

    // updates the participant xref panel
    private void updateParticipantXref() {

        participantXref.removeAll();

        if (entity == null) {
            return;
        }

        int size = entity.getAllReactionMolecules().size();

        List<Metabolite> reactants = entity.getReactantMolecules();
        List<Metabolite> products = entity.getProductMolecules();
        String columnLayout = "";
        for (int i = 0; i < reactants.size(); i++) {
            columnLayout = columnLayout + 128 + "px" + ", ";
            if (i + 1 < reactants.size()) {
                columnLayout = columnLayout + +15 + "px" + ", ";
            }
        }
        columnLayout = columnLayout + 128 + "px";
        for (int i = 0; i < products.size(); i++) {
            columnLayout = columnLayout + ", " + 128 + "px";
            if (i + 1 < products.size()) {
                columnLayout = columnLayout + ", " + 15 + "px";
            }
        }

        participantXref.setLayout(new FormLayout(columnLayout, "p"));


        int columnIndex = 1;
        for (int i = 0; i < reactants.size(); i++) {
            Metabolite m = reactants.get(i);
            Double coef = entity.getReactantStoichiometries().get(i);
            String coefString = coef == 1d ? "" : coef % 1 == 0 ? String.format("%.0f ", coef) : coef.toString() + " ";

            Box box = Box.createHorizontalBox();
            box.add(LabelFactory.newFormLabel(coefString));
            box.add(new InternalLinkLabel(m, m.getName(),
                                          (SelectionController) MainView.getInstance().getViewController()));


            participantXref.add(
                    box,
                    cc.xy(columnIndex, 1, cc.CENTER, cc.CENTER));
            columnIndex += i + 1 < reactants.size() ? 2 : 1;
        }
        columnIndex += 1; // hop over reaction arrow
        for (int i = 0; i < products.size(); i++) {
            Metabolite m = products.get(i);
            Double coef = entity.getProductStoichiometries().get(i);
            String coefString = coef == 1d ? "" : coef % 1 == 0 ? String.format("%.0f ", coef) : coef.toString() + " ";

            Box box = Box.createHorizontalBox();
            box.add(LabelFactory.newFormLabel(coefString));
            box.add(new InternalLinkLabel(m, m.getName(),
                                          (SelectionController) MainView.getInstance().getViewController()));

            participantXref.add(
                    box,
                    cc.xy(columnIndex, 1, cc.CENTER, cc.CENTER));
            columnIndex += i + 1 < products.size() ? 2 : 1;
        }



    }

    @Override
    public Collection<? extends AnnotatedEntity> getReferences() {
        return entity.getModifiers();
    }
}
