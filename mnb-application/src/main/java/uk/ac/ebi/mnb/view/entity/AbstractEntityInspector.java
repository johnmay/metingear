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

import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import java.awt.BorderLayout;
import java.util.Collection;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.mnb.view.GeneralPanel;
import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.SelectionController;

/**
 *          EntityInspector – 2011.09.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class AbstractEntityInspector
        extends GeneralPanel
        implements ListSelectionListener, SelectionController {

    private static final Logger LOGGER = Logger.getLogger(AbstractEntityInspector.class);
    private boolean editable = false;
    private InspectorToolbar toolbar;
    private AbstractEntityTable table;
    private CellConstraints cc = new CellConstraints();
    private AnnotatedEntity component;
    private AnnotationRenderer renderer = new AnnotationRenderer();
    private static final Settings preferences = Settings.getInstance();
    private static Border PADDING_BORDER = Borders.DLU7_BORDER;
    private AbstractEntityPanel panel;

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
    public boolean update() {
        if (table == null) {
            return false;
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
        return true;
    }

    /**
     * Access the currently displayed component
     * @return The active component
     * @deprecated use getSelection()
     */
    @Deprecated
    public AnnotatedEntity getSelectedEntity() {
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

    @Override
    public boolean setSelection(AnnotatedEntity entity) {
        // do nothing..
        return true;
    }

    @Override
    public boolean setSelection(Collection<? extends AnnotatedEntity> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

    public Collection<AnnotatedEntity> getSelection() {
        return table.getSelection();
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
