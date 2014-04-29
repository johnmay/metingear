/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
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

package uk.ac.ebi.mnb.dialog.tools;


import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.Metabolome;
import uk.ac.ebi.mdk.tool.RingClose;
import uk.ac.ebi.mdk.ui.render.molecule.MoleculeRenderer;
import uk.ac.ebi.mdk.ui.render.table.ChemicalStructureRenderer;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.undo.CompoundEdit;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author John May */
public final class Furanator extends ControllerDialog {

    private static final int ID_INDEX   = 0;
    private static final int ORG_INDEX  = 1;
    private static final int BOOl_INDEX = 2;
    private static final int MOD_INDEX  = 3;

    private StructureModifierModel          model;
    private JTable                          table;
    private Map<IAtomContainer, Metabolite> map;

    public Furanator(JFrame frame,
                     TargetedUpdate updater,
                     ReportManager messages,
                     SelectionController controller,
                     UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "Furanator");

        setDefaultLayout();
    }

    @Override public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Select open-chain compounds to convert to close-ring (furanose) form");
        return label;
    }

    @Override public JPanel getForm() {
        JPanel panel = super.getForm();
        model = new StructureModifierModel(new ArrayList<Metabolite>(),
                                           new ArrayList<IAtomContainer>(),
                                           new ArrayList<IAtomContainer>());
        table = new JTable(model);
        table.setDefaultRenderer(IAtomContainer.class, new ChemicalStructureRenderer());
        table.setRowHeight(256);
        table.getColumnModel().getColumn(0).setWidth(256);
        table.getColumnModel().getColumn(1).setWidth(10);
        table.getColumnModel().getColumn(2).setWidth(256);
        table.setSelectionBackground(Color.WHITE);
        table.setSelectionBackground(Color.WHITE);
        table.setSelectionForeground(Color.DARK_GRAY);
        table.setSelectionForeground(Color.DARK_GRAY);
        panel.add(new JScrollPane(table));
        return panel;
    }

    @Override public void prepare() {
        Reconstruction reconstruction = DefaultReconstructionManager.getInstance().active();
        map = metaboliteMap(reconstruction.metabolome());
        List<Metabolite> metabolites = new ArrayList<Metabolite>();
        List<IAtomContainer> org = new ArrayList<IAtomContainer>();
        List<IAtomContainer> mod = new ArrayList<IAtomContainer>();
        RingClose rc = new RingClose();
        for (IAtomContainer container : map.keySet()) {
            try {
                IAtomContainer cpy = container.clone();
                if (rc.closeFuranose(cpy)) {
                    metabolites.add(map.get(container));
                    org.add(container);
                    mod.add(cpy);
                    AtomContainerManipulator.suppressHydrogens(cpy);
                    MoleculeRenderer.getInstance().regenerateDiagram(cpy);
                }
            } catch (CloneNotSupportedException e) {
                // ignore - never thrown
            }
        }
        table.setModel(model = new StructureModifierModel(metabolites, org, mod));
    }

    @Override public void process() {

        CompoundEdit edit = new CompoundEdit();
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, BOOl_INDEX)) {
                IAtomContainer org = (IAtomContainer) model.getValueAt(i, ORG_INDEX);
                IAtomContainer mod = (IAtomContainer) model.getValueAt(i, MOD_INDEX);

                AddAnnotationEdit subedit = new AddAnnotationEdit(map.get(org), new AtomContainerAnnotation(mod));
                subedit.apply();
            }
        }

        edit.end();
        addEdit(edit);
    }

    Map<IAtomContainer, Metabolite> metaboliteMap(Metabolome metabolome) {
        Map<IAtomContainer, Metabolite> map = new HashMap<IAtomContainer, Metabolite>();
        for (Metabolite m : metabolome) {
            for (ChemicalStructure cs : m.getStructures())
                map.put(cs.getStructure(), m);
        }
        return map;
    }

    private final class StructureModifierModel extends DefaultTableModel {

        private final Metabolite[]     metabolites;
        private final IAtomContainer[] org, mod;
        private final boolean[] accept;

        StructureModifierModel(Collection<Metabolite> metabolites,
                               Collection<IAtomContainer> org, Collection<IAtomContainer> mod) {
            super(org.size(), 4);
            if (org.size() != mod.size() || org.size() != metabolites.size())
                throw new IllegalArgumentException();
            this.metabolites = metabolites.toArray(new Metabolite[metabolites.size()]);
            this.org = org.toArray(new IAtomContainer[org.size()]);
            this.mod = mod.toArray(new IAtomContainer[mod.size()]);
            this.accept = new boolean[org.size()];
        }

        @Override public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case ID_INDEX:
                    return "Id";
                case ORG_INDEX:
                    return "Input";
                case BOOl_INDEX:
                    return "Accept";
                case MOD_INDEX:
                    return "Ouput";
            }
            throw new IllegalArgumentException();
        }

        @Override public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case ID_INDEX:
                    return String.class;
                case ORG_INDEX:
                    return IAtomContainer.class;
                case BOOl_INDEX:
                    return Boolean.class;
                case MOD_INDEX:
                    return IAtomContainer.class;
            }
            throw new IllegalArgumentException();
        }

        @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == BOOl_INDEX; // accept only
        }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case ID_INDEX:
                    return metabolites[rowIndex].getAbbreviation() + " " + metabolites[rowIndex].getName();
                case ORG_INDEX:
                    return org[rowIndex];
                case BOOl_INDEX:
                    return accept[rowIndex];
                case MOD_INDEX:
                    return mod[rowIndex];
            }              
            return false;
        }

        @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == BOOl_INDEX && aValue instanceof Boolean)
                accept[rowIndex] = (Boolean) aValue;
        }

    }
}
