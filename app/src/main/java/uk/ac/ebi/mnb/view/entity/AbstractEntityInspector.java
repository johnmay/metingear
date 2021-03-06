/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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
package uk.ac.ebi.mnb.view.entity;

import com.jgoodies.forms.layout.CellConstraints;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.BorderlessScrollPane;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.view.entity.metabolite.MetaboliteInspector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * EntityInspector – 2011.09.06 <br> Displays information on the selected entity
 * in the table
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
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

    public AbstractEntityInspector(AbstractEntityPanel panel) {
        this.panel = panel;

        panel.setup();

        setLayout(new BorderLayout());


        // associate with this inspector (this) and the entity panel
        toolbar = new InspectorToolbar(this);
        pane = new BorderlessScrollPane(panel);
        
        this.add(toolbar, BorderLayout.NORTH);
        if (this instanceof MetaboliteInspector) {
            this.add(panel, BorderLayout.CENTER);
        } else {
            this.add(pane, BorderLayout.CENTER);
        }

    }

    public void store() {
        panel.store();
        panel.update();
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
     * Updates the inspector with the currently selected component. If multiple
     * rows are selected the first row is used (as is the case in
     * table.getSelectedRow())
     */
    @Override
    public boolean update() {
        if (table == null) {
            return false;
        }
        int selected = table.getSelectedRow();
        if (selected >= 0 && selected < table.getRowCount()) {
            entity = table.getModel()
                          .getEntity(table.convertRowIndexToModel(selected));

            panel.setEntity(entity);
            panel.update();
            repaint();
            revalidate();
        } else {
            if (entity != null) {
                panel.update();
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
    public boolean update(EntityCollection selection) {
        if (selection.getEntities().contains(entity)) {
            panel.update();
            repaint();
            revalidate();
            return true;
        }
        return false;
    }

    private JScrollPane pane;

    private void setToolbarDisplayed(boolean x) {
        toolbar.setVisible(x);
    }

    /**
     * Invoked on on a list selection change
     *
     * @param e
     */
    public void valueChanged(ListSelectionEvent e) {
        this.update();
    }

    @Override
    public boolean setSelection(EntityCollection selection) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public EntityCollection getSelection() {
        return table.getSelection();
    }

    public AnnotatedEntity getEntity() {
        return entity;
    }

    public void clear() {
        panel.setEntity(null);
        panel.clear();
        panel.revalidate();
        panel.repaint();
    }
}
