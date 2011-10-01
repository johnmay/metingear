/**
 * MetaboliteTable.java
 *
 * 2011.09.05
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
package uk.ac.ebi.mnb.view.entity.metabolite;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import uk.ac.ebi.mnb.view.entity.BasicAnnotationCellRenderer;
import uk.ac.ebi.mnb.view.entity.BasicBooleanCellRenderer;
import uk.ac.ebi.mnb.view.entity.EntityTable;
import mnb.view.old.CachedMoleculeRenderer;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import uk.ac.ebi.annotation.chemical.ChemicalStructure;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.annotation.crossreference.CrossReference;

/**
 *          MetaboliteTable – 2011.09.05 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetaboliteTable extends EntityTable {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteTable.class);

    public MetaboliteTable() {
        super(new MetaboliteTableModel());
        //       setDefaultRenderer(ChemicalStructure.class,
        //                          new ChemStructureRenderer());
        BasicAnnotationCellRenderer annotationRenderer = new BasicAnnotationCellRenderer();
        BasicBooleanCellRenderer booleanRenderer = new BasicBooleanCellRenderer();
        setDefaultRenderer(Boolean.class,
                booleanRenderer);
        setDefaultRenderer(CrossReference.class,
                annotationRenderer);
        setDefaultRenderer(MolecularFormula.class,
                annotationRenderer);

//        setRowHeight(64);// only set when chem structure is to be displayed
    }
    private JLabel nullLabel = new JLabel("-");

    private class ChemStructureRenderer extends DefaultTableCellRenderer {

        private CachedMoleculeRenderer cmr = new CachedMoleculeRenderer();

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JLabel label = nullLabel;
            if (value instanceof Set) {

                Set values = ((Set) value);

                if (values.isEmpty() == false) {
                    ChemicalStructure obj = ((ChemicalStructure) values.iterator().next());
                    IAtomContainer molecule = obj.getMolecule();
                    if (molecule != null) {
                        BufferedImage img = cmr.getImage(molecule, new Rectangle(0, 0, 64,
                                64));
                        label = new JLabel(new ImageIcon(img));
                    } else {
                        label = new JLabel("No Structure");
                    }
                }
            }
            if (isSelected) {
                label.setBackground(getSelectionBackground());
            } else {
                label.setBackground(getBackground());
            }

            return label;

        }
    }
}
