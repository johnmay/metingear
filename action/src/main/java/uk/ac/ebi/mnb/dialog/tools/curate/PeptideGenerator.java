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
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.Synonym;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.tool.domain.PeptideFactory;
import uk.ac.ebi.mdk.ui.tool.annotation.CrossreferenceModule;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


/**
 *
 *          PeptideGenerator 2012.01.24
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *
 *          Class provides a UI to use the {@see PeptideFactory}
 *
 */
public class PeptideGenerator implements CrossreferenceModule {

    private static final Logger LOGGER = Logger.getLogger(PeptideGenerator.class);

    private JPanel component;

    private CellConstraints cc = new CellConstraints();

    private PeptideFactory factory;

    private JComboBox[] boxes = new JComboBox[]{
        new JComboBox(PeptideFactory.AminoAcid.values()),
        new JComboBox(PeptideFactory.AminoAcid.values())
    };

    private Metabolite context;
    private final UndoManager undoManager;


    public PeptideGenerator(EntityFactory factory, UndoManager undoManager) {

        this.factory = new PeptideFactory(factory);
        this.undoManager = undoManager;

        component = PanelFactory.createDialogPanel("p, 4dlu, p, 4dlu, p, 4dlu, p", "p");

        component.add(LabelFactory.newFormLabel("N-Terminus:"), cc.xy(1, 1));
        component.add(boxes[0], cc.xy(3, 1));

        component.add(LabelFactory.newFormLabel("C-Terminus:"), cc.xy(5, 1));
        component.add(boxes[1], cc.xy(7, 1));

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

        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setSelectedItem(i < aa.length ? aa[i] : null);
        }

    }


    public boolean canTransferAnnotations() {
        return true;
    }


    public void transferAnnotations() {
        // make the peptide

        List<PeptideFactory.AminoAcid> aminoacids = new ArrayList<PeptideFactory.AminoAcid>(boxes.length);

        for (JComboBox comboBox : boxes) {
            aminoacids.add((PeptideFactory.AminoAcid) comboBox.getSelectedItem());
        }

        PeptideFactory.AminoAcid[] chain = aminoacids.toArray(new PeptideFactory.AminoAcid[0]);


        try {
            context.addAnnotation(new AtomContainerAnnotation(factory.generateStructure(chain)));
            context.addAnnotation(new Synonym(factory.generateName(chain)));
            context.addAnnotation(new Synonym(factory.generateAbbreviation(chain)));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PeptideGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CDKException ex) {
            java.util.logging.Logger.getLogger(PeptideGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PeptideGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
