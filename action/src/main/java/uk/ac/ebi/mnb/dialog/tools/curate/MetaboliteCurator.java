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
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.service.ServiceManager;
import uk.ac.ebi.mdk.ui.component.MatchIndication;
import uk.ac.ebi.mdk.ui.component.table.MoleculeTable;
import uk.ac.ebi.mdk.ui.component.table.accessor.CrossReferenceAccessor;
import uk.ac.ebi.mdk.ui.component.table.accessor.NameAccessor;
import uk.ac.ebi.mdk.ui.tool.annotation.CrossreferenceModule;
import uk.ac.ebi.mnb.core.ExpandableComponentGroup;
import uk.ac.ebi.mnb.view.DropdownDialog;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;


/**
 * A dialog housing several curation modules to help in curating information on
 * a metabolite.
 */
public class MetaboliteCurator extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteCurator.class);

    private MoleculeTable table = new MoleculeTable(new CrossReferenceAccessor(), new NameAccessor());

    private CellConstraints cc = new CellConstraints();

    private JLabel desc;

    private String name;

    private boolean selected = false;

    private JPanel options;

    private boolean skipall = false;

    private final CrossreferenceModule[] modules;


    public MetaboliteCurator(JFrame frame, ServiceManager manager, UndoManager undoManager) {
        super(frame, "OkayDialog");
        getClose().setText("Skip");


        modules = new CrossreferenceModule[]{
                new DatabaseSearch(manager, undoManager),
                new AssignStructure(undoManager),
                new PeptideGenerator(this, DefaultEntityFactory.getInstance(), undoManager),
                new ManualCrossReferenceModule(this, undoManager),
                new WebSearch(undoManager),};
        setDefaultLayout();


    }

    private MatchIndication nameMatch = new MatchIndication(300, 300);

    private MatchIndication formulaMatch = new MatchIndication(300, 300);

    private MatchIndication chargeMatch = new MatchIndication(300, 300);

    private Metabolite query;

    public void setSkipall(boolean skipall) {
        this.skipall = skipall;
    }


    public void setup(Metabolite query) {

        this.query = query;

        String descText = String.format("Currently curating '%s':", query.getName());

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

    private static final boolean OS_X = System.getProperty("os.name").equals("Mac OS X");
    private static final String KEY_STROKE_MASK = OS_X ? "meta" : "ctrl";
    private static final String KEY_STROKE_SYMBOL = OS_X ? "⌘" : "^";

    @Override
    public JPanel getForm() {

        options = super.getForm();

        FormLayout layout = new FormLayout("p:grow");
        options.setLayout(layout);

        for (int i = 0; i < modules.length; i++) {
            CrossreferenceModule module = modules[i];

            layout.appendRow(new RowSpec(Sizes.PREFERRED));

            String moduleDescription = "<html>" + module.getDescription() + " [<b>" + KEY_STROKE_SYMBOL + (i + 1) + "</b>]</html>";
            final ExpandableComponentGroup expanding = new ExpandableComponentGroup(moduleDescription,
                                                                                    module.getComponent(), this);
            // allows quick switching with number keys
            expanding.registerKeyboardAction(new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    expanding.toggle();
                    options.revalidate();
                }
            }, KeyStroke.getKeyStroke(KEY_STROKE_MASK + " " + Integer.toString(i + 1)), JComponent.WHEN_IN_FOCUSED_WINDOW);
            options.add(expanding, cc.xy(1, layout.getRowCount()));
            layout.appendRow(new RowSpec(Sizes.DLUY4));
        }

        JPanel panel = PanelFactory.createDialogPanel();

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


    public Collection<Metabolite> getSelected() {
        return table.getSelectedEntities();
    }


    @Override
    public boolean update() {
        // do nothing
        return true;
    }
}
