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
package uk.ac.ebi.mnb.dialog.tools.curate;

import com.jgoodies.forms.layout.CellConstraints;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import uk.ac.ebi.caf.component.ExpandingComponentList;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.Synonym;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.tool.domain.PeptideFactory;
import uk.ac.ebi.mdk.ui.tool.annotation.CrossreferenceModule;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;


/**
 * PeptideGenerator 2012.01.24
 *
 * @author johnmay
 * @author $Author$ (this version)
 *
 *         Class provides a UI to use the {@see PeptideFactory}
 * @version $Rev$ : Last Changed $Date$
 */
public class PeptideGenerator implements CrossreferenceModule {

    private static final Logger LOGGER = Logger.getLogger(PeptideGenerator.class);

    private JComponent component;

    private CellConstraints cc = new CellConstraints();

    private PeptideFactory factory;

    private ExpandingComponentList<JComboBox> expandingList;

    private Metabolite context;
    private final UndoManager undoManager;


    public PeptideGenerator(Window window, EntityFactory factory, UndoManager undoManager) {
        this.factory = new PeptideFactory(factory);
        this.undoManager = undoManager;
        expandingList = new ExpandingComponentList<JComboBox>(window) {
            @Override public JComboBox newComponent() {
                return new JComboBox(PeptideFactory.AminoAcid.values());
            }
        };
        expandingList.setBackground(window.getBackground());
        component = expandingList.getComponent();
    }


    public String getDescription() {
        return "Generate Peptide";
    }


    public JComponent getComponent() {
        return component;
    }


    public void setup(Metabolite metabolite) {
        context = metabolite;

        PeptideFactory.AminoAcid[] aa = factory.guessPeptide(context.getName());

        expandingList.setSize(aa.length); // ensure capacity
        for (int i = 0; i < aa.length; i++) {
            expandingList.getComponent(i).setSelectedItem(aa[i]);
        }


    }


    public boolean canTransferAnnotations() {
        return true;
    }


    public void transferAnnotations() {
        // make the peptide

        List<PeptideFactory.AminoAcid> aminoacids = new ArrayList<PeptideFactory.AminoAcid>(expandingList.getSize());

        for (int i = 0; i < expandingList.getSize(); i++) {
            aminoacids.add((PeptideFactory.AminoAcid) expandingList.getComponent(i).getSelectedItem());
        }

        PeptideFactory.AminoAcid[] chain = aminoacids.toArray(new PeptideFactory.AminoAcid[0]);


        try {
            // create the annotation and add to the undo manager and apply the actual edit
            Collection<? extends Annotation> annotations = Arrays.asList(new AtomContainerAnnotation(factory.generateStructure(chain)),
                                                                         new Synonym(factory.generateAbbreviation(chain)),
                                                                         new Synonym(factory.generateName(chain)));

            undoManager.addEdit(new AddAnnotationEdit(context, annotations));
            context.addAnnotations(annotations); // apply edit

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PeptideGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CDKException ex) {
            java.util.logging.Logger.getLogger(PeptideGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PeptideGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
