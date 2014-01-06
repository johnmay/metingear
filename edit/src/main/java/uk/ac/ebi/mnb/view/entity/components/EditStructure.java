/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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
package uk.ac.ebi.mnb.view.entity.components;

import org.openscience.cdk.interfaces.IAtomContainer;
import uk.ac.ebi.caf.utility.font.EBIIcon;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mnb.edit.RemoveAnnotationEdit;
import uk.ac.ebi.mnb.edit.ReplaceAnnotationEdit;
import uk.ac.ebi.mnb.interfaces.StructureEditor;
import uk.ac.ebi.mnb.interfaces.Updatable;

import javax.swing.AbstractAction;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;

/** An action to edit a {@link ChemicalStructure} annotation. */
final class EditStructure extends AbstractAction {

    private final UndoableEditListener editListener;
    private final StructureEditor      editor;
    private final Updatable            updatable;
    private final AnnotatedEntity      entity;
    private final ChemicalStructure    org;
    private final Window               window;

    public EditStructure(Window window,
                         StructureEditor editor,
                         AnnotatedEntity entity,
                         ChemicalStructure org,
                         Updatable updatable,
                         UndoableEditListener editListener) {
        this.editListener = editListener;
        this.window = window;
        this.editor = editor;
        this.updatable = updatable;
        this.entity = entity;
        this.org = org;
        putValue(SHORT_DESCRIPTION, "Edit chemical structure");
        putValue(LARGE_ICON_KEY, EBIIcon.EDIT.create()
                                        .size(10f)
                                        .color(Color.DARK_GRAY)
                                        .icon());
    }

    public void actionPerformed(ActionEvent ae) {

        IAtomContainer container = editor.edit(org.getStructure());

        // no structure editor - or editing was cancelled 
        if (container == null)
            return;
        
        ChemicalStructure alt = (ChemicalStructure) org.newInstance();

        alt.setStructure(container);
        
        ReplaceAnnotationEdit rae = new ReplaceAnnotationEdit(entity, org, alt);
        rae.apply();
        editListener.undoableEditHappened(new UndoableEditEvent(ae, rae));
        
        updatable.update();
    }
}
