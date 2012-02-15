/**
 * AnnotationTable.java
 *
 * 2011.12.13
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.chemical.AtomContainerAnnotation;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.chemet.render.ClassBasedTableCellDDR;
import uk.ac.ebi.chemet.render.PooledClassBasedTableCellDRR;
import uk.ac.ebi.chemet.render.table.renderers.*;
import uk.ac.ebi.core.tools.StructuralValidity;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.Annotation;


/**
 *          AnnotationTable - 2011.12.13 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AnnotationTable
        extends JTable {

    private static final Logger LOGGER = Logger.getLogger(AnnotationTable.class);

    private boolean editable = false;

    private ActionButtonCellRenderer deleteButtonRenderer = new ActionButtonCellRenderer(SwingConstants.CENTER);

    private ClassBasedTableCellDDR ANNOTATION_RENDERER = new ClassBasedTableCellDDR();

    private ClassBasedTableCellDDR CONTROL_RENDERER = new ClassBasedTableCellDDR();


    public AnnotationTable() {
        super(new AnnotationTableModel());

        TableColumnModel model = getColumnModel();
        model.getColumn(0).setCellRenderer(new DescriptorRenderer());
        model.getColumn(0).setPreferredWidth(150);

        ANNOTATION_RENDERER.setRenderer(Annotation.class, new AnnotationCellRenderer());
        ANNOTATION_RENDERER.setRenderer(AtomContainerAnnotation.class, new ChemicalStructureRenderer());
        ANNOTATION_RENDERER.setRenderer(MolecularFormula.class, new FormulaCellRender());
        ANNOTATION_RENDERER.setRenderer(Action.class, new ActionButtonCellRenderer(SwingConstants.LEFT));
        
        setCellSelectionEnabled(false);
        setIntercellSpacing(new Dimension(4, 4));
        model.getColumn(1).setCellRenderer(ANNOTATION_RENDERER);

        model.getColumn(1).setPreferredWidth(128);
        model.getColumn(2).setCellRenderer(deleteButtonRenderer);
        model.getColumn(2).setPreferredWidth(32);

        CONTROL_RENDERER.setRenderer(Object.class, new ActionButtonCellRenderer(SwingConstants.CENTER));
        CONTROL_RENDERER.setRenderer(Action.class, new ActionButtonCellRenderer(SwingConstants.CENTER));
        CONTROL_RENDERER.setRenderer(StructuralValidity.class, new StructuralValidityRenderer());
        model.getColumn(3).setCellRenderer(CONTROL_RENDERER);
        model.getColumn(3).setPreferredWidth(64);

        addMouseListener(new ActionClickForwarder(this));
    }


    public void clear() {
        getModel().clear();
    }


    public void setEditable(boolean editable) {
        this.editable = editable;
        deleteButtonRenderer.setVisible(editable);
    }


    @Override
    public AnnotationTableModel getModel() {
        return (AnnotationTableModel) super.getModel();
    }


    public void setEntity(AnnotatedEntity entity) {
        getModel().setEntity(entity);
    }


    /**
     * Forwards click events on table to appropiate action
     */
    private class ActionClickForwarder
            extends MouseAdapter {

        private JTable table;


        public ActionClickForwarder(JTable table) {
            this.table = table;
        }


        @Override
        public void mouseClicked(MouseEvent me) {
            int column = table.getColumnModel().getColumnIndexAtX(me.getX());
            int row = table.rowAtPoint(me.getPoint());

            if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof Action) {
                    ((Action) value).actionPerformed(new ActionEvent(me.getSource(), me.getID(), me.paramString()));
                }
            }
        }
    }
}
