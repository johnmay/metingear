/**
 * EntityView.java
 *
 * 2011.09.06
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
package uk.ac.ebi.mnb.view.entity;

import com.explodingpixels.macwidgets.plaf.EmphasizedLabelUI;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import com.jgoodies.forms.factories.Borders;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import uk.ac.ebi.chemet.render.ViewUtilities;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.interfaces.EntityTable;
import uk.ac.ebi.mnb.interfaces.EntityView;
import uk.ac.ebi.interfaces.entities.EntityCollection;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.caf.component.factory.LabelFactory;

/**
 *          EntityView – 2011.09.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AbstractEntityView
        extends JSplitPane
        implements EntityView {

    private static final Logger LOGGER = Logger.getLogger(AbstractEntityView.class);
    private final AbstractEntityTable table;
    private final AbstractEntityInspector inspector;
    private JLabel label = LabelFactory.emptyLabel(); // avoid null pointers

    public AbstractEntityView(String name,
                              AbstractEntityTable entityTable,
                              AbstractEntityInspector inspector) {

        this.table = entityTable;
        this.inspector = inspector;
        setName(name);
        setOrientation(JSplitPane.VERTICAL_SPLIT);
        setDividerSize(10);
        setBackground(ViewUtilities.BACKGROUND);
        JScrollPane tablePane = new BorderlessScrollPane(this.table);
        add(tablePane, JSplitPane.TOP);
        add(this.inspector, JSplitPane.BOTTOM);
        setBorders();
        inspector.setTable(entityTable);
        entityTable.getSelectionModel().addListSelectionListener(inspector);
        setDividerLocation(350);


        // action listener changes text on the bottom-bar
        entityTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                label.setText(table.getSelectedRowCount() + " of " + table.getRowCount() + " " + getName() + " selected");
                label.repaint();
                // set updater for context
                MainView.getInstance().getJMenuBar().updateContext();
            }
        });

    }

    private void setBorders() {
        // simple narrow border on top and bottom
        ((BasicSplitPaneUI) getUI()).getDivider().setBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0xa5a5a5)));
        setBorder(Borders.EMPTY_BORDER);
    }

    public void clear() {
        inspector.clear();
        table.clear();
        repaint();
    }

    /**
     *
     * Accessor to the table component of the split-pane
     *
     * @return Class extending EntityTable
     *
     */
    public EntityTable getTable() {
        return table;
    }

    public boolean update() {
        boolean updated = table.update();
        return inspector.update() || updated;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean update(EntityCollection selection) {
        boolean updated = table.update(selection);
        return inspector.update(selection) || updated;
    }

    /**
     *
     * Accessor to the inspector component of the split-pane
     *
     * @return Class extending the EntityInspector
     *
     */
    public AbstractEntityInspector getInspector() {
        return inspector;
    }

    @Override
    public EntityCollection getSelection() {
        return inspector.getSelection();
    }

    @Override
    public boolean setSelection(EntityCollection selection) {
        return table.setSelection(selection);
    }

    void setBottomBarLabel(JLabel label) {
        this.label = label;
        label.setUI(new EmphasizedLabelUI());
    }
}
