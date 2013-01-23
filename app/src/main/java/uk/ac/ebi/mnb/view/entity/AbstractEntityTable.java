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
package uk.ac.ebi.mnb.view.entity;

import com.explodingpixels.macwidgets.plaf.ITunesTableUI;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.theme.ThemeManager;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.interfaces.EntityTable;
import uk.ac.ebi.mnb.interfaces.SelectionController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EntityTable – 2011.09.06 <br> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public abstract class AbstractEntityTable
        extends JTable
        implements EntityTable,
                   SelectionController {

    private static final Logger LOGGER = Logger.getLogger(AbstractEntityTable.class);
    private EntityCollection selection = new EntityMap(DefaultEntityFactory.getInstance());

    public AbstractEntityTable(AbstractEntityTableModel model) {
        super(model);
        setUI(new ITunesTableUI());
        setAutoscrolls(true);
        setFont(ThemeManager.getInstance().getTheme().getBodyFont());
        setAutoCreateRowSorter(true);
        // force double click to edit -> annoying because ctrl-z/cmd-z will start editing the cell
        putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
        setColumnModel(columnModel);
    }

    @Override
    public AbstractEntityTableModel getModel() {
        return (AbstractEntityTableModel) super.getModel();
    }

    /**
     * Update the table model with the current
     */
    public boolean update() {
        return getModel().update();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean update(EntityCollection selection) {
        return getModel().update(selection);
    }

    public EntityCollection getSelection() {
        List<AnnotatedEntity> components = new ArrayList();
        selection.clear();
        for (Integer index : getSelectedRows()) {
            selection.add(getModel().getEntity(convertRowIndexToModel(index)));
        }
        return selection;
    }

    /**
     * Sets a single selection in the table
     *
     * @param component
     */
    public boolean setSelection(EntityCollection selectionManager) {


        List<AnnotatedEntity> entities = new ArrayList<AnnotatedEntity>(selectionManager.getEntities());

        LOGGER.debug("selecting " + entities.size() + " entities");

        clearSelection();
        getSelectionModel().setValueIsAdjusting(true);

        for (int i = 0; i < entities.size(); i++) {
            int index = convertRowIndexToView(getModel().indexOf(entities.get(i)));
            if (index != -1) {
                addRowSelectionInterval(index, index);
            }
        }

        int selected = getSelectedRow();

        if (selected == -1) {
            return false;
        }

        Container parent = getParent();

        if (parent != null) {

            int y = getTableHeader().getHeight() + (getRowHeight() * selected) - ((int) parent.getHeight()
                    / 2);
            scrollRectToVisible(new Rectangle(0, y,
                                              parent.getWidth(),
                                              parent.getHeight()));

        }

        getSelectionModel().setValueIsAdjusting(false);

        return true;


    }

    public void clear() {
        getSelectionModel().clearSelection();
        getModel().clear();
    }
}