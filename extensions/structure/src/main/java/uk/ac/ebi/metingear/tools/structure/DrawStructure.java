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

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.AtomContainerSet;
import org.openscience.cdk.silent.ChemModel;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.jchempaint.JChemPaintPanel;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/** @author John May */
public final class DrawStructure extends AbstractControlDialog {

    private final JChemPaintPanel panel;
    private       boolean         confirmed;

    public DrawStructure(Window window) {
        super(window);
        IChemModel model = new ChemModel();
        model.setMoleculeSet(new AtomContainerSet());
        model.getMoleculeSet().addAtomContainer(new AtomContainer(0, 0, 0, 0));
        this.panel = new JChemPaintPanel(model);
        this.panel.setPreferredSize(new Dimension(800, 494));
    }

    @Override
    public JComponent createForm() {
        return panel;
    }

    @Override public void prepare() {
        confirmed = false;
    }

    @Override public void process() {

        // user pressed okay
        confirmed = true;

        // no selection - can't set the structure for a metabolite
        if (getSelection(Metabolite.class).isEmpty())
            return;

        Metabolite m = getSelection(Metabolite.class).iterator().next();
        Annotation annotation = new AtomContainerAnnotation(getStructure());
        AddAnnotationEdit edit = new AddAnnotationEdit(m, annotation);
        edit.apply();
        addEdit(edit);
    }


    public void setStructure(IAtomContainer input) {

        IAtomContainer cpy;

        try {
            cpy = input.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError("CDK object could not be cloned");
        }

        if (!input.isEmpty() && !GeometryTools.has2DCoordinates(cpy)) {
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setUseTemplates(false); // faster
            for (IAtomContainer component : ConnectivityChecker.partitionIntoMolecules(cpy)
                                                               .atomContainers()) {
                sdg.setMolecule(component, false);
                try {
                    sdg.generateCoordinates();
                } catch (CDKException e) {
                    Logger.getLogger(getClass()).error("Coordinates could not be generated for a component - structure can not be edited");
                    return;
                }
            }
        }

        IChemModel model = new ChemModel();
        model.setMoleculeSet(new AtomContainerSet());
        model.getMoleculeSet().addAtomContainer(cpy);
        panel.setChemModel(model);
    }

    public IAtomContainer getStructure() {

        if (!confirmed)
            return null;

        IChemModel model = panel.getChemModel();
        IAtomContainer output = new AtomContainer();
        List<IStereoElement> stereoElements = new ArrayList<IStereoElement>();
        for (IAtomContainer container : model.getMoleculeSet().atomContainers()) {
            output.add(container);
            stereoElements.addAll(FluentIterable.from(container.stereoElements())
                                                .toList());
        }
                
        output.setStereoElements(StereoElementFactory.using2DCoordinates(output).createAll());
        
        return output;
    }
}
