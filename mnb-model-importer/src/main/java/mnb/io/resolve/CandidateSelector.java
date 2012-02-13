package mnb.io.resolve;

/**
 * CandidateSelector.java
 *
 * 2011.10.31
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet. If not, see <http://www.gnu.org/licenses/>.
 */
import com.jgoodies.forms.layout.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.render.components.MatchIndication;
import uk.ac.ebi.interfaces.entities.Metabolite;
import uk.ac.ebi.metabolomes.webservices.util.CandidateEntry;
import uk.ac.ebi.mnb.core.ExpandableComponentGroup;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.interfaces.renderers.CrossreferenceModule;
import uk.ac.ebi.render.crossreference.modules.AssignStructure;
import uk.ac.ebi.render.crossreference.modules.GoogleSearch;
import uk.ac.ebi.render.crossreference.modules.PeptideGenerator;
import uk.ac.ebi.render.molecule.MoleculeTable;
import uk.ac.ebi.render.crossreference.modules.DatabaseSearch;
import uk.ac.ebi.visualisation.molecule.access.CrossReferenceAccessor;
import uk.ac.ebi.visualisation.molecule.access.NameAccessor;


/**
 * CandidateSelector - 2011.10.31 <br> A dialog to select candidate
 * reconciliation entries
 *
 * @version $Rev$ : Last Changed $Date: 2011-12-13 16:43:11 +0000 (Tue,
 * 13 Dec 2011) $
 * @author johnmay
 * @author $Author$ (this version)
 */
public class CandidateSelector
        extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(CandidateSelector.class);

    private MoleculeTable table = new MoleculeTable(new CrossReferenceAccessor(), new NameAccessor());

    private CellConstraints cc = new CellConstraints();

    private JLabel desc;

    private String name;

    private boolean selected = false;

    private JPanel options;

    private boolean skipall = false;

    private final CrossreferenceModule[] modules =
                                         new CrossreferenceModule[]{
        new DatabaseSearch(),
        new AssignStructure(),
        new PeptideGenerator(DefaultEntityFactory.getInstance()),
        new GoogleSearch()
    };


    public CandidateSelector(JFrame frame) {
        super(frame, "OkayDialog");
        setDefaultLayout();
        getClose().setText("Skip");


    }

    private MatchIndication nameMatch = new MatchIndication(300, 300);

    private MatchIndication formulaMatch = new MatchIndication(300, 300);

    private MatchIndication chargeMatch = new MatchIndication(300, 300);

    private Map<Metabolite, CandidateEntry> map = new HashMap();

    private Metabolite query;


    public void setSkipall(boolean skipall) {
        this.skipall = skipall;
    }


    public void setup(Metabolite query) {

        this.query = query;

        String descText = String.format("The molecule '%s' was not found:", query.getName());

        desc.setText(descText);
        desc.setToolTipText(descText);

        for (CrossreferenceModule module : modules) {
            module.setup(query);
        }


    }


    @Override
    public JLabel getDescription() {
        desc = super.getDescription();
        desc.setPreferredSize(new Dimension(500, 16));
        desc.setText(String.format("Resolve meta-data for '%s':", name));
        return desc;
    }


    @Override
    public JPanel getOptions() {

        options = super.getOptions();

        FormLayout layout = new FormLayout("p:grow");
        options.setLayout(layout);

        for (int i = 0; i < modules.length; i++) {
            CrossreferenceModule module = modules[i];

            layout.appendRow(new RowSpec(Sizes.PREFERRED));

            String moduleDescription = module.getDescription() + " [" + (i + 1) + "]";
            final ExpandableComponentGroup expanding = new ExpandableComponentGroup(moduleDescription,
                                                                                    module.getComponent(), this);

            options.add(expanding, cc.xy(1, layout.getRowCount()));

            options.getInputMap().put(KeyStroke.getKeyStroke(Integer.toString(i + 1)), new AbstractAction() {

                public void actionPerformed(ActionEvent e) {

                    expanding.toggle();
                }
            });

            layout.appendRow(new RowSpec(Sizes.DLUY4));
        }

        JPanel panel = PanelFactory.createDialogPanel();

        // wrap the whole thin in a pane
//        JScrollPane pane = new BorderlessScrollPane(options);
//
//        pane.getViewport().setBackground(panel.getBackground());
//        pane.setPreferredSize(new Dimension(800, 494));
//
//        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        panel.add(pane);

        return options;

    }


    @Override
    public JPanel getNavigation() {

        JPanel navigation = super.getNavigation();

        FormLayout layout = (FormLayout) navigation.getLayout();

        layout.insertColumn(1, new ColumnSpec(ColumnSpec.LEFT, Sizes.MINIMUM, ColumnSpec.NO_GROW));
        layout.insertColumn(2, new ColumnSpec(ColumnSpec.LEFT, Sizes.PREFERRED, ColumnSpec.DEFAULT_GROW));

        navigation.add(new JButton(new AbstractAction("Skip All") {

            public void actionPerformed(ActionEvent e) {
                skipall = true;
                setVisible(false);
            }
        }), cc.xy(1, 1));

        return navigation;

    }


    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            selected = false; // reset on each showing
        }
        if (visible && !skipall) {
            super.setVisible(visible);
        } else if (!visible) {
            super.setVisible(visible);
        }
    }


    @Override
    public void process() {

        for (CrossreferenceModule module : modules) {
            if (module.getComponent().isVisible()) {
                try {
                    module.transferAnnotations();
                } catch (Exception ex) {
                    System.err.println("TODO: Message needs to be written to GUI");
                    LOGGER.error(ex.getMessage());
                }
            }
        }

    }


    public Collection<uk.ac.ebi.interfaces.entities.Metabolite> getSelected() {
        return table.getSelectedEntities();
    }


    @Override
    public boolean update() {
        // do nothing
        return true;
    }
}
