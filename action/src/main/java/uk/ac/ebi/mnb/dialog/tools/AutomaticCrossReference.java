package uk.ac.ebi.mnb.dialog.tools;

/**
 * AutomaticCrossReferenceDialog.java
 *
 * 2011.09.30
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.google.common.collect.Multimap;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.caf.utility.TextUtility;
import uk.ac.ebi.mdk.domain.identifier.ChEBIIdentifier;
import uk.ac.ebi.mdk.domain.identifier.HMDBIdentifier;
import uk.ac.ebi.mdk.domain.identifier.KEGGCompoundIdentifier;
import uk.ac.ebi.mdk.service.query.LuceneServiceManager;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.service.ServiceManager;
import uk.ac.ebi.mdk.service.query.name.NameService;
import uk.ac.ebi.metabolomes.webservices.util.CandidateEntry;
import uk.ac.ebi.metabolomes.webservices.util.CandidateFactory;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.reconciliation.ChemicalFingerprintEncoder;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.util.ArrayList;
import java.util.List;


/**
 * AutomaticCrossReferenceDialog – 2011.09.30 <br>
 * Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class AutomaticCrossReference
        extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(AutomaticCrossReference.class);

    private JCheckBox chebi = CheckBoxFactory.newCheckBox("ChEBI");

    private JCheckBox kegg = CheckBoxFactory.newCheckBox("KEGG Compound");
    private JCheckBox hmdb = CheckBoxFactory.newCheckBox("HMDB");

    private JCheckBox approximate = CheckBoxFactory.newCheckBox("Use Approximate Match",
                                                                TextUtility.html("Uses approximate word matching when searching. Only " +
                                                                                         "names with '0' differences will be transferred but <br>" +
                                                                                         "using this method may yield new matches. Note: this method " +
                                                                                         "vastly reduces speed of the search "));

    private JSpinner results = new JSpinner(new SpinnerNumberModel(50, 10, 200, 10));


    public AutomaticCrossReference(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");
        setDefaultLayout();
    }


    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Match name(s) to chemical databases");
        return label;
    }


    @Override
    public JPanel getForm() {

        JPanel options = PanelFactory.createDialogPanel();

        CellConstraints cc = new CellConstraints();

        options.setLayout(new FormLayout("p, 4dlu, p, 4dlu, p", "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"));
        options.add(chebi, cc.xy(1, 1));
        options.add(kegg, cc.xy(3, 1));
        options.add(hmdb, cc.xy(5, 1));

        options.add(approximate, cc.xyw(1, 3, 3));

        options.add(new JSeparator(), cc.xyw(1, 5, 3));
        //        JLabel label = LabelFactory.newFormLabel("Method", TextUtility.html("The method to use for name matching, Generally they<br> aim to improve recall at the cost of precision"));
        //        options.add(label, cc.xy(1, 7));
        //        options.add(ComboBoxFactory.newComboBox("Direct", "Fingerprint", "N-gram"), cc.xy(3, 7));

        return options;
    }


    @Override
    public void process() {
        // not used
    }

    @Override
    public void process(final SpinningDialWaitIndicator indicator) {

        List<Metabolite> metabolites = new ArrayList<Metabolite>(getSelection().get(Metabolite.class));

        boolean useChEBI = chebi.isSelected();
        boolean useKegg = kegg.isSelected();
        boolean useHMDB = hmdb.isSelected();

        ServiceManager manager = LuceneServiceManager.getInstance();

        List<CandidateFactory> factories = new ArrayList<CandidateFactory>();
        if (useChEBI && manager.hasService(ChEBIIdentifier.class, NameService.class)) {
            factories.add(new CandidateFactory(manager.getService(ChEBIIdentifier.class, NameService.class),
                                               new ChemicalFingerprintEncoder()));
        }
        if (useKegg && manager.hasService(KEGGCompoundIdentifier.class, NameService.class)) {
            factories.add(new CandidateFactory(manager.getService(KEGGCompoundIdentifier.class, NameService.class),
                                               new ChemicalFingerprintEncoder()));
        }
        if (useHMDB && manager.hasService(HMDBIdentifier.class, NameService.class)) {
            factories.add(new CandidateFactory(manager.getService(HMDBIdentifier.class, NameService.class),
                                               new ChemicalFingerprintEncoder()));
        }


        for (int i = 0; i < metabolites.size(); i++) {

            Metabolite m = metabolites.get(i);
            final double progress = (double) i / metabolites.size();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    indicator.setText(String.format("Searching... %.1f %%", progress * 100));
                }
            });

            for (CandidateFactory factory : factories) {
                Multimap<Integer, CandidateEntry> map = approximate.isSelected()
                        ? factory.getFuzzySynonymCandidates(m.getName())
                        : factory.getSynonymCandidates(m.getName());
                if (map.containsKey(0)) {
                    for (CandidateEntry entry : map.get(0)) {
                        m.addAnnotation(factory.getCrossReference(entry));
                    }
                }
                map.clear();
                map = null;
            }
        }

        factories = null; // for cleanup
        System.gc();

    }

    @Override
    public boolean update() {
        return update(getSelection());
    }
}
