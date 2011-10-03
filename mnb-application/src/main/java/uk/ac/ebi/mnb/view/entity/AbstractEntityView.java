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

import com.jgoodies.forms.factories.Borders;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import uk.ac.ebi.mnb.view.ViewUtils;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.EntityView;

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

    public AbstractEntityView(AbstractEntityTable table,
            AbstractEntityInspector inspector) {

        this.table = table;
        this.inspector = inspector;
        setOrientation(JSplitPane.VERTICAL_SPLIT);
        setDividerSize(10);
        setBackground(ViewUtils.BACKGROUND);
        JScrollPane tablePane = new BorderlessScrollPane(this.table);
        add(tablePane, JSplitPane.TOP);
        add(this.inspector, JSplitPane.BOTTOM);
        setBorders();
        inspector.setTable(table);
        table.getSelectionModel().addListSelectionListener(inspector);

    }

    private void setBorders() {
        // simple narrow border on top and bottom
        ((BasicSplitPaneUI) getUI()).getDivider().setBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0xa5a5a5)));
        setBorder(Borders.EMPTY_BORDER);
    }

    /**
     *
     * Accessor to the table component of the split-pane
     *
     * @return Class extending EntityTable
     *
     */
    public AbstractEntityTable getTable() {
        return table;
    }

    public boolean update() {
        table.update();
        inspector.update();
        return true; // ? what return
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

    /**
     * Returns the currently selected component in the view
     * @return
     * @Deprecated use getSelection()
     */
    @Deprecated
    public AnnotatedEntity getSelectedEntity() {
        return inspector.getSelectedEntity();
    }

    public Collection<AnnotatedEntity> getSelection() {
        return inspector.getSelection();
    }

    public boolean setSelection(AnnotatedEntity annotatedComponent) {
        return table.setSelection(annotatedComponent);
    }

    public boolean setSelection(Collection<? extends AnnotatedEntity> entities) {
        return table.setSelection(entities);
    }

    


}
