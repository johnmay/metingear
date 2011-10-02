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

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.mnb.view.GeneralPanel;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;

/**
 *          EntityInspector – 2011.09.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class EntityInspector
        extends GeneralPanel
        implements ListSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(EntityInspector.class);
    private boolean editable = false;
    private InspectorToolbar toolbar;
    private EntityTable table;
    private CellConstraints cc = new CellConstraints();
    private AnnotatedEntity component;
    private AnnotationRenderer renderer = new AnnotationRenderer();
    private static final Settings preferences = Settings.getInstance();
    private static Border PADDING_BORDER = Borders.DLU7_BORDER;
    private EntityPanel panel;

    public EntityInspector(EntityPanel panel) {
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
    public void setTable(EntityTable table) {
        this.table = table;
    }

    /**
     * Updates the inspector with the currently selected component
     */
    public void update() {
        if (table == null) {
            return;
        }
        int selected = table.getSelectedRow();
        if (selected != -1) {
            component = table.getModel().getEntity(table.convertRowIndexToModel(selected));
            if (panel.setEntity(component)) {
                panel.update();
            }
            setDisplay();
            repaint();
            revalidate();

        } else {
            if (component != null) {
                panel.update();
                setDisplay();
                repaint();
                revalidate();
            }
        }
    }

    /**
     * Access the currently displayed component
     * @return The active component
     */
    public AnnotatedEntity getActiveComponent() {
        return component;
    }
    private JScrollPane pane;

    private void setDisplay() {
        // called on update
        toolbar.setVisible((Boolean) preferences.get(Settings.VIEW_TOOLBAR_INSPECTOR));
    }

    public void valueChanged(ListSelectionEvent e) {
        update();
    }

    public List<AnnotatedEntity> getActiveComponents() {
        List<AnnotatedEntity> components = new ArrayList();
        for (Integer index : table.getSelectedRows()) {
            components.add(table.getModel().getEntity(index));
        }
        return components;
    }

    /**
     * Todo should return those containing an annotation of type
     * @param type
     * @return
     */
    public List<AnnotatedEntity> getActiveComponents(Class annotationType) {
        throw new UnsupportedOperationException("Not supported");
//        List<AnnotatedComponent> components = new ArrayList();
//        for( Integer index : table.getSelectedRows() ) {
//            components.add(table.getComponentTableModel().getEntity(index));
//        }
//        return components;
    }
}
