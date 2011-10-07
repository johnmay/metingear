/**
 * QuickViewTable.java
 *
 * 2011.10.04
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
package uk.ac.ebi.mnb.importer.xls.wizzard;

import com.explodingpixels.macwidgets.MacFontUtils;
import com.explodingpixels.macwidgets.plaf.ITunesTableUI;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import mnb.io.tabular.util.ExcelUtilities;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.parser.ExcelHelper;
import uk.ac.ebi.mnb.view.ViewUtils;

/**
 * @name    QuickViewTable - 2011.10.04 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class SelectionTable extends JTable {

    private static final Logger LOGGER = Logger.getLogger(SelectionTable.class);
    private int start = 0;
    private int end = super.getRowCount();
    private ExcelHelper helper;

    public SelectionTable(ExcelHelper helper) {

        this.helper = helper;
//        super(helper.getSheetData(i), new String[]{"A", "B", "C", "D", "E", "F", "G"});

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                this.setText(value.toString());
                this.setFont(MacFontUtils.ITUNES_FONT);
                if (isSelected) {
                    this.setForeground(row >= start && row <= end ? table.getSelectionForeground() : ViewUtils.shade(table.getSelectionForeground(), 0.6f));
                    this.setBackground(table.getSelectionBackground());
                } else {
                    this.setForeground(row >= start && row <= end ? table.getForeground() : ViewUtils.shade(table.getForeground(), 0.6f));
                    this.setBackground(table.getBackground());
                }
                return this;
            }
        });
    }

    public void setSheet(int index) {
        String[][] data = helper.getSheetData(index);
        super.setModel(new DefaultTableModel(data, ExcelUtilities.buildHeaders(0, data[0].length)));
        end = super.getRowCount();
    }

    public void setStart(int start) {
        if (start > 0 && start < super.getRowCount()) {
            this.start = start - 1;
        }
    }

    public void setEnd(int end) {
        this.end = end - 1;
    }

    @Override
    public Object getValueAt(int i, int i1) {
        return super.getValueAt(i, i1); // lowest value of start is 1
    }

    /**
     * Sets the header name
     * @param index
     * @param name
     */
    public void setHeader(int index, String name) {
        getColumnModel().getColumn(index - 1).setHeaderValue(name);
    }
}
