/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
package uk.ac.ebi.mnb.view.entity.components;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.MolecularFormula;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.tool.domain.StructuralValidity;
import uk.ac.ebi.mdk.ui.edit.annotation.AnnotationEditorFactory;
import uk.ac.ebi.mdk.ui.render.table.*;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;


/**
 * AnnotationTable - 2011.12.13 <br>
 * Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
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
        model.getColumn(1).setCellEditor(AnnotationEditorFactory.getInstance().getTableCellEditor());
        model.getColumn(2).setCellRenderer(deleteButtonRenderer);
        model.getColumn(2).setPreferredWidth(32);

        CONTROL_RENDERER.setRenderer(Object.class, new ActionButtonCellRenderer(SwingConstants.CENTER));
        CONTROL_RENDERER.setRenderer(Action.class, new ActionButtonCellRenderer(SwingConstants.CENTER));
        CONTROL_RENDERER.setRenderer(StructuralValidity.class, new StructuralValidityRenderer());
        model.getColumn(3).setCellRenderer(CONTROL_RENDERER);
        model.getColumn(3).setPreferredWidth(64);

        addMouseListener(new ActionClickForwarder(this));

        setBackground(Color.WHITE);


    }


    public void clear() {
        getModel().clear();
    }


    public void setEditable(boolean editable) {
        this.editable = editable;
        getModel().setEditable(editable);
        deleteButtonRenderer.setVisible(editable);
        if (!editable) {
            AnnotationEditorFactory.getInstance().getTableCellEditor().cancelCellEditing();
        }
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
                } else if (!editable && value instanceof CrossReference) {
                    openCrossReferenceLink((CrossReference) value);
                }
            }
        }
    }

    private void openCrossReferenceLink(CrossReference xref) {
        try {
            Desktop.getDesktop().browse(xref.getIdentifier().getURL().toURI());
        } catch (IOException ex) {
            LOGGER.error("IO Exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            LOGGER.error("Syntax error in cross-reference: " + ex.getMessage());
        }
    }

    public void store() {
        AnnotationEditorFactory.getInstance().getTableCellEditor().stopCellEditing();
    }

}
