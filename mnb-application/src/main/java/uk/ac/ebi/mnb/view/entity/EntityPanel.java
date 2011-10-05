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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.undo.UndoableEdit;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.interfaces.Annotation;
import uk.ac.ebi.interfaces.vistors.AnnotationVisitor;
import uk.ac.ebi.mnb.edit.AbbreviationEdit;
import uk.ac.ebi.mnb.edit.AccessionEdit;
import uk.ac.ebi.mnb.edit.NameEdit;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.TransparentTextField;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.labels.ActionLabel;
import uk.ac.ebi.mnb.view.labels.DeleteAnnotation;
import uk.ac.ebi.mnb.view.labels.IconButton;
import uk.ac.ebi.mnb.view.labels.ThemedLabel;

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
    private ThemedLabel typeLabel = new ThemedLabel();
    private JSeparator seperator = new JSeparator(JSeparator.HORIZONTAL);
    private JTextField accession = new TransparentTextField("", 10, false);
    private JTextField name = new TransparentTextField("", 35, false);
    private JTextField abbreviation = new TransparentTextField("", 10, false);
    private AnnotatedEntity entity;
    private JPanel info;
    private JPanel references;
    private JPanel annotations;
    private CellConstraints cc = new CellConstraints();
    private AnnotationRenderer renderer;
    private JPanel middle;
    private JPanel synopsis;
    private List<JButton> deleteButtons = new ArrayList();

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

        setLayout(new FormLayout("p:grow", "p,p,p"));
        setBorder(Borders.DLU7_BORDER);

        add(getBasicPanel(), cc.xy(1, 1));

        middle = new GeneralPanel(new FormLayout("p:grow, 4dlu, p:grow", "p"));
        info = new GeneralPanel(new FormLayout("p", "p, p, p"));


        info.add(synopsis, cc.xy(1, 1));
        info.add(new JSeparator(), cc.xy(1, 2));
        info.add(references, cc.xy(1, 3));


        middle.add(info, cc.xy(1, 1, CellConstraints.LEFT, CellConstraints.TOP));
        middle.add(annotations, cc.xy(3, 1, CellConstraints.CENTER, CellConstraints.TOP));
        add(middle, cc.xy(1, 2));
        add(new JSeparator(), cc.xy(1, 3));
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

        deleteButtons = new ArrayList(); // todo use a pool

        if (entity != null) {

            Collection<Annotation> annotations = entity.getAnnotations();
            panel.setLayout(ViewUtils.formLayoutHelper(3, annotations.size(), 4, 4));
            int i = 1;
            for (Annotation annotation : annotations) {
                panel.add(renderer.getLabel(annotation), cc.xy(3, i));
                panel.add((JComponent) renderer.visit(annotation), cc.xy(5, i)); // better way to do this would be with a method similar to table e.g. this.setText(), this.setLabel()

                MainView view = MainView.getInstance();

                DeleteAnnotation action = new DeleteAnnotation(entity,
                        annotation,
                        view.getViewController().getActiveView(),
                        view.getUndoManager());
                JButton remove = new IconButton(ViewUtils.getIcon("images/cutout/close_16x16.png", "Remove annotation"),action);
                remove.setVisible(editable);
                deleteButtons.add(remove);
                panel.add(remove, cc.xy(1, i));

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
            middle.add(annotations, cc.xy(3, 1, CellConstraints.RIGHT, CellConstraints.TOP));
            return true;
        }

        return false;

    }

    /**
     * Sets if the info is editable
     * @param editable
     */
    public void setEditable(boolean editable) {

        this.editable = editable;

        accession.setEditable(editable);
        name.setEditable(editable);
        abbreviation.setEditable(editable);

        for (JButton label : deleteButtons) {
            label.setVisible(editable);
        }


    }

    private boolean editable;

    /**
     * Persists changed information in the currently selected entity
     */
    public void store() {

        String accessionText = accession.getText().trim();
        if (!accessionText.equals(entity.getAccession())) {
            UndoableEdit nameEdit = new AccessionEdit(entity, accessionText);
            entity.getIdentifier().setAccession(accessionText);
            MainView.getInstance().getUndoManager().addEdit(nameEdit);
        }

        String abbreviationText = abbreviation.getText().trim();
        if (!abbreviationText.equals(entity.getAbbreviation())) {
            UndoableEdit nameEdit = new AbbreviationEdit(entity, abbreviationText);
            entity.setAbbreviation(abbreviationText);
            MainView.getInstance().getUndoManager().addEdit(nameEdit);
        }

        String nameText = name.getText().trim();
        if (!nameText.equals(entity.getName())) {
            UndoableEdit nameEdit = new NameEdit(entity, nameText);
            entity.setName(nameText);
            MainView.getInstance().getUndoManager().addEdit(nameEdit);
        }


    }

    public abstract JPanel getSynopsis();

    public abstract JPanel getInternalReferencePanel();
}
