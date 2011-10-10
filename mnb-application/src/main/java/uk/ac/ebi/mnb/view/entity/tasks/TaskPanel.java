
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

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.io.external.RunnableTask;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
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
    private JTextField generic;


    public TaskPanel() {
        super("Metabolite", new AnnotationRenderer());
    }


    @Override
    public boolean update() {


        // update all fields and labels...

        return super.update();

    }


    @Override
    public boolean setEntity(AnnotatedEntity entity) {
        this.entity = (RunnableTask) entity;
        return super.setEntity(entity);
    }


    /**
     * Returns the specific information panel
     */
    public JPanel getSynopsis() {

        JPanel panel = new JPanel();

        panel.setBackground(Color.LIGHT_GRAY);
        panel.add(new JLabel("Metabolic Specifics"));


        return panel;

    }


    /**
     * Returns the internal reference panel information panel
     */
    public JPanel getInternalReferencePanel() {

        JPanel panel = new JPanel();

        panel.setBackground(Color.DARK_GRAY);
        panel.add(new JLabel("Internal references"));

        return panel;

    }


}

