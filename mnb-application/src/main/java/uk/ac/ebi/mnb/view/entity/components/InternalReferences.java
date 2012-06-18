/**
 * InternalReferences.java
 *
 * 2011.12.14
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
package uk.ac.ebi.mnb.view.entity.components;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.ui.render.table.ListLinkRenderer;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *          InternalReferences - 2011.12.14 <br>
 *          Class displays internal references of a model
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class InternalReferences
        extends JList {

    private static final Logger LOGGER = Logger.getLogger(InternalReferences.class);

    public InternalReferences() {

        this(new InternalReferenceModel());

        setVisibleRowCount(8);
        setCellRenderer(new ListLinkRenderer());


        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index == -1) {
                    return;
                }
                if (getCellBounds(index, index).contains(e.getPoint())) {
                    setSelectedIndex(index);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    removeSelectionInterval(0, getModel().getSize());
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        addMouseListener(new MouseAdapter() {

            // need to monitor when we leave (mouseMoved no longer inside JList)
            @Override
            public void mouseExited(MouseEvent e) {
                removeSelectionInterval(0, getModel().getSize());
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int index = locationToIndex(e.getPoint());

                MainController mainController = MainView.getInstance();
                ViewController viewController = mainController.getViewController();
                EntityCollection manager = viewController.getSelection();
                manager.clear().add((AnnotatedEntity) getModel().getElementAt(index));
                viewController.setSelection(manager);
            }
        });


    }

    public void clear() {
        getModel().clear();
    }

    public InternalReferences(ListModel model) {
        super(model);
    }

    @Override
    public InternalReferenceModel getModel() {
        return (InternalReferenceModel) super.getModel();
    }
}
