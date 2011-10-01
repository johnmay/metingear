
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
import java.util.Collection;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import uk.ac.ebi.mnb.view.labels.Label;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.core.ApplicationPreferences;
import uk.ac.ebi.mnb.view.labels.AltLabel;


/**
 *          BasicAnnotationCellRenderer – 2011.09.29 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class BasicAnnotationCellRenderer extends DefaultTableCellRenderer {

    private static final Logger LOGGER = Logger.getLogger(BasicAnnotationCellRenderer.class);
    private JLabel emptyLabel = new Label("");


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row,
                                                   int column) {

        this.setFont(ApplicationPreferences.getInstance().getTheme().getBodyFont());

        if( value instanceof Collection ) {

            Collection collection = (Collection) value;
            if( collection.isEmpty() ) {
                this.setText("-");
            } else {
                this.setText(StringUtils.join(collection, ", "));
            }

            if( isSelected ) {
                this.setBackground(table.getSelectionBackground());
            } else {
                this.setBackground(table.getBackground());
            }

            //   return label;

        }

        return this;



    }


}

