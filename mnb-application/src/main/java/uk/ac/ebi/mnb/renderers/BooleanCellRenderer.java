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

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.settings.Settings;

/**
 *          BasicAnnotationCellRenderer – 2011.09.29 <br>
 *          Renders boolean values as Yes and No (personal preference)
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class BooleanCellRenderer extends DefaultTableCellRenderer {

    private static final Logger LOGGER = Logger.getLogger(BooleanCellRenderer.class);
    private JLabel yes = new JLabel("Yes");
    private JLabel no = new JLabel("No");

    public BooleanCellRenderer() {
        yes.setFont(Settings.getInstance().getTheme().getBodyFont());
        no.setFont(Settings.getInstance().getTheme().getBodyFont());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row,
            int column) {

        JLabel label = (Boolean) value ? yes : no;

        label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        return label;

    }
}
