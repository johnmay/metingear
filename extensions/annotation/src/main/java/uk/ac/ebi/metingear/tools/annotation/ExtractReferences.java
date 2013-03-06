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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.caf.component.factory.FieldFactory;
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

import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import java.awt.*;
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

    private static final Logger LOGGER = Logger.getLogger(ExtractReferences.class);

    private JCheckBox checkAccessionBox = CheckBoxFactory.newCheckBox();
    private JCheckBox override = CheckBoxFactory.newCheckBox();

    private JComboBox resources = ComboBoxFactory.newComboBox(DefaultIdentifierFactory.getInstance().getSupportedIdentifiers());
    private JTextField resourcePattern = FieldFactory.newField(30);
    private JTextField separatorPattern = FieldFactory.newField(30);
    private JTextField accessionPattern = FieldFactory.newField(30);

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
                resourcePattern.setText(((Identifier) resources.getSelectedItem()).getShortDescription());
            }
        });
    }

    @Override
    public JComponent createForm() {

        JComponent component = super.createForm();

        component.setLayout(new FormLayout("right:p, 4dlu, left:p",
                                           "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"));

        CellConstraints cc = new CellConstraints();

        component.add(getLabel("checkId"), cc.xy(1, 1));
        component.add(checkAccessionBox, cc.xy(3, 1));
        component.add(getLabel("overrideLabel"), cc.xy(1, 3));
        component.add(override, cc.xy(3, 3));

        // specific parsing
        component.add(getLabel("resourceSelection"), cc.xy(1, 5));
        component.add(resources, cc.xy(3, 5));
        component.add(getLabel("resourcePattern"), cc.xy(1, 7));
        component.add(resourcePattern, cc.xy(3, 7));
        component.add(getLabel("separatorPattern"), cc.xy(1, 9));
        component.add(separatorPattern, cc.xy(3, 9));
        component.add(getLabel("accessionPattern"), cc.xy(1, 11));
        component.add(accessionPattern, cc.xy(3, 11));

        return component;

    }

    @Override
    public void process() {

        // put all the edits together
        CompoundEdit edit = new CompoundEdit();

        String name = ((Identifier) resources.getSelectedItem()).getShortDescription();

        ReferenceExtractor<Note> extractor = override.isSelected()
                                             ? new ReferenceExtractor<Note>(Note.class, name,
                                                                            resourcePattern.getText().trim(),
                                                                            separatorPattern.getText().trim(),
                                                                            accessionPattern.getText().trim())
                                             : new ReferenceExtractor<Note>(Note.class);

        AnnotationFactory annotationFactory = DefaultAnnotationFactory.getInstance();

        boolean checkAccession = checkAccessionBox.isSelected();
        List<Annotation> queue = new ArrayList<Annotation>();

        for (AnnotatedEntity entity : getSelectionController().getSelection().getEntities()) {

            for (Annotation annotation : entity.getAnnotations()) {

                Identifier identifier = annotation.accept(extractor);

                if (identifier != IdentifierFactory.EMPTY_IDENTIFIER) {

                    // if the user wants to check accessions (e.g. removes N/A accessions)
                    // we use the pattern (from MIRIAM) to check the ID
                    if (checkAccession) {
                        Resource resource = identifier.getResource();
                        if (resource != null && !resource.getCompiledPattern().matcher(identifier.getAccession()).matches())
                            continue;
                    }

                    CrossReference xref = (CrossReference) annotationFactory.getCrossReference(identifier);
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
