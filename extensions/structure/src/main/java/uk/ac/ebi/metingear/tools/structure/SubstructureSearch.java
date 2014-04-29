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
import net.sf.furbelow.SpinningDialWaitIndicator;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.jchempaint.JChemPaintPanel;
import uk.ac.ebi.chemet.render.source.EntitySubset;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.core.WarningMessage;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/** @author John May */
public final class SubstructureSearch extends AbstractControlDialog {

    private final JChemPaintPanel panel;

    public SubstructureSearch(Window window) {
        super(window);
        setModal(false);
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

    @Override public void process(final SpinningDialWaitIndicator indicator) {
        IAtomContainer query = getStructure();
        Pattern        pat = Pattern.findSubstructure(query);

        Reconstruction   recon  = DefaultReconstructionManager.getInstance().active();
        EntityCollection matches = new EntitySubset("structures containing " + toSmi(query), recon);

        int queried = 0, matched = 0;
        int nMetabolites = recon.metabolome().size();
        for (Metabolite m : recon.metabolome()) {
            queried++;
            for (ChemicalStructure cs : m.getStructures()) {
                try {
                    if (pat.matches(cs.getStructure())) {
                        // store matched
                        matched++;
                        matches.add(m);
                        break; // found a match on one of the structures
                    }
                } catch (Exception e) {
                    addReport(new WarningMessage("A structure of " + m.getIdentifier() + " could not be tested: " + e.getMessage()));
                }
            }
            if (queried % 50 == 0) {
                final String mesg = String.format("%.2f %% matched (%d found)", 100d * queried / nMetabolites, matched);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        indicator.setText(mesg);
                    }
                });
            }
        }

        recon.addSubset(matches);
    }

    private String toSmi(IAtomContainer container) {
        try {
            return SmilesGenerator.isomeric().create(container);
        } catch (CDKException e) {
            return " n/a";
        }
    }

    private IAtomContainer getStructure() {

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
