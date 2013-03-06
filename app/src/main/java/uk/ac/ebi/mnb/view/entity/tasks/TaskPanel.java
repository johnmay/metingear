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
package uk.ac.ebi.mnb.view.entity.tasks;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.caf.component.theme.ThemeManager;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.tool.task.RunnableTask;
import uk.ac.ebi.mnb.view.entity.AbstractEntityPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;


/**
 * MetabolitePanel – 2011.09.30 <br>
 * Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class TaskPanel
        extends AbstractEntityPanel {

    private static final Logger LOGGER = Logger.getLogger(TaskPanel.class);

    private RunnableTask entity;

    private JLabel formula;

    private JTextArea command = new JTextArea();

    private CellConstraints cc = new CellConstraints();


    public TaskPanel() {
        super("Task");
    }


    /**
     * Updates the command text
     *
     * @return
     */
    @Override
    public boolean update() {

        command.setText(entity.getCommand());
        command.setFont(new Font("Courier New", Font.PLAIN, 10));
        command.setForeground(ThemeManager.getInstance().getTheme().getForeground());
        command.setEditable(false);
        command.setLineWrap(true);

        return super.update();

    }


    @Override
    public boolean setEntity(AnnotatedEntity entity) {
        this.entity = (RunnableTask) entity;
        return super.setEntity(entity);
    }


    @Override
    public JPanel getBasicPanel() {
        JPanel panel = super.getBasicPanel();
        FormLayout layout = (FormLayout) panel.getLayout();
        layout.appendRow(new RowSpec(Sizes.PREFERRED));
        panel.add(command, cc.xyw(1, layout.getRowCount(), 5));
        return panel;
    }


    /**
     * Returns the specific information panel
     */
    public JPanel getSynopsis() {

        JPanel panel = PanelFactory.createInfoPanel();
        panel.add(LabelFactory.newLabel("No synopsis"));
        return panel;

    }


    @Override
    public Collection<? extends AnnotatedEntity> getReferences() {
        return entity.getEntities();
    }
}
