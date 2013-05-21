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

import com.explodingpixels.macwidgets.plaf.ITunesTableUI;
import com.explodingpixels.widgets.TableUtils;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.theme.ThemeManager;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.metingear.TransferableEntity;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.interfaces.EntityTable;
import uk.ac.ebi.mnb.interfaces.SelectionController;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    private static final Logger LOGGER = Logger
            .getLogger(AbstractEntityTable.class);
    private EntityCollection selection = new EntityMap(DefaultEntityFactory
                                                               .getInstance());

    private boolean updating = false;

    public void addListSelectionListener(final ListSelectionListener listener) {
        // only forward when not updating
        getSelectionModel()
                .addListSelectionListener(new ListSelectionListener() {
                    @Override public void valueChanged(ListSelectionEvent e) {
                        if (!updating)
                            listener.valueChanged(e);
                    }
                });
    }

    public AbstractEntityTable(AbstractEntityTableModel model) {
        super(model);
        setUI(new ITunesTableUI());
        setAutoscrolls(true);
        setFont(ThemeManager.getInstance().getTheme().getBodyFont());
        setAutoCreateRowSorter(true);
        TableUtils.makeSortable(this, new TableUtils.SortDelegate() {
            @Override
            public void sort(int columnModelIndex, TableUtils.SortDirection sortDirection) {
                // handled by the auto row sorter
            }
        });
        // force double click to edit -> annoying because ctrl-z/cmd-z will start editing the cell
        putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
        setColumnModel(columnModel);
        setDragEnabled(true);
        setTransferHandler(new TransferHandler() {

            @Override protected Transferable createTransferable(JComponent c) {
                AbstractEntityTable t = (AbstractEntityTable) c;
                Collection<AnnotatedEntity> entities = t.getSelection()
                                                        .getEntities();
                return new TransferableEntity(DefaultReconstructionManager
                                                      .getInstance()
                                                      .active(),
                                              entities);
            }

            @Override public int getSourceActions(JComponent c) {
                return COPY;
            }
        });
    }

    @Override
    public AbstractEntityTableModel getModel() {
        return (AbstractEntityTableModel) super.getModel();
    }

    /** Update the table model with the current */
    public boolean update() {
        updating = true;
        Collection<AnnotatedEntity> entities = selection.getEntities();
        boolean updated = getModel().update();
        select(entities);
        updating = false;
        return updated;
    }

    void select(int[] rows) {
        clearSelection();
        for (int[] r : intervals(rows)) {
            if (r[1] < getRowCount()) {
                addRowSelectionInterval(r[0], r[1]);
            }
        }
    }

    static int[][] intervals(int[] rows) {
        if (rows.length == 0)
            return new int[0][];
        if (rows.length == 1)
            return new int[][]{{rows[0], rows[0]}};

        List<int[]> intervals = new ArrayList<int[]>();
        int last = rows.length - 1;
        for (int i = 0; i < rows.length; i++) {
            int j = i, k = i;
            for (j = i + 1; j < rows.length && rows[j] == rows[j - 1] + 1; j++) {
                k = j;
            }
            intervals.add(new int[]{rows[i], rows[k]});
            i = k;
        }
        return intervals.toArray(new int[intervals.size()][]);
    }

    /** @inheritDoc */
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

    public int indexInView(AnnotatedEntity e) {
        int i = getModel().indexOf(e);
        return i < 0 ? i : convertRowIndexToView(i);
    }

    public void select(Collection<AnnotatedEntity> entities) {
        getSelectionModel().setValueIsAdjusting(true);
        int[] rows = new int[entities.size()];
        int i = 0;
        for (AnnotatedEntity e : entities) {
            int index = indexInView(e);
            if (index >= 0)
                rows[i++] = index;
        }
        if (i < rows.length)
            rows = Arrays.copyOf(rows, i);
        Arrays.sort(rows);
        select(rows);
        scrollToSelected();
        getSelectionModel().setValueIsAdjusting(false);
    }

    private void scrollToSelected() {
        int selected = getSelectedRow();
        if (selected < 0)
            return;

        Container parent = getParent();
        if (parent != null) {
            int y = getTableHeader()
                    .getHeight() + (getRowHeight() * selected) - ((int) parent
                    .getHeight()
                    / 2);
            scrollRectToVisible(new Rectangle(0, y,
                                              parent.getWidth(),
                                              parent.getHeight()));
        }
    }

    /**
     * Sets a single selection in the table
     *
     * @param selectionManager
     */
    public boolean setSelection(EntityCollection selectionManager) {
        List<AnnotatedEntity> entities = new ArrayList<AnnotatedEntity>(selectionManager.getEntities());
        getSelectionModel().setValueIsAdjusting(true);
        select(entities);
        scrollToSelected();
        return true;
    }

    public void clear() {
        getSelectionModel().clearSelection();
        getModel().clear();
    }
}
