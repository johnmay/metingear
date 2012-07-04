/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
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
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.edit.ReplaceAnnotationEdit;

import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A simple tool that allows generation of 2D coordinates using the CDK Structure Diagram
 * Generator. As the diagram generator does not respect chirality it is not recomended for
 * cases where an adequate diagram is already available.
 *
 * @author John May
 * @see GenerateStructureDiagramPlugin
 */
public class GenerateStructureDiagram
        extends AbstractControlDialog {

    private static final Logger LOGGER = Logger.getLogger(GenerateStructureDiagram.class);

    // don't want to make these static as it would increace class loading time
    private final StructureDiagramGenerator STRUCTURE_DIAGRAM_GENERATOR = new StructureDiagramGenerator();
    private final IChemObjectBuilder        BUILDER                     = SilentChemObjectBuilder.getInstance();

    private JCheckBox overwrite = CheckBoxFactory.newCheckBox();

    public GenerateStructureDiagram(Window window) {
        super(window);
    }

    @Override
    public JLabel createInformation() {
        JLabel label = super.createInformation();
        label.setText("Generate a structure diagram for selected metabolites");
        return label;
    }

    @Override
    public JComponent createForm() {

        JComponent component = super.createForm();

        component.setLayout(new FormLayout("right:p, 4dlu, left:p",
                                           "p"));

        CellConstraints cc = new CellConstraints();

        component.add(LabelFactory.newFormLabel("Overwrite:",
                                                "Indicates structures with existing coordinates should have" +
                                                        " a new diagram generated (not recommended)"),
                      cc.xy(1, 1));
        component.add(overwrite,
                      cc.xy(3, 1));

        return component;

    }

    @Override
    public void process() {

        // put all the edits together
        CompoundEdit edit = new CompoundEdit();

        for (Metabolite metabolite : getSelection(Metabolite.class)) {

            Collection<AtomContainerAnnotation> annotations = new ArrayList<AtomContainerAnnotation>(metabolite.getAnnotations(AtomContainerAnnotation.class));
            for (AtomContainerAnnotation original : annotations) {

                IAtomContainer structure = original.getStructure();

                if (!GeometryTools.has2DCoordinates(structure) || overwrite.isSelected()) {

                    try {

                        IMolecule molecule = BUILDER.newInstance(IMolecule.class, structure);

                        STRUCTURE_DIAGRAM_GENERATOR.setMolecule(molecule);
                        STRUCTURE_DIAGRAM_GENERATOR.generateCoordinates();

                        // convert back to IAtomContainer (IMolecule gets removed from CDK soon...)

                        AtomContainerAnnotation replacement = new AtomContainerAnnotation(BUILDER.newInstance(IAtomContainer.class, STRUCTURE_DIAGRAM_GENERATOR.getMolecule()));

                        edit.addEdit(new ReplaceAnnotationEdit(metabolite, original, replacement));

                        metabolite.removeAnnotation(original);
                        metabolite.addAnnotation(replacement);

                    } catch (CDKException ex) {
                        addReport(new WarningMessage("Unable to generate coordinates for structure: " + original.getShortDescription()));
                    }

                }

            }

        }

        // inform the compound edit that we've finished editing
        edit.end();

        addEdit(edit);

    }

    @Override
    public void update() {
        super.update(getSelectionController().getSelection());
    }
}
