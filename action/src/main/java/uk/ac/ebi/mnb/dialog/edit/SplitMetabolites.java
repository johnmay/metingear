/*
 * Copyright (c) 2012. John May <jwmay@users.sf.net>
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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import uk.ac.ebi.caf.component.factory.LabelFactory;
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

        form.setLayout(new FormLayout("90dlu, 4dlu, 90dlu", "p, 4dlu, p"));

        CellConstraints cc = new CellConstraints();

        left.setVisibleRowCount(10);
        right.setVisibleRowCount(10);
        left.setBackground(form.getBackground());
        right.setBackground(form.getBackground());
        left.setForeground(form.getForeground());
        right.setForeground(form.getForeground());
        left.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, form.getBackground().darker()));
        right.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, form.getBackground().darker()));

        form.add(LabelFactory.newLabel("left"), cc.xy(1, 1));
        form.add(LabelFactory.newLabel("right"), cc.xy(3, 1));

        JComponent leftComponent = new MutableJListController(left).getListWithController();
        JComponent rightComponent = new MutableJListController(right).getListWithController();

        leftComponent.setPreferredSize(new Dimension(250, (int) leftComponent.getPreferredSize().getHeight()));
        rightComponent.setPreferredSize(new Dimension(250, (int) rightComponent.getPreferredSize().getHeight()));

        form.add(leftComponent, cc.xy(1, 3));
        form.add(rightComponent, cc.xy(3, 3));

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
        final Collection<MetabolicReaction> reactions = manager.getActive().getReactome().getReactions(metabolite);

        left.removeAll();
        right.removeAll();
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
                                                           DefaultReconstructionManager.getInstance().getActive());

        addEdit(edit);

        edit.apply();


    }

}
