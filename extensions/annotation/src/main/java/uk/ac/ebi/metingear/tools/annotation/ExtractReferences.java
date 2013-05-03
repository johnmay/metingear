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

package uk.ac.ebi.metingear.tools.annotation;

import com.google.common.base.Function;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.chemet.tools.annotation.ReferenceExtractor;
import uk.ac.ebi.mdk.domain.DefaultIdentifierFactory;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.Note;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.identifier.IdentifierFactory;
import uk.ac.ebi.mdk.domain.identifier.Resource;
import uk.ac.ebi.mdk.ui.render.list.DefaultRenderer;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.undo.CompoundEdit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple tool that allows generation of 2D coordinates using the CDK
 * Structure Diagram Generator. As the diagram generator does not respect
 * chirality it is not recomended for cases where an adequate diagram is already
 * available.
 *
 * @author John May
 * @see ExtractReferencesPlugin
 */
public class ExtractReferences
        extends AbstractControlDialog {

    private static final Logger LOGGER = Logger
            .getLogger(ExtractReferences.class);

    private JCheckBox checkAccessionBox = CheckBoxFactory.newCheckBox();
    private JCheckBox override = CheckBoxFactory.newCheckBox();

    private JComboBox resources = ComboBoxFactory
            .newComboBox(DefaultIdentifierFactory.getInstance()
                                                 .getSupportedIdentifiers());
    private JTextField resourcePattern = FieldFactory.newField(30);
    private JTextField separatorPattern = FieldFactory.newField(30);
    private JTextField accessionPattern = FieldFactory.newField(30);

    private JComboBox source = ComboBoxFactory
            .newComboBox("Identifier", "Name", "Abbreviation");
    private JComboBox type = ComboBoxFactory
            .newComboBox(DefaultIdentifierFactory.getInstance()
                                                 .getSupportedIdentifiers());

    private final JTabbedPane tabbedPane = new JTabbedPane();

    public ExtractReferences(Window window) {
        super(window);
        checkAccessionBox.setSelected(true);
        override.setSelected(false);
        resources.setEnabled(false);

        resourcePattern.setEnabled(false);
        separatorPattern.setEnabled(false);
        accessionPattern.setEnabled(false);

        resourcePattern.setText(ReferenceExtractor.DEFAULT_RESOURCE_PATTERN);
        separatorPattern.setText(ReferenceExtractor.DEFAULT_SEPARATOR_PATTERN);
        accessionPattern.setText(ReferenceExtractor.DEFAULT_ACCESSION_PATTERN);

        resources.setRenderer(new DefaultRenderer<Identifier>() {
            @Override
            public JLabel getComponent(JList list, Identifier value, int index) {
                JLabel label = super.getComponent(list, value, index);
                label.setText(value.getShortDescription());
                return label;
            }
        });
        type.setRenderer(new DefaultRenderer<Identifier>() {
            @Override
            public JLabel getComponent(JList list, Identifier value, int index) {
                JLabel label = super.getComponent(list, value, index);
                label.setText(value.getShortDescription());
                return label;
            }
        });
        override.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                resources.setEnabled(override.isSelected());
                resourcePattern.setEnabled(override.isSelected());
                accessionPattern.setEnabled(override.isSelected());
                separatorPattern.setEnabled(override.isSelected());
            }
        });
        resources.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                // update the pattern e.g. ChEBI -> 'ChEBI\\s+[^\\s]+\\s+(.+)'
                resourcePattern.setText(((Identifier) resources
                        .getSelectedItem()).getShortDescription());
            }
        });
    }

    @Override
    public JComponent createForm() {

        JComponent component = super.createForm();

        JComponent fromNotes = PanelFactory.createDialogPanel();
        JComponent fromBasic = PanelFactory.createDialogPanel();
        fromNotes.setLayout(new FormLayout("right:p, 4dlu, left:p",
                                           "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"));
        fromBasic.setLayout(new FormLayout("right:p, 4dlu, left:p",
                                           "p, 4dlu, p"));
        fromNotes
                .setBorder(Borders.createEmptyBorder("4dlu, 4dlu, 4dlu, 4dlu"));
        fromBasic
                .setBorder(Borders.createEmptyBorder("4dlu, 4dlu, 4dlu, 4dlu"));

        CellConstraints cc = new CellConstraints();

        fromNotes.add(getLabel("checkId"), cc.xy(1, 1));
        fromNotes.add(checkAccessionBox, cc.xy(3, 1));
        fromNotes.add(getLabel("overrideLabel"), cc.xy(1, 3));
        fromNotes.add(override, cc.xy(3, 3));

        // specific parsing
        fromNotes.add(getLabel("resourceSelection"), cc.xy(1, 5));
        fromNotes.add(resources, cc.xy(3, 5));
        fromNotes.add(getLabel("resourcePattern"), cc.xy(1, 7));
        fromNotes.add(resourcePattern, cc.xy(3, 7));
        fromNotes.add(getLabel("separatorPattern"), cc.xy(1, 9));
        fromNotes.add(separatorPattern, cc.xy(3, 9));
        fromNotes.add(getLabel("accessionPattern"), cc.xy(1, 11));
        fromNotes.add(accessionPattern, cc.xy(3, 11));

        fromBasic.add(getLabel("source"), cc.xy(1, 1));
        fromBasic.add(source, cc.xy(3, 1));
        fromBasic.add(getLabel("type"), cc.xy(1, 3));
        fromBasic.add(type, cc.xy(3, 3));


        tabbedPane.add("Extract from Notes", fromNotes);
        tabbedPane.add("Extract from Id, Name or Abrv", fromBasic);

        component.add(tabbedPane);
        return component;

    }

    @Override public void process() {
        int i = tabbedPane.getSelectedIndex();
        if (i == 0)
            processNote();
        else
            processBasic();
    }

    public void processBasic() {
        CompoundEdit edit = new CompoundEdit();

        Function<AnnotatedEntity, String> accessor = accessor();
        Identifier idType = (Identifier) type.getSelectedItem();

        AnnotationFactory annotationFactory = DefaultAnnotationFactory
                .getInstance();

        for (AnnotatedEntity entity : getSelectionController().getSelection()
                .getEntities()) {
            String accession = accessor.apply(entity);
            Identifier id = idType.newInstance();
            id.setAccession(accession);
            if (id.isValid()) {
                // add to entity
                Annotation xref = annotationFactory.getCrossReference(id);
                edit.addEdit(new AddAnnotationEdit(entity, xref));
                entity.addAnnotation(xref);
            }
        }

        edit.end();
        addEdit(edit);
    }

    public Function<AnnotatedEntity, String> accessor() {
        String sourceItem = (String) source.getSelectedItem();
        if (sourceItem.equals("Identifier"))
            return new Function<AnnotatedEntity, String>() {
                @Override
                public String apply(AnnotatedEntity entity) {
                    return entity.getAccession();
                }
            };
        if (sourceItem.equals("Abbreviation"))
            return new Function<AnnotatedEntity, String>() {
                @Override
                public String apply(AnnotatedEntity entity) {
                    return entity.getAbbreviation();
                }
            };
        if (sourceItem.equals("Name"))
            return new Function<AnnotatedEntity, String>() {
                @Override
                public String apply(AnnotatedEntity entity) {
                    return entity.getName();
                }
            };
        throw new IllegalArgumentException("No accessor available");
    }

    public void processNote() {

        // put all the edits together
        CompoundEdit edit = new CompoundEdit();

        String name = ((Identifier) resources.getSelectedItem())
                .getShortDescription();

        ReferenceExtractor<Note> extractor = override.isSelected()
                                             ? new ReferenceExtractor<Note>(Note.class, name,
                                                                            resourcePattern
                                                                                    .getText()
                                                                                    .trim(),
                                                                            separatorPattern
                                                                                    .getText()
                                                                                    .trim(),
                                                                            accessionPattern
                                                                                    .getText()
                                                                                    .trim())
                                             : new ReferenceExtractor<Note>(Note.class);

        AnnotationFactory annotationFactory = DefaultAnnotationFactory
                .getInstance();

        boolean checkAccession = checkAccessionBox.isSelected();
        List<Annotation> queue = new ArrayList<Annotation>();

        for (AnnotatedEntity entity : getSelectionController().getSelection()
                .getEntities()) {

            for (Annotation annotation : entity.getAnnotations()) {

                Identifier identifier = annotation.accept(extractor);

                if (identifier != IdentifierFactory.EMPTY_IDENTIFIER) {

                    // if the user wants to check accessions (e.g. removes N/A accessions)
                    // we use the pattern (from MIRIAM) to check the ID
                    if (checkAccession) {
                        Resource resource = identifier.getResource();
                        if (resource != null && !resource.getCompiledPattern()
                                                         .matcher(identifier
                                                                          .getAccession())
                                                         .matches())
                            continue;
                    }

                    CrossReference xref = (CrossReference) annotationFactory
                            .getCrossReference(identifier);
                    queue.add(xref);

                }

            }

            // add to edit stack
            edit.addEdit(new AddAnnotationEdit(entity,
                                               queue));

            // actually do the edit
            entity.addAnnotations(queue);
            queue.clear();

        }

        // inform the compound edit that we've finished editing
        edit.end();

        addEdit(edit);

    }

    @Override
    public void update() {
        super.update(getSelectionController().getSelection());
    }

}
