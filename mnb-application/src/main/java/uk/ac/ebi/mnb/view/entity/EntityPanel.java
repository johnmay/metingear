/**
 * EntityPanelFactory.java
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
package uk.ac.ebi.mnb.view.entity;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.interfaces.Annotation;
import uk.ac.ebi.interfaces.vistors.AnnotationVisitor;
import uk.ac.ebi.mnb.edit.AccessionEdit;
import uk.ac.ebi.mnb.edit.NameEdit;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.TransparentTextField;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.labels.Label;

/**
 *          EntityPanelFactory – 2011.09.30 <br>
 *          Displays the basic info on an entity (accession, abbreviation and name)
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class EntityPanel
        extends GeneralPanel {

    private static final Logger LOGGER = Logger.getLogger(EntityPanel.class);
    private String type;
    private GeneralPanel basic = new GeneralPanel(
            new FormLayout("p, p:grow, p, p:grow, p", "p, 4dlu, p"));
    private Label typeLabel = new Label();
    private JSeparator seperator = new JSeparator(JSeparator.HORIZONTAL);
    private JTextField accession = new TransparentTextField("", 10, false);
    private JTextField name = new TransparentTextField("", 35, false);
    private JTextField abbreviation = new TransparentTextField("", 10, false);
    private AnnotatedEntity entity;
    private JPanel synopsis;
    private JPanel references;
    private JPanel annotations;
    private CellConstraints cc = new CellConstraints();
    private AnnotationRenderer renderer;
    private JPanel middle;
    private JPanel midleft;

    public AnnotationVisitor getRenderer() {
        return renderer;
    }

    public EntityPanel(String type, AnnotationRenderer renderer) {
        this.type = type;
        typeLabel.setText(type);
        layoutBasicPanel();
        this.renderer = renderer;
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        name.setHorizontalAlignment(SwingConstants.CENTER);
        accession.setHorizontalAlignment(SwingConstants.CENTER);
        abbreviation.setHorizontalAlignment(SwingConstants.CENTER);

    }

    public void setup() {
        // could move

        synopsis = getSynopsis();
        annotations = getAnnotationPanel();
        references = getInternalReferencePanel();

        setLayout(new FormLayout("p:grow", "p,p"));
        setBorder(Borders.DLU7_BORDER);

        add(getBasicPanel(), cc.xy(1, 1));

        middle = new GeneralPanel(new FormLayout("p, p", "p"));
        midleft = new GeneralPanel(new FormLayout("p", "p, p"));


        midleft.add(synopsis, cc.xy(1, 1));
        midleft.add(references, cc.xy(1, 2));


        middle.add(midleft, cc.xy(1, 1, CellConstraints.LEFT, CellConstraints.TOP));
        middle.add(annotations, cc.xy(2, 1, CellConstraints.LEFT, CellConstraints.TOP));
        add(middle, cc.xy(1, 2));
        //TODO add observations panel...
    }

    public JPanel getReferences() {
        return references;
    }

    /**
     * lays out the labels of the basic panel
     */
    private void layoutBasicPanel() {
        basic.add(accession, cc.xy(1, 1));
        basic.add(name, cc.xy(3, 1));
        basic.add(abbreviation, cc.xy(5, 1));
        basic.add(seperator, cc.xyw(1, 3, 5));
    }

    public JPanel getBasicPanel() {
        return basic;
    }

    public JPanel getAnnotationPanel() {

        JPanel panel = new GeneralPanel();


        if (entity != null) {

            Collection<Annotation> annotations = entity.getAnnotations();
            panel.setLayout(ViewUtils.formLayoutHelper(2, annotations.size(), 4, 4));
            int i = 1;
            for (Annotation annotation : annotations) {
                panel.add(renderer.getLabel(annotation), cc.xy(1, i));
                panel.add((JComponent) renderer.visit(annotation), cc.xy(3, i)); // better way to do this would be with a method similar to table e.g. this.setText(), this.setLabel()
                i += 2;
            }
        }

        return panel;
    }

    /**
     *
     * Sets the current entity
     * 
     * @param entity true if the entity was different from the previous one
     * 
     */
    public boolean setEntity(AnnotatedEntity entity) {

        if (this.entity == entity) { // don't reset
            return false;
        }

        this.entity = entity;

        return true;
    }

    /**
     * 
     * Updates the current entity
     *
     * @return true if info was updated
     *
     */
    public boolean update() {

        if (entity != null) {
            accession.setText(entity.getAccession());
            name.setText(entity.getName());
            name.setCaretPosition(0);
            abbreviation.setText(entity.getAbbreviation());
            middle.remove(annotations);
            annotations = getAnnotationPanel();
            middle.add(annotations, cc.xy(2, 1, CellConstraints.RIGHT, CellConstraints.TOP));
            return true;
        }

        return false;

    }

    /**
     * Sets if the info is editable
     * @param editable
     */
    public void setEditable(boolean editable) {
        accession.setEditable(editable);
        name.setEditable(editable);
        abbreviation.setEditable(editable);
    }

    /**
     * Persists changed information in the currently selected entity
     */
    public void store() {

        entity.getIdentifier().setAccession(accession.getText().trim());
        entity.setAbbreviation(abbreviation.getText().trim());

        NameEdit nameEdit = new NameEdit(entity, name.getText().trim());
        entity.setName(name.getText().trim());
        MainView.getInstance().getUndoManager().addEdit(nameEdit);


    }

    public abstract JPanel getSynopsis();

    public abstract JPanel getInternalReferencePanel();
}
