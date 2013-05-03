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
package uk.ac.ebi.mnb.dialog.file;

import uk.ac.ebi.caf.component.complete.PrefixSearch;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicChemicalIdentifier;
import uk.ac.ebi.metingear.edit.entity.AddEntitiesEdit;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditListener;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;


/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 * @name NewMetabolite - 2011.10.04 <br> Class description
 */
public class NewMetabolite extends NewEntity {

    private final PrefixSearch chebi = PrefixSearch.chebi();
    private final LinkedList<String> suggestions = new LinkedList<String>();
    private final Comparator<String> comparator = new MyComparator();

    public NewMetabolite(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits);
        setDefaultLayout();

        setFocusTraversalKeysEnabled(false);
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                              Collections.<AWTKeyStroke>emptySet());
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                              Collections.<AWTKeyStroke>emptySet());

        name.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke
                                                              .getKeyStroke("TAB"),
                                                      new AbstractAction() {
                                                          @Override
                                                          public void actionPerformed(ActionEvent e) {
                                                              if (suggestions.isEmpty()) {
                                                                  suggestions
                                                                          .addAll(chebi.startsWith(name.getText()));
                                                                  Collections
                                                                          .sort(suggestions, comparator);
                                                                  suggestions
                                                                          .push(name.getText());
                                                              }
                                                              suggestions
                                                                      .add(suggestions
                                                                                   .poll());
                                                              name.setText(suggestions
                                                                                   .peek());
                                                          }
                                                      });
        name.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("shift TAB"),
                               new AbstractAction() {
                                   @Override
                                   public void actionPerformed(ActionEvent e) {
                                       if (!suggestions.isEmpty()) {
                                           suggestions.push(suggestions.pollLast());
                                           name.setText(suggestions.peek());
                                       }
                                   }
                               });
        name.addKeyListener(new KeyListener() {
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


    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Please specify detail for a new metabolite");
        return label;
    }

    @Override
    public Identifier getIdentifier() {
        return BasicChemicalIdentifier.nextIdentifier();
    }

    @Override
    public void process() {
        ReconstructionManager manager = DefaultReconstructionManager
                .getInstance();
        if (manager.active() != null) {
            Reconstruction reconstruction = manager.active();
            Metabolite m = DefaultEntityFactory.getInstance()
                                               .newInstance(Metabolite.class, getIdentifier(), getName(), getAbbreviation());
            AddEntitiesEdit edit = new AddEntitiesEdit(reconstruction, EntityMap
                    .singleton(DefaultEntityFactory.getInstance(),
                               m));
            reconstruction.addMetabolite(m);
            addEdit(edit);
        }
    }

    static class MyComparator implements Comparator<String> {
        @Override public int compare(String a, String b) {
            if (a.length() < b.length())
                return -1;
            if (a.length() > b.length())
                return +1;
            return a.compareTo(b);
        }
    }
}
