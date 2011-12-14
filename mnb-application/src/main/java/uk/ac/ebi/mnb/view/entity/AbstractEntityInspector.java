/**
 * EntityInspector.java
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

import uk.ac.ebi.mnb.interfaces.SelectionManager;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import com.jgoodies.forms.layout.CellConstraints;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.ebi.mnb.settings.Settings;
import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.SelectionController;

/**
 *          EntityInspector – 2011.09.06 <br>
 *          Displays information on the selected entity in the table
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class AbstractEntityInspector
        extends JPanel
        implements ListSelectionListener, SelectionController {

    private static final Logger LOGGER = Logger.getLogger(AbstractEntityInspector.class);
    private boolean editable = false;
    private InspectorToolbar toolbar;
    private AbstractEntityTable table;
    private CellConstraints cc = new CellConstraints();
    private AnnotatedEntity entity;
    private AbstractEntityPanel panel;
    private static final Settings preferences = Settings.getInstance();

    public AbstractEntityInspector(AbstractEntityPanel panel) {
        this.panel = panel;
        panel.setup();
        toolbar = new InspectorToolbar(this);
        toolbar.setViewMode();
        setLayout(new BorderLayout());
        pane = new BorderlessScrollPane(panel);
        add(toolbar, BorderLayout.NORTH); // only if viewable
        add(pane, BorderLayout.CENTER);

    }

    public void store() {
        panel.store();
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        panel.setEditable(editable);
        update();
    }

    public boolean isEditable() {
        return this.editable;
    }

    /**
     * Sets the table this inspector is linked too
     */
    public void setTable(AbstractEntityTable table) {
        this.table = table;
    }

    /**
     * Updates the inspector with the currently selected component
     */
    @Override
    public boolean update() {
        if (table == null) {
            return false;
        }
        int selected = table.getSelectedRow();
        if (selected != -1) {
            entity = table.getModel().getEntity(table.convertRowIndexToModel(selected));
            if (panel.setEntity(entity)) {
                panel.update();
            }
            setDisplay();
            repaint();
            revalidate();

        } else {
            if (entity != null) {
                panel.update();
                setDisplay();
                repaint();
                revalidate();
            }
        }
        return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean update(SelectionManager selection) {
        if (selection.getEntities().contains(entity)) {
            panel.update();
            setDisplay();
            repaint();
            revalidate();
            return true;
        }
        return false;
    }

    private JScrollPane pane;

    private void setDisplay() {
        // called on update
        toolbar.setVisible((Boolean) preferences.get(Settings.VIEW_TOOLBAR_INSPECTOR));
    }

    public void valueChanged(ListSelectionEvent e) {
        update();
    }

    @Override
    public boolean setSelection(SelectionManager selection) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public SelectionManager getSelection() {
        return table.getSelection();
    }

    public void clear() {
        panel.setEntity(null);
        panel.clear();
        panel.revalidate();
        panel.repaint();
    }

}
