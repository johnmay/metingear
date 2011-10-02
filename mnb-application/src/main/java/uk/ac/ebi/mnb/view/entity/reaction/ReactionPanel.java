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
package uk.ac.ebi.mnb.view.entity.reaction;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.ReactionRenderer;
import uk.ac.ebi.mnb.view.entity.EntityPanel;
import uk.ac.ebi.mnb.view.labels.Label;

/**
 *          MetabolitePanel – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReactionPanel
        extends EntityPanel {

    private static final Logger LOGGER = Logger.getLogger(ReactionPanel.class);
    private MetabolicReaction entity;
    private ReactionRenderer renderer = new ReactionRenderer();
    private JLabel reactionLabel = new Label();
    private CellConstraints cc = new CellConstraints();

    public ReactionPanel() {
        super("Metabolite", new AnnotationRenderer());
    }

    @Override
    public boolean update() {


        // update all fields and labels...
        reactionLabel.setIcon(renderer.getReaction(entity));

        return super.update();

    }

    @Override
    public boolean setEntity(AnnotatedEntity entity) {
        this.entity = (MetabolicReaction) entity;
        return super.setEntity(entity);
    }

    /**
     * Returns the specific information panel
     */
    public JPanel getSynopsis() {

        JPanel panel = new GeneralPanel();

        panel.add(new Label("Todo: Reaction Synopsis"));


        return panel;

    }

    @Override
    public JPanel getBasicPanel() {

        JPanel panel = super.getBasicPanel();

        FormLayout layout = (FormLayout) panel.getLayout();

        // add a row
        layout.appendRow(new RowSpec(RowSpec.CENTER, Sizes.PREFERRED, RowSpec.DEFAULT_GROW));
        panel.add(reactionLabel, cc.xyw(1, layout.getRowCount(), layout.getColumnCount(), cc.CENTER, cc.CENTER));

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
