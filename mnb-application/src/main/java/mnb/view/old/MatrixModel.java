/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package mnb.view.old;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import uk.ac.ebi.metabolomes.core.reaction.matrix.InChIStoichiometricMatrix;

/**
 * MatrixModel.java
 *
 *
 * @author johnmay
 * @date May 15, 2011
 */
public class MatrixModel
        extends InChIStoichiometricMatrix
        implements TableModel {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( MatrixModel.class );

    @Override
    public int getRowCount() {
        return super.getReactionCount();
    }

    @Override
    public int getColumnCount() {
        return super.getMoleculeCount();
    }

    @Override
    public String getColumnName( int columnIndex ) {
        return super.getMolecule( columnIndex ).getName();
    }

    @Override
    public Class<?> getColumnClass( int columnIndex ) {
        return Object.class;
    }

    @Override
    public boolean isCellEditable( int rowIndex , int columnIndex ) {
        return false;
    }

    @Override
    public Object getValueAt( int rowIndex , int columnIndex ) {
        return super.get( rowIndex , columnIndex );
    }

    @Override
    public void setValueAt( Object aValue , int rowIndex , int columnIndex ) {
        // do nothing
    }

    @Override
    public void addTableModelListener( TableModelListener l ) {
        // do nothing
    }

    @Override
    public void removeTableModelListener( TableModelListener l ) {
        // do nothing
    }
}
