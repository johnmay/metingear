
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
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.ebi.mnb.core.ApplicationPreferences;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.mnb.view.GeneralPanel;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.interfaces.Annotation;
import uk.ac.ebi.mnb.view.ViewUtils;


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
    private static final ApplicationPreferences preferences = ApplicationPreferences.getInstance();
    private static Border PADDING_BORDER = Borders.DLU7_BORDER;
    private EntityPanelFactory panel;


    public EntityInspector(EntityPanelFactory panel) {
        this.panel = panel;
        toolbar = new InspectorToolbar(this);
        toolbar.setViewMode();
        setLayout(new FormLayout("p:grow", "min, 4dlu, p"));
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
        if( table == null ) {
            return;
        }
        int selected = table.getSelectedRow();
        if( selected != -1 ) {
            component = table.getModel().getEntity(table.convertRowIndexToModel(selected));
            if( panel.setEntity(component) ) {
                panel.update();
            }
            display();
            repaint();
            revalidate();
        } else {
            if( component != null ) {
                panel.update();
                display();
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


    private void display() {

        this.removeAll();
        if( (Boolean) preferences.get(ApplicationPreferences.VIEW_TOOLBAR_INSPECTOR) ) {
            add(toolbar, cc.xy(1, 1)); // only if viewable
        }

        add(panel.getPanel(), cc.xy(1, 3));

//        if( component instanceof AnnotatedEntity ) {
//
//            EntityPanelFactory factory = new EntityPanelFactory(getActiveComponent().getClass().
//              getSimpleName(), new AnnotationRenderer());
//            factory.setEntity(component);
//            factory.update();
//            GeneralPanel panel = new GeneralPanel();
//            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
//            panel.setBorder(PADDING_BORDER);
//            panel.add(factory.getBasicPanel());
//            JScrollPane pane = new BorderlessScrollPane(panel);
//            add(pane, cc.xy(1, 3));
//
//
//            GeneralPanel annotationPanel = new GeneralPanel(ViewUtils.formLayoutHelper(2, component.
//              getAnnotations().size(), 4, 4));
//            int rowIndex = 1;
//            for( Annotation annotation : component.getAnnotations() ) {
//                JComponent[] components = (JComponent[]) annotation.accept(renderer);
//                annotationPanel.add(components[0], cc.xy(1, rowIndex));
//                annotationPanel.add(components[1], cc.xy(3, rowIndex));
//                rowIndex += 2;
//            }
//            annotationPanel.setBorder(PADDING_BORDER);
//            panel.add(annotationPanel);
//
//        }


    }


    public void valueChanged(ListSelectionEvent e) {
        update();
    }


    public List<AnnotatedEntity> getActiveComponents() {
        List<AnnotatedEntity> components = new ArrayList();
        for( Integer index : table.getSelectedRows() ) {
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

