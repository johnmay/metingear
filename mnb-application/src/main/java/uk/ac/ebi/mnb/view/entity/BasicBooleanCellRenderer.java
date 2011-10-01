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
package uk.ac.ebi.mnb.view.entity;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.core.ApplicationPreferences;
import uk.ac.ebi.mnb.view.labels.AltLabel;

/**
 *          BasicAnnotationCellRenderer – 2011.09.29 <br>
 *          Renders boolean values as Yes and No (personal preference)
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class BasicBooleanCellRenderer extends DefaultTableCellRenderer {
    
    private static final Logger LOGGER = Logger.getLogger(BasicBooleanCellRenderer.class);
    private JLabel emptyLabel = new JLabel("");
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row,
            int column) {
        
        this.setText((Boolean) value ? "Yes" : "No");
        this.setFont(ApplicationPreferences.getInstance().getTheme().getBodyFont());
        
        return this;
        
    }
}
