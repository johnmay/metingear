
/**
 * EditMenu.java
 *
 * 2011.09.26
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
package uk.ac.ebi.mnb.menu;

import javax.swing.JComponent;
import uk.ac.ebi.mnb.menu.reconciliation.AddCrossReference;
import uk.ac.ebi.mnb.menu.reconciliation.DownloadStructures;
import uk.ac.ebi.mnb.view.ViewUtils;
import org.apache.log4j.Logger;


/**
 *          EditMenu – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class EditMenu extends ClearMenu {

    private static final Logger LOGGER = Logger.getLogger(EditMenu.class);
    private JComponent items[] = new JComponent[2];


    public EditMenu() {
        super("Edit");
        setBackground(ViewUtils.CLEAR_COLOUR);
        setBorderPainted(false);

        int index = 0;
        items[index++] = new DynamicMenuItem(new AddCrossReference());
        items[index++] = new DynamicMenuItem(new DownloadStructures());

        for( JComponent component : items ) {
            add(component);
            if( component instanceof DynamicMenuItem ) {
                ((DynamicMenuItem) component).reloadEnabled();
            }
        }
    }


}

