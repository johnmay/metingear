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
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StarsCategory;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
import uk.ac.ebi.metabolomes.webservices.KeggCompoundWebServiceConnection;
import uk.ac.ebi.mnb.core.TooltipLabel;
import uk.ac.ebi.mnb.core.Utilities;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.view.DialogPanel;
import uk.ac.ebi.mnb.view.CheckBox;
import uk.ac.ebi.mnb.view.ComboBox;
import uk.ac.ebi.mnb.view.SelectionDialog;

/**
 *          AutomaticCrossReferenceDialog – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AutomaticCrossReference
        extends SelectionDialog {

    private static final Logger LOGGER = Logger.getLogger(AutomaticCrossReference.class);
    private List<AnnotatedEntity> components;
    private ChEBIWebServiceConnection chebiClient;
    private KeggCompoundWebServiceConnection keggClient;
    private JCheckBox chebi = new CheckBox("ChEBI (currated)");
    private JCheckBox kegg = new CheckBox("KEGG Compound");
    private JCheckBox chebiAll = new CheckBox("ChEBI (all)");
    private JSpinner results = new JSpinner(new SpinnerNumberModel(50, 10, 200, 10));

    public AutomaticCrossReference(JFrame frame, ViewController controller) {

        super(frame, controller, "RunDialog");

        chebiAll.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent ie) {
                chebi.setSelected(chebiAll.isSelected());
            }
        });

        setDefaultLayout();

    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Match name(s) to chemical databases");
        return label;
    }

    @Override
    public JPanel getOptions() {

        JPanel options = new DialogPanel();

        CellConstraints cc = new CellConstraints();

        options.setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"));
        options.add(chebi, cc.xy(1, 1));
        options.add(chebiAll, cc.xy(3, 1));
        options.add(kegg, cc.xy(1, 3));

        options.add(results, cc.xy(1, 5));

        options.add(new JSeparator(), cc.xyw(1, 7, 3));
        JLabel label = new TooltipLabel("Method", "<html>The method to use for name matching, Generally they<br> aim to improve recall at the cost of precision</html>", SwingConstants.RIGHT);
        options.add(label, cc.xy(1, 9));
        options.add(new ComboBox("Direct", "Fingerprint", "N-gram"), cc.xy(3, 9));

        return options;
    }

    @Override
    public void process() {

        chebiClient = chebiClient == null ? new ChEBIWebServiceConnection() : chebiClient;
        keggClient = keggClient == null ? new KeggCompoundWebServiceConnection() : keggClient;

        Collection<Metabolite> metabolties = Utilities.getMetabolites(getSelection());

        boolean useChEBI = chebi.isSelected() || chebiAll.isSelected();
        boolean useKegg = kegg.isSelected();

        chebiClient.setStarsCategory(chebiAll.isSelected() ? StarsCategory.ALL : StarsCategory.THREE_ONLY);
        chebiClient.setMaxResults((Integer) results.getValue());

        for (Metabolite metabolite : metabolties) {
            if (useChEBI) {
            }
            if (useKegg) {
            }
        }

    }

    @Override
    public boolean update() {
        // update
        return true;
    }
}
