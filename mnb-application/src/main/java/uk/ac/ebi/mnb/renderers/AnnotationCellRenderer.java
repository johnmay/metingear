/**
 * BasicAnnotationCellRenderer.java
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
package uk.ac.ebi.mnb.renderers;

import com.google.common.base.Joiner;
import java.awt.Component;
import java.util.Collection;
import javax.swing.JTable;

/**
 *          AnnotationCellRenderer – 2011.09.29 <br>
 *          A simple annotation cell renderer that joins the toString() values of a collection
 *          of annotations using a comma and space ', '.
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AnnotationCellRenderer extends DefaultRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row,
            int column) {

        Collection collection = (Collection) value;
        this.setText(Joiner.on(", ").join(collection));

        this.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        this.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        return this;

    }
}
