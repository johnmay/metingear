/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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

package uk.ac.ebi.mnb.dialog.edit;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import uk.ac.ebi.caf.component.list.MutableJList;
import uk.ac.ebi.caf.component.list.MutableJListController;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicChemicalIdentifier;
import uk.ac.ebi.metingear.edit.entity.SplitMetaboliteEdit;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * @author John May
 */
public class SplitMetabolites extends ControllerDialog {

    private MutableJList<MetabolicReaction> left = new MutableJList<MetabolicReaction>(MetabolicReaction.class);
    private MutableJList<MetabolicReaction> right = new MutableJList<MetabolicReaction>(MetabolicReaction.class);

    public SplitMetabolites(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "SplitMetabolites");
        setDefaultLayout();
        left.setCellRenderer(new ReactionCellRenderer());
        right.setCellRenderer(new ReactionCellRenderer());
    }


    @Override
    public JLabel getDescription() {
        final JLabel label = super.getDescription();
        label.setText("Split a metabolite between reactions");
        label.setToolTipText("Divide the reactions into two sets by dragging and dropping");
        return label;
    }

    @Override
    public JPanel getForm() {

        final JPanel form = super.getForm();

        form.setLayout(new FormLayout("p:grow, 4dlu, p:grow", "top:min"));
        form.setBorder(Borders.createEmptyBorder("10dlu, 10dlu, 10dlu, 10dlu"));

        final CellConstraints cc = new CellConstraints();


        left.setVisibleRowCount(10);
        right.setVisibleRowCount(10);

        final Color bgColor = form.getBackground();

        left.setBackground(bgColor);
        right.setBackground(bgColor);

        // fix cell width/height - otherwise the lists can expand and be unaligned
        left.setFixedCellHeight(18);
        left.setFixedCellWidth(200);
        right.setFixedCellHeight(18);
        right.setFixedCellWidth(200);

        left.setForeground(form.getForeground());
        right.setForeground(form.getForeground());


//        form.add(LabelFactory.newLabel("left"), cc.xy(1, 1));
//        form.add(LabelFactory.newLabel("right"), cc.xy(3, 1));

        final JComponent leftComponent = new MutableJListController(left).getListWithController();
        final JComponent rightComponent = new MutableJListController(right).getListWithController();

        leftComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        rightComponent.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

        form.add(leftComponent, cc.xy(1, 1));
        form.add(rightComponent, cc.xy(3, 1));

        return form;

    }

    @Override
    public void prepare() {

        final Collection<Metabolite> metabolites = getSelection().get(Metabolite.class);

        if (metabolites.isEmpty())
            addMessage(new ErrorMessage("no metabolite was selected"));
        if (metabolites.size() > 1)
            addMessage(new ErrorMessage("unable to split more then one metabolite at once"));

        final Metabolite metabolite = metabolites.iterator().next();

        final ReconstructionManager manager = DefaultReconstructionManager.getInstance();
        final Collection<MetabolicReaction> reactions = manager.active().participatesIn(metabolite);

        left.getModel().removeAllElements();
        right.getModel().removeAllElements();

        for (final MetabolicReaction reaction : reactions) {
            left.addElement(reaction);
        }


    }

    @Override
    public void process() {

        final EntityFactory factory = DefaultEntityFactory.getInstance();

        final List<MetabolicReaction> leftReactions = left.getElements();
        final List<MetabolicReaction> rightReactions = right.getElements();

        // don't split if one of the reaction sets is empty
        if (leftReactions.isEmpty() || rightReactions.isEmpty())
            return;


        final Collection<Metabolite> metabolites = getSelection().get(Metabolite.class);

        if (metabolites.isEmpty())
            addMessage(new ErrorMessage("no metabolite was selected"));
        if (metabolites.size() > 1)
            addMessage(new ErrorMessage("unable to split more then one metabolite at once"));

        final Metabolite original = metabolites.iterator().next();

        Metabolite left = factory.newInstance(Metabolite.class,
                                              BasicChemicalIdentifier.nextIdentifier(),
                                              original.getName() + "_1",
                                              original.getAbbreviation() + "_1");
        Metabolite right = factory.newInstance(Metabolite.class,
                                               BasicChemicalIdentifier.nextIdentifier(),
                                               original.getName() + "_2",
                                               original.getAbbreviation() + "_2");

        // TODO: transfer annotations

        SplitMetaboliteEdit edit = new SplitMetaboliteEdit(original,
                                                           left, leftReactions,
                                                           right, rightReactions,
                                                           DefaultReconstructionManager.getInstance().active());

        addEdit(edit);

        edit.apply();


    }

    private static class ReactionCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            final JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            final String text = component.getText();
            if (text.length() > 40) {
                component.setToolTipText(text);
                component.setText(text.substring(0, 40) + "...");
            }
            return component;
        }
    }

}
