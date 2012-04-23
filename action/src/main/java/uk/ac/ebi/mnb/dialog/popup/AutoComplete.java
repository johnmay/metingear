/**
 * Autocomplete.java
 *
 * 2011.12.05
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
package uk.ac.ebi.mnb.dialog.popup;

import java.util.Collection;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import uk.ac.ebi.chemet.render.ViewUtilities;
import uk.ac.ebi.visualisation.molecule.access.EntityValueAccessor;

/**
 *          Autocomplete - 2011.12.05 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AutoComplete extends JDialog {

    private static final Logger LOGGER = Logger.getLogger(AutoComplete.class);
    private JList list;
    private DefaultListModel model;
    private JScrollPane pane;

    public AutoComplete(JDialog dialog) {
        super(dialog, JDialog.ModalityType.MODELESS);

        setFocusable(false);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setFocusableWindowState(false);

        model = new DefaultListModel();
        list = new JList(model);

        list.setVisibleRowCount(6);

        list.setFont(ViewUtilities.DEFAULT_BODY_FONT);

        pane = new BorderlessScrollPane(list, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);


        add(pane);

    }

    public void setItems(Collection<AnnotatedEntity> entities,
                         EntityValueAccessor accessor) {

        model.clear();

        for (AnnotatedEntity entity : entities) {
            model.addElement(accessor.getValue(entity));
        }

    }

    /**
     * Clear the list model
     */
    public void clear() {
        model.clear();
    }

    /**
     * Moves the selection to the next item (i.e. down,  in a single column list)
     */
    public void selectNext() {
        int index = list.getSelectedIndex() > 0 ? list.getSelectedIndex() - 1 : list.getSelectedIndex();
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }

    /**
     * Moves the selection to the previous item (i.e. up, in a single column list)
     */
    public void selectPrevious() {
        int index = list.getSelectedIndex() < model.getSize() ? list.getSelectedIndex() + 1 : list.getSelectedIndex();
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }

    /**
     *
     * Returns the currently selected item. If not item has been selected the first item
     * is returned
     *
     * @return
     */
    public String getSelectedItem() {
        return (String) (list.getSelectedIndex() == -1
                         ? model.isEmpty() ? " " : model.get(0) // return empty string or fist item
                         : model.get(list.getSelectedIndex())); // return selected item
    }
}