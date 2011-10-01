/**
 * SelectableHeaderTableColumn.java
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

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 * @name    SelectableHeaderTableColumn
 * @date    2011.08.04
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class SelectableHeaderTableColumn extends TableColumn {

  protected TableCellEditor headerEditor;

  protected boolean isHeaderEditable;

  public SelectableHeaderTableColumn() {
    setHeaderEditor(createDefaultHeaderEditor());
    isHeaderEditable = true;
  }

  public void setHeaderEditor(TableCellEditor headerEditor) {
    this.headerEditor = headerEditor;
  }

  public TableCellEditor getHeaderEditor() {
    return headerEditor;
  }

  public void setHeaderEditable(boolean isEditable) {
    isHeaderEditable = isEditable;
  }

  public boolean isHeaderEditable() {
    return isHeaderEditable;
  }

  public void copyValues(TableColumn base) {
    modelIndex = base.getModelIndex();
    identifier = base.getIdentifier();
    width = base.getWidth();
    minWidth = base.getMinWidth();
    setPreferredWidth(base.getPreferredWidth());
    maxWidth = base.getMaxWidth();
    headerRenderer = base.getHeaderRenderer();
    headerValue = base.getHeaderValue();
    cellRenderer = base.getCellRenderer();
    cellEditor = base.getCellEditor();
    isResizable = base.getResizable();
  }

  protected TableCellEditor createDefaultHeaderEditor() {
    return new DefaultCellEditor(new JTextField());
  }

}