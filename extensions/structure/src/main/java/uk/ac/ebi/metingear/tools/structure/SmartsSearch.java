/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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
import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.chemet.render.source.EntitySubset;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.core.WarningMessage;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/**
 * A
 *
 * @author John May
 */
public class SmartsSearch
        extends AbstractControlDialog {

    private static final Logger LOGGER = Logger.getLogger(SmartsSearch.class);

    private final IChemObjectBuilder BUILDER = SilentChemObjectBuilder.getInstance();

    private JTextField field = FieldFactory.newField(20);

    public SmartsSearch(Window window) {
        super(window);
    }

    @Override
    public JComponent createForm() {

        JComponent component = super.createForm();

        component.setLayout(new FormLayout("right:p, 4dlu, left:p",
                                           "p"));

        CellConstraints cc = new CellConstraints();

        component.add(getLabel("smartsPattern"), cc.xy(1, 1));
        component.add(field, cc.xy(3, 1));

        return component;

    }

    @Override public void process(final SpinningDialWaitIndicator indicator) {
        Reconstruction recon = DefaultReconstructionManager.getInstance().active();
        try {

            List<Metabolite> matches = new ArrayList<Metabolite>();

            SMARTSQueryTool sqt = new SMARTSQueryTool(field.getText(), BUILDER);
            int queried = 0, matched = 0;
            int nMetabolites = recon.metabolome().size();
            for (Metabolite m : recon.metabolome()) {
                queried++;
                for (ChemicalStructure cs : m.getStructures()) {
                    try {
                        if (sqt.matches(cs.getStructure())) {
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

            EntityCollection collection = new EntitySubset(field.getText() + " search", recon);
            collection.addAll(matches);
            recon.addSubset(collection);

        } catch (Exception e) {
            report(new ErrorMessage("An error occured whilst parsing/matching SMARTS: " + e.getMessage()));
        }
    }

    @Override
    public void update() {
        super.update(getSelectionController().getSelection());
    }

}
