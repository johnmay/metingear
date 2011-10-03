/**
 * ReconciliationMenu.java
 *
 * 2011.09.30
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

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.core.SelectionMenuItem;
import uk.ac.ebi.mnb.dialog.tools.AutomaticCrossReference;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.menu.reconciliation.AddCrossReference;
import uk.ac.ebi.mnb.menu.reconciliation.DownloadStructures;

/**
 *          ReconciliationMenu – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReconciliationMenu extends JMenu {

    private static final Logger LOGGER = Logger.getLogger(ReconciliationMenu.class);
    private JMenuItem items[] = new JMenuItem[3];

    public ReconciliationMenu() {
        super("Reconciliation");

        MainFrame frame = MainFrame.getInstance();

        items[0] = new DynamicMenuItem(new AddCrossReference());
        items[1] = new SelectionMenuItem(frame,
                                         frame.getViewController(),
                                         AutomaticCrossReference.class);
        items[2] = new DynamicMenuItem(new DownloadStructures());

        for (JMenuItem mnbMenuItem : items) {
            add(mnbMenuItem);
//            mnbMenuItem.reloadEnabled();
        }
    }
}
