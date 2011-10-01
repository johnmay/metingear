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

import javax.swing.JTable;

/**
 * MatrixView.java
 * Matrix view provides a view of the reaction matrix containing the metabolites and reactions
 *
 * @author John May
 * @date May 15, 2011
 */
public class MatrixView 
    extends JTable {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( MatrixView.class );

    public MatrixView(MatrixModel model) {
        super(model);        
    }

    @Override
    public MatrixModel getModel() {
        return (MatrixModel) super.getModel();
    }
    
}
