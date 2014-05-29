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

package uk.ac.ebi.metingear.tools.structure;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tautomer.TautomerStream;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.ui.render.molecule.MoleculeRenderer;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.edit.ReplaceAnnotationEdit;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author John May
 */
public final class ChooseTautomer extends AbstractControlDialog {

    private final JComboBox box;
    private final JList     list;
    private final DefaultComboBoxModel boxModel  = new DefaultComboBoxModel();
    private final DefaultListModel     listModel = new DefaultListModel();
    private final JLabel               label     = new JLabel("");
    private Metabolite m;

    public ChooseTautomer(Window window) {
        super(window);
        list = new JList(listModel);
        box = new JComboBox(boxModel);
        list.setFixedCellHeight(256);
        list.setFixedCellWidth(256);
        list.setSelectionBackground(new Color(0xCBD0FF));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
              
        ListCellRenderer renderer = new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                }
                else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                // unbox annotated structure
                if (value instanceof ChemicalStructure)
                    value = ((ChemicalStructure) value).getStructure();
                IAtomContainer container = (IAtomContainer) value;
                try {
                    setIcon(new ImageIcon(MoleculeRenderer.getInstance().getImage(container, new Rectangle(0, 0, 256, 256), getBackground())));
                } catch (CDKException e) {
                    setIcon(null);
                }

                return this;
            }
        };

        box.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                this.setText(value.getClass().getSimpleName());
                return this;
            }
        });
        label.setSize(new Dimension(128, 128));
        label.setPreferredSize(new Dimension(128, 128));
        list.setCellRenderer(renderer);

        box.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent e) {
                ChemicalStructure cs = (ChemicalStructure) box.getSelectedItem();
                if (cs == null)
                    return;
                try {
                    label.setIcon(new ImageIcon(MoleculeRenderer.getInstance().getImage(cs.getStructure(), 128)));
                    updateDisplayedTautomers(cs.getStructure());
                } catch (CDKException e1) {
                    System.err.println(e1.getMessage());
                }
            }
        });

    }

    private void updateDisplayedTautomers(IAtomContainer input) {
        listModel.clear();
        try {
            TautomerStream stream = new TautomerStream(input.clone());
            IAtomContainer next = null;
            while ((next = stream.next()) != null) {
                listModel.addElement(next.clone());
            }
        } catch (CloneNotSupportedException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override public void prepare() {
        m = getSelection(Metabolite.class).iterator().next();

        listModel.removeAllElements();
        boxModel.removeAllElements();
        for (ChemicalStructure cs : m.getStructures()) {
            boxModel.addElement(cs);
        }

        
    }

    @Override public JComponent createForm() {
        JComponent component = super.createForm();
        component.setLayout(new FormLayout("p, 4dlu, p, 4dlu, p:grow",
                                           "p, 4dlu, p"));
        JScrollPane pane = new JScrollPane(list);
        pane.setSize(new Dimension((int) (256 * 3.1), 128 * 2));
        pane.setMaximumSize(new Dimension((int) (256 * 3.1), 128 * 2));
        pane.setPreferredSize(new Dimension((int) (256 * 3.1), 128 * 2));
        component.add(LabelFactory.newFormLabel("Input:"), new CellConstraints(1, 1));
        component.add(box, new CellConstraints(3, 1));
        component.add(label, new CellConstraints(5, 1));
        component.add(LabelFactory.newFormLabel("Tautomers:"), new CellConstraints(1, 3));
        component.add(pane, new CellConstraints(3, 3, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
        return component;
    }


    @Override public void process() {
        ChemicalStructure cs = (ChemicalStructure) boxModel.getSelectedItem();
        IAtomContainer replacement = (IAtomContainer) list.getSelectedValue();
        if (m == null || replacement == null)
            return;
        ChemicalStructure replacementCS = (ChemicalStructure) cs.newInstance();
        replacementCS.setStructure(replacement);
        ReplaceAnnotationEdit rae = new ReplaceAnnotationEdit(m, cs, replacementCS);
        rae.apply();
        addEdit(rae);
    }
}
