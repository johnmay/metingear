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

package uk.ac.ebi.mnb.dialog.tools.compare;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.list.MutableJListController;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.ChEBIIdentifier;
import uk.ac.ebi.mdk.domain.identifier.KEGGCompoundIdentifier;
import uk.ac.ebi.mdk.tool.AbstractEntityAligner;
import uk.ac.ebi.mdk.tool.MappedEntityAligner;
import uk.ac.ebi.mdk.ui.component.ResourceList;
import uk.ac.ebi.mdk.ui.component.compare.MatcherDescription;
import uk.ac.ebi.mdk.ui.component.compare.MatcherFactory;
import uk.ac.ebi.mdk.ui.component.compare.MatcherStackList;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.mnb.view.ReconstructionChooser;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.util.Collection;

/**
 * Align the currently active reconstruction to not active 'reference'.
 *
 * @author John May
 */
public class AlignReconstruction
        extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(AlignReconstruction.class);

    private MatcherStackList      matcherStack;
    private ReconstructionChooser reconstructionChooser;
    private ResourceList resources = new ResourceList();

    public AlignReconstruction(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {

        super(frame, updater, messages, controller, undoableEdits, "Okay");

        reconstructionChooser = new ReconstructionChooser();

        matcherStack = new MatcherStackList();
        for (MatcherDescription description : MatcherFactory.getInstance().getMethods()) {
            matcherStack.addElement(description);
        }

        matcherStack.setBackground(getBackground());
        matcherStack.setForeground(LabelFactory.newFormLabel("").getForeground());
        matcherStack.setVisibleRowCount(6);

        resources.addElement(new ChEBIIdentifier());
        resources.addElement(new KEGGCompoundIdentifier());

        setDefaultLayout();

    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            reconstructionChooser.refresh();
            matcherStack.setSelectedIndex(0);
        }
        super.setVisible(b);
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Aligns the current reconstruction to a selected reference reconstruction");
        return label;
    }

    @Override
    public JPanel getForm() {

        JPanel panel = super.getForm();

        panel.setLayout(new FormLayout("right:p, 4dlu, left:p:grow",
                                       "p, 4dlu, top:p"));

        CellConstraints cc = new CellConstraints();

        panel.add(LabelFactory.newFormLabel("Reference:"), cc.xy(1, 1));
        panel.add(reconstructionChooser.getComponent(), cc.xy(3, 1));
        panel.add(LabelFactory.newFormLabel("Method Stack:"), cc.xy(1, 3));
        panel.add(new MutableJListController(matcherStack).getListWithController(), cc.xy(3, 3));

        return panel;

    }

    @Override
    public void process() {

        Reconstruction reference = reconstructionChooser.getSelected();
        AbstractEntityAligner resolver = new MappedEntityAligner(reference.getMetabolome().toList());

        // set up the resolver
        for (MatcherDescription description : matcherStack.getElements()) {
            resolver.push(description.getMatcher());
        }

        Reconstruction active = DefaultReconstructionManager.getInstance().active();

        Collection<Metabolite> queries = getSelection().get(Metabolite.class);
        int matched = 0;
        for (Metabolite metabolite : queries) {
            if (!resolver.getMatches(metabolite).isEmpty())
                matched++;
        }

        System.out.println("Query size: " + queries.size());
        System.out.println("Reference size: " + reference.getMetabolome().size());
        System.out.println("Overlap: " + matched);


    }

}
