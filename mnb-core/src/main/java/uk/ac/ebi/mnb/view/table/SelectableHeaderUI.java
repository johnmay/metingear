/**
 * SelectableHeaderUI.java
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
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableColumnModel;

/**
 * @name    SelectableHeaderUI
 * @date    2011.08.04
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class SelectableHeaderUI extends BasicTableHeaderUI {

    protected MouseInputListener createMouseInputListener() {
        return new MouseInputHandler( ( SelectableHeader ) header );
    }

    public class MouseInputHandler extends BasicTableHeaderUI.MouseInputHandler {

        private Component dispatchComponent;
        protected SelectableHeader header;

        public MouseInputHandler( SelectableHeader header ) {
            this.header = header;
        }

        private void setDispatchComponent( MouseEvent e ) {
            Component editorComponent = header.getEditorComponent();
            Point p = e.getPoint();
            Point p2 = SwingUtilities.convertPoint( header , p , editorComponent );
            dispatchComponent = SwingUtilities.getDeepestComponentAt(
                    editorComponent , p2.x , p2.y );
        }

        private boolean repostEvent( MouseEvent e ) {
            if ( dispatchComponent == null ) {
                return false;
            }
            MouseEvent e2 = SwingUtilities.convertMouseEvent( header , e ,
                                                              dispatchComponent );
            dispatchComponent.dispatchEvent( e2 );
            return true;
        }

        public void mousePressed( MouseEvent e ) {
            if ( !SwingUtilities.isLeftMouseButton( e ) ) {
                return;
            }
            super.mousePressed( e );

            if ( header.getResizingColumn() == null ) {
                Point p = e.getPoint();
                TableColumnModel columnModel = header.getColumnModel();
                int index = columnModel.getColumnIndexAtX( p.x );
                if ( index != -1 ) {
                    if ( header.editCellAt( index , e ) ) {
                        setDispatchComponent( e );
                        repostEvent( e );
                    }
                }
            }
        }

        public void mouseReleased( MouseEvent e ) {
            super.mouseReleased( e );
            if ( !SwingUtilities.isLeftMouseButton( e ) ) {
                return;
            }
            repostEvent( e );
            dispatchComponent = null;
        }
    }
}