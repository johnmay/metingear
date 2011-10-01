
/**
 * EntityTable.java
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

import com.explodingpixels.macwidgets.plaf.ITunesTableUI;
import com.explodingpixels.widgets.TableUtils;
import java.awt.Container;
import java.awt.Rectangle;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import uk.ac.ebi.mnb.view.ViewUtils;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.annotation.crossreference.Classification;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.core.AnnotatedEntity;


/**
 *          EntityTable – 2011.09.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class EntityTable extends JTable {

    private static final Logger LOGGER = Logger.getLogger(EntityTable.class);


    public EntityTable(EntityTableModel model) {
        super(model);
        setUI(new ITunesTableUI());
        setAutoscrolls(true);
        setFont(ViewUtils.DEFAULT_BODY_FONT);
        setAutoCreateRowSorter(true);
        TableUtils.makeSortable(this, new TableUtils.SortDelegate() {

            public void sort(int columnModelIndex, TableUtils.SortDirection sortDirection) {
                // no implementation.
            }


        });
        setColumnModel(columnModel);
    }


    @Override
    public EntityTableModel getModel() {
        return (EntityTableModel) super.getModel();
    }


    /**
     *
     * Update the table model with the current
     *
     */
    public boolean update() {
        return getModel().update();
    }


    /**
     *
     * Sets a single selection in the table
     * 
     * @param component
     *
     */
    public void setSelected(AnnotatedEntity component) {

        int index = convertRowIndexToView(getModel().indexOf(component));

        // could check for -1 but if something is not in the table then it should not be
        // selectable out of principle

        removeRowSelectionInterval(0, getModel().getRowCount() - 1);
        addRowSelectionInterval(index, index);

        Container parent = getParent();

        if( parent != null ) {

            int y = getTableHeader().getHeight() + (getRowHeight() * index) - ((int) parent.
                                                                               getHeight() /
                                                                               2);

            scrollRectToVisible(new Rectangle(0, y,
                                              parent.getWidth(),
                                              parent.getHeight()));

        }

    }


}

