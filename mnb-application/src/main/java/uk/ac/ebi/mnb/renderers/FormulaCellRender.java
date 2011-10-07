/**
 * FormulaCellRender.java
 *
 * 2011.10.06
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
import java.util.Collection;
import javax.swing.JTable;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.mnb.view.ViewUtils;

/**
 * @name    FormulaCellRender - 2011.10.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class FormulaCellRender extends DefaultRenderer {

    private static final Logger LOGGER = Logger.getLogger(FormulaCellRender.class);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Collection collection = (Collection) value;
        if (collection.size() == 1) {
            this.setText(ViewUtils.htmlWrapper(((MolecularFormula) collection.iterator().next()).toHTML()));
        }

        this.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        this.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        return this;
    }
}
