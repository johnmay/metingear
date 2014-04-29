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

package uk.ac.ebi.mnb.dialog.tools.curate;

import com.jgoodies.forms.layout.CellConstraints;
import org.openscience.cdk.smiles.Beam;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.NameToStructureConfig;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.utility.TextUtility;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.SMILES;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.ui.render.molecule.MoleculeRenderer;
import uk.ac.ebi.mdk.ui.tool.annotation.CrossreferenceModule;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Use opsin to input acyl nomeculature for ACP attached metabolites.
 * 
 * @author John May
 */
public class ACPGenerator implements CrossreferenceModule {

    private final JTextField field = FieldFactory.newField(50);
    private final JLabel     label = new JLabel();
    private final UndoManager undoManager;
    private       OpsinResult result;
    private       Metabolite  metabolite;
    private final JPanel      component;
    
    private String defaultMesg = "Enter nomeculature that ends in aycl. An ACP psuedo-atom" +
            "is attached via a 'oyl' or 'thio'. Examples: " +
            "'octadecanoyl', '(5Z)-octadec-5-enoyl' and 'octadecanoyl-thio'." +
            "Nomenculature is parsed using OPSIN (http://opsin.ch.cam.ac.uk/)";

    public ACPGenerator(UndoManager undoManager) {
        this.undoManager = undoManager;
        label.setPreferredSize(new Dimension(256, 256));
        // warmup opsin
        new Thread(new Runnable() {
            @Override public void run() {
                NameToStructure.getInstance().parseChemicalName("ethanol");
            }
        }).run();

        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                update(field.getText());
            }

            @Override public void removeUpdate(DocumentEvent e) {
                label.setText(null);
                label.setIcon(null);
                update(field.getText());
            }

            @Override public void changedUpdate(DocumentEvent e) {
                label.setText(null);
                label.setIcon(null);
                update(field.getText());
            }
        });

        JPanel panel = PanelFactory.createDialogPanel("p", "p, 4dlu, p");
        panel.add(label, new CellConstraints(1, 1));
        panel.add(field, new CellConstraints(1, 3));
        component = panel;
    }

    private void update(final String name) {
        NameToStructureConfig config = NameToStructureConfig.getDefaultConfigInstance();
        config.setAllowRadicals(true);
        this.result = NameToStructure.getInstance().parseChemicalName(name, config);
        if (result.getStatus() == OpsinResult.OPSIN_RESULT_STATUS.FAILURE) {
            field.setBackground(new Color(0xFF9999));
            label.setText(TextUtility.html(result.getMessage()));
        }
        else if (result.getStatus() == OpsinResult.OPSIN_RESULT_STATUS.SUCCESS) {
            field.setBackground(new Color(0x99EE99));
            label.setText(null);
            try {
                String smi = result.getSmiles();
                int i;
                if ((i = smi.indexOf("[C]")) >= 0) {
                    smi = smi.substring(0, i + 3) + "(S[ACP])" + smi.substring(i + 3);
                    label.setIcon(new ImageIcon(MoleculeRenderer.getInstance().getImage(Beam.fromSMILES(smi), 256)));
                }
                else if ((i = smi.indexOf("[S]")) >= 0) {
                    smi = smi.substring(0, i + 3) + "([ACP])" + smi.substring(i + 3);
                    label.setIcon(new ImageIcon(MoleculeRenderer.getInstance().getImage(Beam.fromSMILES(smi), 256)));
                }
                else {
                    field.setBackground(new Color(0xFF9999));
                    label.setText(TextUtility.html("No attachment point, the input name should end 'oyl', for example 'ethanoyl' or 'ethanoyl-thio'"));
                    result = null;
                }
            } catch (Exception e) {

            }
        }
        else if (result.getStatus() == OpsinResult.OPSIN_RESULT_STATUS.WARNING) {
            field.setBackground(new Color(0xDDDD99));
            label.setText(TextUtility.html(result.getMessage()));
        }
    }

    @Override
    public String getDescription() {
        return "Generate Acyl-Carrier-Protein (ACP) attachment";
    }

    @Override public JComponent getComponent() {
        return component;
    }

    @Override public void setup(Metabolite metabolite) {
        label.setIcon(null);
        if (metabolite.getName().endsWith("-ACP")) {
            String name = metabolite.getName();
            field.setText(name.substring(0, name.length() - 4));
        } else {
            field.setText("");
            label.setText(TextUtility.html(defaultMesg));
        }                                                                                                                       
        this.metabolite = metabolite;
    }

    @Override public boolean canTransferAnnotations() {
        return true;
    }

    @Override public void transferAnnotations() throws Exception {
        if (metabolite == null || result == null)
            return;
        String smi = result.getSmiles();
        if (smi == null)
            return;
        int i;
        if ((i = smi.indexOf("[C]")) >= 0) {
            smi = smi.substring(0, i + 3) + "(S[ACP])" + smi.substring(i + 3);
        }
        else if ((i = smi.indexOf("[S]")) >= 0) {
            smi = smi.substring(0, i + 3) + "([ACP])" + smi.substring(i + 3);
        } else {
            return;
        }
        
        Annotation annotation = new SMILES(Beam.fromSMILES(smi));
        metabolite.addAnnotation(annotation);
        undoManager.addEdit(new AddAnnotationEdit(metabolite, annotation));
    }
}
