/**
 * CrossReferenceCellEditor.java
 *
 * 2011.10.07
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
package uk.ac.ebi.mnb.editors;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.ui.render.table.AnnotationCellRenderer;
import uk.ac.ebi.mnb.dialog.popup.CrossReferenceEditorDialog;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;

/**
 * @name    CrossReferenceCellEditor - 2011.10.07 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class CrossReferenceCellEditor
        extends AnnotationCellRenderer
        implements TableCellEditor {

    private static final Logger LOGGER = Logger.getLogger(CrossReferenceCellEditor.class);
    private static CrossReferenceEditorDialog xrefEditor = new CrossReferenceEditorDialog(MainView.getInstance());
    private ChangeEvent event = new ChangeEvent(this);


    public CrossReferenceCellEditor() {
        //  setFont(Settings.getInstance().getTheme().getBodyFont());
        xrefEditor.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentHidden(ComponentEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        Collection refs = (Collection) value;
        xrefEditor.setup(refs);
        xrefEditor.pack();
        xrefEditor.setOnMouse();
        xrefEditor.setVisible(true);
        return super.getTableCellRendererComponent(table, value, isSelected, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        return xrefEditor.getCrossReferences();
    }

    protected void fireEditingStopped() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            ((CellEditorListener) listeners[i + 1]).editingStopped(event);
        }
    }

    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent) anEvent).getClickCount() >= 2;
        }
        return false;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        return true;
    }

    @Override
    public void cancelCellEditing() {
        fireEditingStopped();
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }
}
