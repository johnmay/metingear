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
package uk.ac.ebi.mnb.view.entity;

import com.explodingpixels.macwidgets.plaf.EmphasizedLabelUI;
import com.jgoodies.forms.factories.Borders;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.BorderlessScrollPane;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.theme.ThemeManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.interfaces.EntityTable;
import uk.ac.ebi.mnb.interfaces.EntityView;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

/**
 * EntityView – 2011.09.06 <br> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class AbstractEntityView
        extends JSplitPane
        implements EntityView {

    private static final Logger LOGGER = Logger.getLogger(AbstractEntityView.class);
    private final AbstractEntityTable table;
    private final AbstractEntityInspector inspector;
    private JLabel label = LabelFactory.emptyLabel(); // avoid null pointers

    private final ListSelectionListener listener = new ListSelectionListener() {

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                label.setText(AbstractEntityView.this.table
                                      .getSelectedRowCount() + " of " + AbstractEntityView.this
                        .table
                        .getRowCount() + " " + getName() + " selected");
                label.repaint();
                // set updater for context
                MainView.getInstance().getJMenuBar().updateContext();
            }
        }
    };


    public AbstractEntityView(String name,
                              final AbstractEntityTable table,
                              final AbstractEntityInspector inspector) {

        this.table = table;
        this.inspector = inspector;
        setName(name);
        setOrientation(JSplitPane.VERTICAL_SPLIT);
        setDividerSize(10);
        setBackground(ThemeManager.getInstance().getTheme().getBackground());
        JScrollPane tablePane = new BorderlessScrollPane(this.table);
        add(tablePane, JSplitPane.TOP);
        add(this.inspector, JSplitPane.BOTTOM);
        setBorders();
        inspector.setTable(table);
        setDividerLocation(350);


        // action listener changes text on the bottom-bar
        table.addListSelectionListener(listener);

        // update inspector on selection and data change
        table.addListSelectionListener(inspector);
        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                inspector.update();
            }
        });

    }


    private void setBorders() {
        // simple narrow border on top and bottom
        ((BasicSplitPaneUI) getUI()).getDivider().setBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0xa5a5a5)));
        setBorder(Borders.EMPTY_BORDER);
    }


    public void clear() {
        inspector.clear();
        table.clear();
        repaint();
    }


    /**
     * Accessor to the table component of the split-pane
     *
     * @return Class extending EntityTable
     */
    public EntityTable getTable() {
        return table;
    }


    public boolean update() {
        boolean changed = table.update();
        changed = inspector.update() || changed;
        repaint();
        return changed;
    }


    /**
     * @inheritDoc
     */
    @Override
    public boolean update(EntityCollection selection) {
        boolean updated = table.update(selection);
        return inspector.update(selection) || updated;
    }


    /**
     * Accessor to the inspector component of the split-pane
     *
     * @return Class extending the EntityInspector
     */
    public AbstractEntityInspector getInspector() {
        return inspector;
    }


    @Override
    public EntityCollection getSelection() {
        return inspector.getSelection();
    }


    @Override
    public boolean setSelection(EntityCollection selection) {
        return table.setSelection(selection);
    }


    void setBottomBarLabel(JLabel label) {
        this.label = label;
        label.setUI(new EmphasizedLabelUI());
    }
}
