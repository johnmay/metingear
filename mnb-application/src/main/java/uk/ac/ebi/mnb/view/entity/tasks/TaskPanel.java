/**
 * MetabolitePanel.java
 *
 * 2011.09.30
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
package uk.ac.ebi.mnb.view.entity.tasks;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import java.util.Collection;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.io.external.RunnableTask;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.chemet.render.ViewUtilities;
import uk.ac.ebi.mnb.view.entity.AbstractEntityPanel;

/**
 *          MetabolitePanel – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class TaskPanel
        extends AbstractEntityPanel {

    private static final Logger LOGGER = Logger.getLogger(TaskPanel.class);
    private RunnableTask entity;
    private JLabel formula;
    private JTextArea command = new JTextArea();
    private CellConstraints cc = new CellConstraints();

    public TaskPanel() {
        super("Task", new AnnotationRenderer());
    }

    /**
     * Updates the command text
     * @return
     */
    @Override
    public boolean update() {

        command.setText(entity.getCommand());
        command.setFont(ViewUtilities.COURIER_NEW_PLAIN_11);
        command.setForeground(Settings.getInstance().getTheme().getForeground());
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
