/**
 * ComboRenderer.java
 *
 * 2011.08.04
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
package uk.ac.ebi.mnb.view.table;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.apache.log4j.Logger;

/**
 * @name    ComboRenderer
 * @date    2011.08.04
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class ComboRenderer extends JComboBox implements TableCellRenderer {

    private static final Logger LOGGER = Logger.getLogger( ComboRenderer.class );

    public ComboRenderer( String[] items ) {
        for ( int i = 0; i < items.length; i++ ) {
            addItem( items[i] );
        }
    }

    public Component getTableCellRendererComponent( JTable table ,
                                                    Object value , boolean isSelected , boolean hasFocus , int row ,
                                                    int column ) {
        setSelectedItem( value );
        return this;
    }
}
