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

package uk.ac.ebi.metingear.edit;

import uk.ac.ebi.mdk.domain.annotation.Note;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import java.awt.*;

/**
 * A simple utility dialog for adding notes to an entity from a text area.
 * @author John May
 */
public class AddNote extends AbstractControlDialog {

    private final JTextArea area = new JTextArea(8, 20);

    public AddNote(Window window) {
        super(window);

    }

    @Override public JComponent createForm() {
        JScrollPane pane = new JScrollPane(area);
        return pane;
    }

    @Override public void process() {

        String content = area.getText().trim();
        if(content.isEmpty())
            return;

        CompoundEdit edit = new CompoundEdit();
        for(AnnotatedEntity e : getSelectionController().getSelection().getEntities()){
            Note note = new Note(content);
            edit.addEdit(new AddAnnotationEdit(e, note));
            e.addAnnotation(note);
        }

        edit.end();
        addEdit(edit);
    }

    @Override public void update() {
        super.update(getSelectionController().getSelection());
    }
}
