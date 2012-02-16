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

import uk.ac.ebi.interfaces.entities.GeneProduct;
import uk.ac.ebi.mnb.view.entity.components.AnnotationTable;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uk.ac.ebi.interfaces.*;
import uk.ac.ebi.interfaces.vistors.AnnotationVisitor;
import uk.ac.ebi.mnb.dialog.popup.AlignmentViewer;
import uk.ac.ebi.mnb.edit.*;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.observation.sequence.LocalAlignment;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import java.awt.Component;
import java.awt.Font;
import uk.ac.ebi.core.AbstractAnnotatedEntity;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.mnb.view.AnnotationRenderer;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.observation.ObservationCollection;
import uk.ac.ebi.chemet.render.list.renderers.LocalAlignmentListCellRenderer;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.chemet.render.PooledClassBasedListCellDRR;
import uk.ac.ebi.caf.component.ui.VerticalLabelUI;
import uk.ac.ebi.caf.utility.ColorUtility;
import uk.ac.ebi.mnb.view.entity.components.InternalReferences;


/**
 *          EntityPanelFactory – 2011.09.30 <br>
 *          Displays the basic info on an entity (accession, abbreviation and
 *          name). Additional entries can be added to the basic information by
 *          overriding the method and adding rows (see ProductPanel or 
 *          ReactionPanel). 
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class AbstractEntityPanel
        extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(AbstractEntityPanel.class);

    private String type;

    private JLabel typeLabel = LabelFactory.newLabel("");

    private JTextField accession = FieldFactory.newTransparentField(10, false);

    private JTextField name = FieldFactory.newTransparentField(30, false);

    private JTextField abbreviation = FieldFactory.newTransparentField(10, false);

    private AnnotatedEntity entity;
    //

    private JList observationList = new JList();

    private InternalReferences references = new InternalReferences();

    private AnnotationTable annotationTable;
    //

    private DefaultListModel observationModel = new DefaultListModel();

    private CellConstraints cc = new CellConstraints();

    private AnnotationRenderer renderer;

    private JPanel middle;

    private JPanel synopsis;

    private JPanel basic = PanelFactory.createInfoPanel("p, p:grow, p, p:grow, p", "p");

    private JPanel observations;

    private JLabel refLabel;

    private JLabel synLabel;

    private JLabel annLabel;

    private JScrollPane refPane;

    private List<JButton> removeAnnotationButtons = new ArrayList();

    private boolean editable;


    public AnnotationVisitor getRenderer() {
        return renderer;
    }


    public AbstractEntityPanel(String type,
                               AnnotationRenderer renderer) {
        setBackground(Color.WHITE);
        this.type = type;
        typeLabel.setText(type);
        layoutBasicPanel();
        this.renderer = renderer;
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        name.setHorizontalAlignment(SwingConstants.CENTER);
        accession.setHorizontalAlignment(SwingConstants.CENTER);
        abbreviation.setHorizontalAlignment(SwingConstants.CENTER);

        observationList.setModel(observationModel);
        observationList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                alignmentView.setVisible(false);
            }
        });
        observationList.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int index = observationList.locationToIndex(e.getPoint());
                if (index == -1) {
                    alignmentView.setVisible(false);
                    return;
                }
                Object value = observationModel.get(index);
                if (value instanceof LocalAlignment) {
                    LocalAlignment alignment = (LocalAlignment) value;
                    float location = Math.min(e.getX() / 750f, 1f);
                    alignmentView.setSequence(alignment, location);
                    alignmentView.pack();
                    alignmentView.setOnMouse(20);
                    if (alignmentView.isVisible() == false) {
                        alignmentView.setVisible(true);
                    };
                }
            }
        });

        annotationTable = new AnnotationTable();
        annotationTable.getModel().setObservationList(observationList);


        OBSERVATION_RENDERING_POOL = new PooledClassBasedListCellDRR();

        OBSERVATION_RENDERING_POOL.setRenderer(LocalAlignment.class, new LocalAlignmentListCellRenderer());

        observationList.setCellRenderer(OBSERVATION_RENDERING_POOL);


    }

    private PooledClassBasedListCellDRR OBSERVATION_RENDERING_POOL;

    private AlignmentViewer alignmentView = new AlignmentViewer(MainView.getInstance(), 15);


    public void setup() {

        synopsis = getSynopsis();
        observations = getObservationPanel();

        setLayout(new FormLayout("p:grow", "p,p,p,p,p"));
        setBorder(Borders.DLU7_BORDER);

        add(getBasicPanel(), cc.xy(1, 1));
        basic.setBorder(Borders.DLU4_BORDER);

        middle = PanelFactory.createInfoPanel("p, p:grow, p, p:grow, p, p:grow", "p");
        middle.setBorder(Borders.DLU4_BORDER);

        Box synBox = Box.createHorizontalBox();
        synLabel = LabelFactory.newVerticalFormLabel("Synopsis",
                                                     VerticalLabelUI.Rotation.ANTICLOCKWISE);
        synBox.add(synLabel);
        synBox.add(Box.createHorizontalGlue());
        synBox.add(synopsis);
        synLabel.setAlignmentY(TOP_ALIGNMENT);
        synopsis.setAlignmentY(TOP_ALIGNMENT);
        synBox.setAlignmentY(TOP_ALIGNMENT);
        middle.add(synBox, cc.xy(1, 1, cc.CENTER, cc.TOP));

        Box refBox = Box.createHorizontalBox();
        refLabel = LabelFactory.newVerticalFormLabel("Associations",
                                                     VerticalLabelUI.Rotation.ANTICLOCKWISE);


        refPane = new BorderlessScrollPane(references);
        refBox.add(refLabel);
        refBox.add(Box.createHorizontalGlue());
        refBox.add(refPane);
        refLabel.setAlignmentY(TOP_ALIGNMENT);
        refPane.setAlignmentY(TOP_ALIGNMENT);
        refBox.setAlignmentY(TOP_ALIGNMENT);
        middle.add(refBox, cc.xy(3, 1, cc.CENTER, cc.TOP));


        Box annBox = Box.createHorizontalBox();
        annLabel = LabelFactory.newVerticalFormLabel("Annotations",
                                                     VerticalLabelUI.Rotation.ANTICLOCKWISE);
        annBox.add(annLabel);
        annBox.add(annotationTable);
        annLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        annotationTable.setAlignmentY(Component.TOP_ALIGNMENT);
        annBox.setAlignmentY(Component.TOP_ALIGNMENT);
        middle.add(annBox, cc.xy(5, 1));


        refLabel.setForeground(ColorUtility.EMBL_PETROL_75);
        synLabel.setForeground(ColorUtility.EMBL_PETROL_75);
        annLabel.setForeground(ColorUtility.EMBL_PETROL_75);
        refLabel.setFont(new Font("Gill Sans", Font.PLAIN, 20));
        synLabel.setFont(new Font("Gill Sans", Font.PLAIN, 20));
        annLabel.setFont(new Font("Gill Sans", Font.PLAIN, 20));
//        refLabel.setForeground(ColorUtility.shade(refLabel.getForeground(), 0.4f));
//        synLabel.setForeground(ColorUtility.shade(synLabel.getForeground(), 0.4f));
//        annLabel.setForeground(ColorUtility.shade(annLabel.getForeground(), 0.4f));


        add(new JSeparator(), cc.xy(1, 2));
        add(middle, cc.xy(1, 3));
        add(new JSeparator(), cc.xy(1, 4));
        add(observations, cc.xy(1, 5));


    }


    public JPanel getObservationPanel() {
        JPanel panel = PanelFactory.createInfoPanel();
        panel.add(observationList);
        return panel;
    }


    /**
     * layout the labels of the basic panel
     */
    private void layoutBasicPanel() {
        basic.add(accession, cc.xy(1, 1));
        basic.add(name, cc.xy(3, 1));
        basic.add(abbreviation, cc.xy(5, 1));
    }


    public JPanel getBasicPanel() {
        basic.setBorder(Borders.DLU4_BORDER);
        return basic;
    }


    public JPanel getAnnotationPanel() {

        JPanel panel = PanelFactory.createInfoPanel();
        panel.setBorder(Borders.DLU4_BORDER);

        return panel;

    }


    /**
     * Clears all displayed components
     */
    public void clear() {
        accession.setText("");
        name.setText("");
        abbreviation.setText("");
        annotationTable.clear();
        references.clear();
        observationModel.removeAllElements();
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

        annotationTable.setEntity(entity);
        annotationTable.getModel().fireTableDataChanged();

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

            // for sequence rendering
            if (entity instanceof GeneProduct) {
                alignmentView.setProduct((GeneProduct) entity);
            }

            // update internal references
            references.getModel().setEntities(getReferences());

            // Update obserations
            {
                // check the objects back in to the pool
                for (int i = 0; i < observationModel.getSize(); i++) {
                    Observation o = (Observation) observationModel.remove(i);
                    OBSERVATION_RENDERING_POOL.checkIn(o);
                }

                observationModel.removeAllElements();
                ObservationCollection collection = ((AbstractAnnotatedEntity) entity).getObservationCollection();

                Collection<Observation> observationCollection = collection.get(LocalAlignment.class);
                if (!observationCollection.isEmpty()) {
                    LOGGER.debug("Displying " + observationCollection.size() + " observations");
                }

                for (Observation observation : observationCollection) {
                    observationModel.addElement(observation);
                }

            }

            annotationTable.getModel().fireTableDataChanged();

//            middle.remove(annotations);
//            annotations = getAnnotationPanel();
//            ((Box) middle.getComponent(2)).remove(1);
//            ((Box) middle.getComponent(2)).add(annotations);
            return true;
        } else {
            clear();
        }

        return false;

    }


    /**
     * 
     * Sets if the info is editable
     *
     * @param editable
     * 
     */
    public void setEditable(boolean editable) {

        this.editable = editable;

        accession.setEditable(editable);
        name.setEditable(editable);
        abbreviation.setEditable(editable);

        annotationTable.setEditable(editable);

    }


    /**
     * Access whether the panel is editable or not.
     * @return
     */
    public boolean isEditable() {
        return editable;
    }


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


    public Collection<? extends AnnotatedEntity> getReferences() {
        return new ArrayList<AnnotatedEntity>();
    }
}
