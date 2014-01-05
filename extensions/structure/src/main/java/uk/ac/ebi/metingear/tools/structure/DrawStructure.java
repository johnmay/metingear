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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IStereoElement;
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
        IChemModel model = new ChemModel();
        model.setMoleculeSet(new AtomContainerSet());
        model.getMoleculeSet().addAtomContainer(new AtomContainer(0, 0, 0, 0));
        panel.setChemModel(model);
    }

    @Override public void process() {
        IChemModel model = panel.getChemModel();
        IAtomContainer base = new AtomContainer();
        List<IStereoElement> stereoElements = new ArrayList<IStereoElement>();
        for (IAtomContainer container : model.getMoleculeSet().atomContainers()) {
            base.add(container);
            stereoElements.addAll(FluentIterable.from(container.stereoElements())
                                                .toList());
        }
        
        // stereo is not currently set -> check if it was set otherwise set the elements
        if (Iterables.size(base.stereoElements()) == 0) {
            if (!stereoElements.isEmpty()) {
                base.setStereoElements(stereoElements);
            } else {
                base.setStereoElements(StereoElementFactory.using2DCoordinates(base).createAll());
            }
        }

        Metabolite m = getSelection(Metabolite.class).iterator().next();
        Annotation annotation = new AtomContainerAnnotation(base);
        AddAnnotationEdit edit = new AddAnnotationEdit(m, annotation);
        edit.apply();

        addEdit(edit);
    }
}
