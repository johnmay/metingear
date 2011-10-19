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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uk.ac.ebi.chemet.visualisation.*;
import uk.ac.ebi.core.AbstractAnnotatedEntity;
import uk.ac.ebi.interfaces.*;
import uk.ac.ebi.interfaces.vistors.AnnotationVisitor;
import uk.ac.ebi.mnb.dialog.popup.AlignmentViewer;
import uk.ac.ebi.mnb.edit.*;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.renderers.ListLinkRenderer;
import uk.ac.ebi.mnb.view.*;
import uk.ac.ebi.mnb.view.labels.IconButton;
import uk.ac.ebi.mnb.view.labels.MLabel;
import uk.ac.ebi.observation.ObservationCollection;
import uk.ac.ebi.observation.sequence.LocalAlignment;
import uk.ac.ebi.visualisation.ColorUtilities;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import uk.ac.ebi.mnb.interfaces.SelectionManager;

/**
 *          EntityPanelFactory – 2011.09.30 <br>
 *          Displays the basic info on an entity (accession, abbreviation and name). Additional entries can be added to
 *          the basic information by overriding the method and adding rows (see ProductPanel or ReactionPanel).
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class AbstractEntityPanel
        extends GeneralPanel {

    private static final Logger LOGGER = Logger.getLogger(AbstractEntityPanel.class);
    private String type;
    private GeneralPanel basic = new GeneralPanel(
            new FormLayout("p, p:grow, p, p:grow, p", "p, 4dlu, p"));
    private MLabel typeLabel = new MLabel();
    private JSeparator seperator = new JSeparator(JSeparator.HORIZONTAL);
    private JTextField accession = new TransparentTextField("", 10, false);
    private JTextField name = new TransparentTextField("", 35, false);
    private JTextField abbreviation = new TransparentTextField("", 10, false);
    private AnnotatedEntity entity;
    private JPanel info;
    private JPanel annotations;
    private JPanel observations;
    private JList observationList = new JList();
    private DefaultListModel observationModel = new DefaultListModel();
    private CellConstraints cc = new CellConstraints();
    private AnnotationRenderer renderer;
    private JPanel middle;
    private JPanel synopsis;
    private List<JButton> removeAnnotationButtons = new ArrayList();
    private JList references = new JList();
    private DefaultListModel refModel = new DefaultListModel();
    private boolean editable;

    public AnnotationVisitor getRenderer() {
        return renderer;
    }

    public AbstractEntityPanel(String type, AnnotationRenderer renderer) {
        this.type = type;
        typeLabel.setText(type);
        layoutBasicPanel();
        this.renderer = renderer;
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        name.setHorizontalAlignment(SwingConstants.CENTER);
        accession.setHorizontalAlignment(SwingConstants.CENTER);
        abbreviation.setHorizontalAlignment(SwingConstants.CENTER);

        // internal references
        references.setModel(refModel);
        references.setVisibleRowCount(8);
        references.setCellRenderer(new ListLinkRenderer());
        references.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int index = references.locationToIndex(e.getPoint());
                if (index == -1) {
                    return;
                }
                if (references.getCellBounds(index, index).contains(e.getPoint())) {
                    references.setSelectedIndex(index);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    references.removeSelectionInterval(0, references.getModel().getSize());
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
        references.addMouseListener(new MouseAdapter() {

            // need to monitor when we leave (mouseMoved no longer inside JList)
            @Override
            public void mouseExited(MouseEvent e) {
                references.removeSelectionInterval(0, references.getModel().getSize());
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int index = references.locationToIndex(e.getPoint());
                SelectionManager manager = MainView.getInstance().getViewController().getSelection();
                manager.clear().add((AnnotatedEntity) refModel.get(index));
                MainView.getInstance().getViewController().setSelection(manager);
            }
        });

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


    }
    private AlignmentViewer alignmentView = new AlignmentViewer(MainView.getInstance(), 15);

    public void setup() {
        // could move

        synopsis = getSynopsis();
        annotations = getAnnotationPanel();
        observations = getObservationPanel();
        //references = getInternalReferencePanel();

        setLayout(new FormLayout("p:grow", "p,p,p,p"));
        setBorder(Borders.DLU7_BORDER);

        add(getBasicPanel(), cc.xy(1, 1));

        middle = new GeneralPanel(new FormLayout("p, 5dlu, p, 5dlu, p", "p"));

        middle.add(synopsis, cc.xy(1, 1));
        middle.add(new BorderlessScrollPane(references), cc.xy(3, 1));
        middle.add(annotations, cc.xy(5, 1));

        add(middle, cc.xy(1, 2));
        add(new JSeparator(), cc.xy(1, 3));
        add(observations, cc.xy(1, 4));



    }

    public JPanel getObservationPanel() {
        JPanel panel = new GeneralPanel();
        observationList.setModel(observationModel);
        panel.add(observationList);
        return panel;
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
        basic.setBorder(Borders.DLU4_BORDER);
        return basic;
    }

    public JPanel getAnnotationPanel() {

        JPanel panel = new GeneralPanel();
        panel.setBorder(Borders.DLU4_BORDER);

        removeAnnotationButtons = new ArrayList(); // todo use a pool

        MainView view = MainView.getInstance();
        Icon closeIcon = ViewUtils.getIcon("images/cutout/close_16x16.png", "Remove annotation");

        if (entity != null) {
            FormLayout layout = new FormLayout("p:grow, min, 4dlu, p, 4dlu, left:p:grow", "");
            panel.setLayout(layout);
            for (Annotation annotation : entity.getAnnotations()) {
                layout.appendRow(new RowSpec(Sizes.PREFERRED));
                panel.add(renderer.getLabel(annotation), cc.xy(4, layout.getRowCount()));
                panel.add((JComponent) renderer.visit(annotation), cc.xy(6, layout.getRowCount())); // better way to do this would be with a method similar to table e.g. this.setText(), this.setLabel()


                DeleteAnnotation action = new DeleteAnnotation(entity,
                                                               annotation,
                                                               view.getViewController().getActiveView(),
                                                               view.getUndoManager());
                JButton remove = new IconButton(closeIcon, action);
                remove.setVisible(editable);
                removeAnnotationButtons.add(remove);
                panel.add(remove, cc.xy(2, layout.getRowCount()));
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


            // for sequence rendering
            if (entity instanceof GeneProduct) {
                alignmentView.setProduct((GeneProduct) entity);
            }

            // update the internal references
            refModel.removeAllElements();


            new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {
                    for (AnnotatedEntity ref : getReferences()) {
                        refModel.addElement(ref);
                    }
                    return null;
                }
            }.run();


            observationModel.removeAllElements();
            final ConservationRenderer complexRenderer = new ConservationRenderer(new Rectangle(0, 0, 750, 10),
                                                                                  new BasicAlignmentColor(ColorUtilities.EMBL_PETROL, ColorUtilities.EMBL_PETROL, Color.lightGray),
                                                                                  new BlastConsensusScorer(),
                                                                                  1);
            complexRenderer.setGranularity(0.8f);
            final AlignmentRenderer basicRenderer = new AlignmentRenderer(new Rectangle(0, 0, 750, 10),
                                                                          new BasicAlignmentColor(ColorUtilities.EMBL_PETROL, ColorUtilities.EMBL_PETROL, Color.lightGray),
                                                                          1);


            observationList.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof LocalAlignment) {
                        LocalAlignment alignment = (LocalAlignment) value;
                        AlignmentRenderer renderer = alignment.hasSequences() ? complexRenderer : basicRenderer;
                        Icon icon = new ImageIcon(renderer.render((LocalAlignment) value, (GeneProduct) entity));
                        this.setIcon(icon);
                        this.setBorder(null);
                        this.setBackground(null);
                        this.setFont(ViewUtils.VERDANA_PLAIN_11);
                        this.setText(alignment.getSubject());
                        this.setToolTipText(ViewUtils.htmlWrapper(alignment.getHTMLSummary()));
                        return this;
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });
            ObservationCollection collection = ((AbstractAnnotatedEntity) entity).getObservationCollection();
            int i = 0;
            for (Observation observation : collection.get(LocalAlignment.class)) {
                observationModel.addElement(observation);
                if (i++ > 15) {
                    break;
                }
            }

            middle.remove(annotations);
            annotations = getAnnotationPanel();
            middle.add(annotations, cc.xy(5, 1, CellConstraints.CENTER, CellConstraints.TOP));
            return true;
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

        for (JButton label : removeAnnotationButtons) {
            label.setVisible(editable);
        }

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
