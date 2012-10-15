/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
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
import uk.ac.ebi.chemet.tools.annotation.ReferenceExtractor;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.Note;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.identifier.IdentifierFactory;
import uk.ac.ebi.mdk.domain.identifier.Resource;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple tool that allows generation of 2D coordinates using the CDK Structure Diagram
 * Generator. As the diagram generator does not respect chirality it is not recomended for
 * cases where an adequate diagram is already available.
 *
 * @author John May
 * @see ExtractReferencesPlugin
 */
public class ExtractReferences
        extends AbstractControlDialog {

    private static final Logger LOGGER = Logger.getLogger(ExtractReferences.class);

    private JCheckBox checkAccessionBox = CheckBoxFactory.newCheckBox();

    public ExtractReferences(Window window) {
        super(window);
    }

    @Override
    public JComponent createForm() {

        JComponent component = super.createForm();

        component.setLayout(new FormLayout("right:p, 4dlu, left:p",
                                           "p"));

        CellConstraints cc = new CellConstraints();

        component.add(getLabel("checkId"), cc.xy(1, 1));
        component.add(checkAccessionBox, cc.xy(3, 1));

        return component;

    }

    @Override
    public void process() {

        // put all the edits together
        CompoundEdit edit = new CompoundEdit();

        ReferenceExtractor<Note> extractor = new ReferenceExtractor<Note>(Note.class);

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
