package mnb.io.resolve;

/**
 * CandidateSelector.java
 *
 * 2011.10.31
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
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Sizes;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.AbstractBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.metabolomes.webservices.util.SynonymCandidateEntry;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.visualisation.molecule.MoleculeTable;

/**
 *          CandidateSelector - 2011.10.31 <br>
 *          A dialog to select candidate reconciliation entries
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class CandidateSelector
        extends DropdownDialog
        implements ListSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(CandidateSelector.class);
    private MoleculeTable table = new MoleculeTable();
    private CellConstraints cc = new CellConstraints();
    private JLabel desc;
    private JLabel nameLabel = new JLabel();
    private JLabel matchLabel = new JLabel();
    private String name;
    private boolean selected = false;
    private JPanel options;
    private boolean skipall = false;

    public CandidateSelector(JFrame frame) {
        super(frame, "OkayDialog");
        setDefaultLayout();
        getClose().setText("Skip");
        table.getSelectionModel().addListSelectionListener(this);
    }

    public void setNameAndCandidates(String name, Collection<SynonymCandidateEntry> candidates) {

        this.name = name;

        desc.setText(String.format("The molecule '%s' was not found:", name));
        nameLabel.setText(name);

        this.table.getModel().setCandidates(candidates);

    }

    @Override
    public JLabel getDescription() {
        desc = super.getDescription();
        desc.setText(String.format("The molecule '%s' was not found:", name));
        return desc;
    }

    @Override
    public JPanel getOptions() {

        options = super.getOptions();
        options.setLayout(new FormLayout("p:grow, 4dlu, p, 4dlu, p:grow",
                                         "p, 4dlu, p, 4dlu, p"));

        nameLabel.setText(name);
        nameLabel.setBorder(BorderFactory.createCompoundBorder(new AbstractBorder() {

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                g.setColor(c.getForeground());
                g.drawRoundRect(x, y, width, height, 16, 16);
            }
        }, BorderFactory.createBevelBorder(BevelBorder.LOWERED) ));
        matchLabel.setText("?");

        options.add(nameLabel, cc.xy(1, 1));
        options.add(new JLabel("="), cc.xy(3, 1));
        options.add(matchLabel, cc.xy(5, 1));
        options.add(new JSeparator(), cc.xyw(1, 3, 5));
        options.add(new BorderlessScrollPane(table), cc.xyw(1, 5, 5));

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
        // do nothing
        selected = true;
    }

    public Collection<Metabolite> getSelected() {
        return selected ? table.getSelectedEntities() : new ArrayList();
    }

    @Override
    public boolean update() {
        // do nothing
        return true;
    }

    public void valueChanged(ListSelectionEvent e) {
        Collection<Metabolite> selection = getSelected();
        if (selection.iterator().hasNext()) {
            Metabolite m = selection.iterator().next();
            matchLabel.setText(m.getName());
        }
    }
}
