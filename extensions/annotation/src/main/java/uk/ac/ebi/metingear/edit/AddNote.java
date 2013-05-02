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

import uk.ac.ebi.caf.component.complete.PrefixSearch;
import uk.ac.ebi.mdk.domain.annotation.Note;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple utility dialog for adding notes to an entity from a text area.
 *
 * @author John May
 */
public class AddNote extends AbstractControlDialog {

    private final JTextArea area = new JTextArea(20, 40);
    private final PrefixSearch englishWords = PrefixSearch.englishWords();
    private PrefixSearch currentProject = new PrefixSearch(Collections
                                                                   .<String>emptyList());


    private final LinkedList<String> suggestions = new LinkedList<String>();
    private int index;

    private static final Comparator<String> comparator = new MyComparator();

    public AddNote(Window window) {
        super(window);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.getInputMap().put(KeyStroke
                                       .getKeyStroke("TAB"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suggestions.isEmpty()) {
                    String text = area.getText();
                    index = text.length() - 1;
                    while (index > 0 && !isBreakChar(text.charAt(index - 1))) {
                        index--;
                    }
                    String prefix = text.substring(index, text.length());
                    suggestions.addAll(currentProject.startsWith(prefix));
                    suggestions.addAll(englishWords.startsWith(prefix));
                    Collections.sort(suggestions, comparator);
                    suggestions.push(prefix);
                }
                suggestions.add(suggestions.poll());
                replace();
            }
        });
        // previous word
        area.getInputMap().put(KeyStroke
                                       .getKeyStroke("shift TAB"), new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (!suggestions.isEmpty()) {
                    suggestions.push(suggestions.pollLast());
                }
                replace();
            }
        });
        area.addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {
                if (!ignore(e)) {
                    suggestions.clear();
                }
            }

            @Override public void keyPressed(KeyEvent e) {
                if (!ignore(e)) {
                    suggestions.clear();
                }
            }

            @Override public void keyReleased(KeyEvent e) {
                if (!ignore(e)) {
                }
            }
        });
    }

    private static boolean ignore(KeyEvent e) {
        return e.getKeyChar() == KeyEvent.VK_TAB || e
                .getKeyCode() == KeyEvent.VK_SHIFT;
    }

    private void replace() {
        String text = area.getText();
        if (index >= 0 && index < text.length()) {
            area.setText(text.substring(0, index)
                                 + suggestions.peek());
        }
    }

    @Override public JComponent createForm() {
        JScrollPane pane = new JScrollPane(area);
        return pane;
    }

    @Override public void prepare() {
        Reconstruction reconstruction = DefaultReconstructionManager
                .getInstance().active();
        List<String> projectWords = new ArrayList<String>();
        for (Metabolite m : reconstruction.metabolome()) {
            projectWords.add(m.getName());
            projectWords.add(m.getAccession());
        }
        for (MetabolicReaction r : reconstruction.reactome()) {
            projectWords.add(r.getName());
            projectWords.add(r.getAccession());
        }
        for (GeneProduct p : reconstruction.proteome()) {
            projectWords.add(p.getName());
            projectWords.add(p.getAccession());
        }
        for (GeneProduct g : reconstruction.proteome()) {
            projectWords.add(g.getName());
            projectWords.add(g.getAccession());
        }
        Collections.shuffle(projectWords);
        currentProject = new PrefixSearch(projectWords);
    }

    @Override public void process() {

        String content = area.getText().trim();
        if (content.isEmpty())
            return;

        CompoundEdit edit = new CompoundEdit();
        for (AnnotatedEntity e : getSelectionController().getSelection()
                .getEntities()) {
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

    static boolean isBreakChar(char c) {
        return c == ' ' || c == '"' || c == '\'';
    }

    static class MyComparator implements Comparator<String> {
        @Override public int compare(String a, String b) {
            if(a.length() < b.length())
                return -1;
            if(a.length() > b.length())
                return +1;
            return a.compareTo(b);
        }
    }
}
