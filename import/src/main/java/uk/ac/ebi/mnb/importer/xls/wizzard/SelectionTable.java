/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.importer.xls.wizzard;

import com.explodingpixels.macwidgets.MacFontUtils;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import mnb.io.tabular.util.ExcelUtilities;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.utility.ColorUtility;
import uk.ac.ebi.mnb.parser.ExcelHelper;


/**
 * @name QuickViewTable - 2011.10.04 <br> Class description
 *
 * @version $Rev$ : Last Changed $Date: 2011-12-13 16:43:11 +0000 (Tue,
 * 13 Dec 2011) $
 * @author johnmay
 * @author $Author$ (this version)
 */
public class SelectionTable extends JTable {

    private static final Logger LOGGER = Logger.getLogger(SelectionTable.class);

    private int start = 0;

    private int end = super.getRowCount();

    private ExcelHelper helper;


    public SelectionTable(ExcelHelper helper) {

        this.helper = helper;
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                this.setText(value == null ? "null" : value.toString());
                this.setFont(MacFontUtils.ITUNES_FONT);
                if (isSelected) {
                    this.setForeground(row >= start && row <= end ? table.getSelectionForeground() : ColorUtility.shade(table.getSelectionForeground(), 0.6f));
                    this.setBackground(table.getSelectionBackground());
                } else {
                    this.setForeground(row >= start && row <= end ? table.getForeground() : ColorUtility.shade(table.getForeground(), 0.6f));
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
        for (int i = 0; i < getColumnCount(); i++) {
            this.getColumnModel().getColumn(index).setWidth(80);
        }
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
     *
     * @param index
     * @param name
     */
    public void setHeader(int index, String name) {
        if (index > 0) {
            getColumnModel().getColumn(index - 1).setHeaderValue(name);
        }
    }
}
