/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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
package uk.ac.ebi.mnb.view.entity.reaction;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReactionImpl;
import uk.ac.ebi.mdk.tool.domain.MassBalance;
import uk.ac.ebi.mdk.tool.domain.TransportReactionUtil;
import uk.ac.ebi.mdk.ui.edit.reaction.ReactionEditor;
import uk.ac.ebi.mdk.ui.render.reaction.ReactionRenderer;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.entity.AbstractEntityPanel;
import uk.ac.ebi.mnb.view.labels.InternalLinkLabel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * MetabolitePanel – 2011.09.30 <br> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class ReactionPanel
        extends AbstractEntityPanel {

    private static final Logger LOGGER = Logger.getLogger(ReactionPanel.class);

    private MetabolicReaction entity;

    private ReactionRenderer renderer = new ReactionRenderer();

    private JLabel reactionLabel = LabelFactory.newLabel("");

    private ReactionEditor editor = new ReactionEditor(null);

    private JComponent participantXref;

    private JLabel transportIcon = new JLabel();
    private JLabel balanceIcon   = new JLabel();

    private CellConstraints cc = new CellConstraints();


    public ReactionPanel() {
        super("Reaction");
    }


    @Override
    public boolean update() {

        if (super.update()) {

            // update all fields and labels...
            reactionLabel.setIcon(renderer.getReaction(entity));
            updateParticipantXref();

            TransportReactionUtil.Classification classification = TransportReactionUtil.getClassification(entity);
            transportIcon.setIcon(renderer.getTransportClassificationIcon(classification));
            transportIcon.setToolTipText(classification.toString());

            MassBalance.BalanceType balanceType = MassBalance.getBalanceClassification(entity);
            balanceIcon.setIcon(renderer.getBalanceTypeIcon(balanceType));
            balanceIcon.setToolTipText(balanceType.toString() + " " + MassBalance.deficit(entity));

            editor.setReaction(entity);

            return true;
        }

        return false;
    }

    @Override
    public boolean setEntity(AnnotatedEntity entity) {
        this.entity = (MetabolicReactionImpl) entity;
        return super.setEntity(entity);
    }


    /**
     * Returns the specific information panel
     */
    public JPanel getSynopsis() {

        JPanel panel = PanelFactory.createInfoPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setBorder(Borders.DLU4_BORDER);

        panel.add(transportIcon);
        panel.add(balanceIcon);

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

        layout.appendRow(new RowSpec(RowSpec.CENTER, Sizes.PREFERRED, RowSpec.NO_GROW));
        panel.add(editor.getComponent(), cc.xyw(1, layout.getRowCount(), layout.getColumnCount(), cc.CENTER, cc.CENTER));

        editor.getComponent().setVisible(false);

        return panel;

    }

    // updates the participant xref panel


    private void updateParticipantXref() {

        participantXref.removeAll();

        if (entity == null) {
            return;
        }

        int size = entity.getParticipants().size();

        List<MetabolicParticipant> reactants = entity.getReactants();
        List<MetabolicParticipant> products = entity.getProducts();
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
            Metabolite m = reactants.get(i).getMolecule();
            Double coef = entity.getReactants().get(i).getCoefficient();
            String coefString = coef == 1d ? "" : coef % 1 == 0
                                                  ? String.format("%.0f ", coef)
                                                  : coef.toString() + " ";

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
            Metabolite m = products.get(i).getMolecule();
            Double coef = entity.getProducts().get(i).getCoefficient();
            String coefString = coef == 1d ? "" : coef % 1 == 0
                                                  ? String.format("%.0f ", coef)
                                                  : coef.toString() + " ";

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
        List<AnnotatedEntity> entities = new ArrayList<AnnotatedEntity>();
        entities.addAll(DefaultReconstructionManager.getInstance().active().enzymesOf(entity));
        return entities;
    }


    @Override
    public void store() {

        entity = editor.getReaction();

        EntityCollection collection = new EntityMap(DefaultEntityFactory.getInstance());

        // update metabolite table for new entries
        for (MetabolicParticipant p : entity.getReactants()) {
            collection.add(p.getMolecule());
        }
        for (MetabolicParticipant p : entity.getProducts()) {
            collection.add(p.getMolecule());
        }


        MainView.getInstance().update();

        super.store();

    }


    @Override
    public void setEditable(boolean editable) {

        super.setEditable(editable);

        editor.getComponent().setVisible(editable);
        participantXref.setVisible(!editable);
        reactionLabel.setVisible(!editable);

    }
}
