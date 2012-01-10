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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.chemical.ChemicalStructure;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.chemet.render.PooledClassBasedTableCellDRR;
import uk.ac.ebi.chemet.render.table.renderers.ActionButtonCellRenderer;
import uk.ac.ebi.chemet.render.table.renderers.AnnotationCellRenderer;
import uk.ac.ebi.chemet.render.table.renderers.AnnotationDescriptionRenderer;
import uk.ac.ebi.chemet.render.table.renderers.ChemicalStructureRenderer;
import uk.ac.ebi.chemet.render.table.renderers.DefaultRenderer;
import uk.ac.ebi.chemet.render.table.renderers.FormulaCellRender;
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
    private ActionButtonCellRenderer deleteButtonRenderer = new ActionButtonCellRenderer(false);
    private PooledClassBasedTableCellDRR ANNOTATION_RENDERER = new PooledClassBasedTableCellDRR();

    public AnnotationTable() {
        super(new AnnotationTableModel());

        TableColumnModel model = getColumnModel();
        model.getColumn(0).setCellRenderer(new AnnotationDescriptionRenderer());
        model.getColumn(0).setPreferredWidth(150);

        ANNOTATION_RENDERER.setRenderer(Annotation.class, new AnnotationCellRenderer());
        ANNOTATION_RENDERER.setRenderer(ChemicalStructure.class, new ChemicalStructureRenderer());
        ANNOTATION_RENDERER.setRenderer(MolecularFormula.class, new FormulaCellRender());

        setIntercellSpacing(new Dimension(10, 0));
        model.getColumn(1).setCellRenderer(ANNOTATION_RENDERER);

        model.getColumn(1).setPreferredWidth(128);
        model.getColumn(2).setCellRenderer(deleteButtonRenderer);
        model.getColumn(2).setPreferredWidth(16);
        model.getColumn(3).setCellRenderer(new ActionButtonCellRenderer(false));
        model.getColumn(2).setPreferredWidth(32);

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

        // check-in previous annotations (freeing up object pool)
        if (getModel().getEntity() != null) {
            for (Annotation annotation : getModel().getEntity().getAnnotations()) {
                ANNOTATION_RENDERER.checkIn(annotation);
            }
        }

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