/**
 * SearchTable.java
 *
 * 2011.09.29
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
package uk.ac.ebi.mnb.view.entity.search;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTable;
import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.main.MainView;

/**
 *          SearchTable – 2011.09.29 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class SearchTable extends AbstractEntityTable {

    private static final Logger LOGGER = Logger.getLogger(SearchTable.class);
    private DoubleClickListener listener;

    public SearchTable() {
        super(new SearchTableModel());
        listener = new DoubleClickListener(this);
        addMouseListener(listener);

    }

    private class DoubleClickListener extends MouseAdapter {

        private AbstractEntityTable table;

        public DoubleClickListener(AbstractEntityTable table) {
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                MainView.getInstance().getViewController().setSelection(table.getSelection());
            }
        }
    }
}
