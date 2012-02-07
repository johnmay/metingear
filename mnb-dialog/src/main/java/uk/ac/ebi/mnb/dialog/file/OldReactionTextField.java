/**
 * ReactionTextField.java
 *
 * 2011.10.21
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
package uk.ac.ebi.mnb.dialog.file;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import mnb.io.tabular.parser.ReactionParser;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.reaction.MetabolicParticipant;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import uk.ac.ebi.resource.chemical.BasicChemicalIdentifier;
import uk.ac.ebi.search.FieldType;
import uk.ac.ebi.search.SearchManager;
import uk.ac.ebi.search.SearchableIndex;


/**
 *          ReactionTextField - 2011.10.21 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class OldReactionTextField
        extends JTextField
        implements DocumentListener {

    private static final Logger LOGGER = Logger.getLogger(OldReactionTextField.class);

    private JDialog autocomplete;

    private DefaultListModel model = new DefaultListModel();

    private JList list = new JList(model);

    private static SearchManager search = SearchManager.getInstance();

    private static Pattern REMOVE_TRAILING_CHARS = Pattern.compile("[-=+]+\\s?\\z");

    private static int N_SUGGESTIONS = Preferences.userNodeForPackage(OldReactionTextField.class).getInt("reaction.form.suggestions", 15);

    private static String[] fields = new String[]{FieldType.NAME.getName(), FieldType.ACCESSION.getName(), FieldType.ABBREVIATION.getName()};

    private int previousCount = 0;

    private List<Object> participants = new ArrayList();


    public OldReactionTextField(JDialog parent) {

        autocomplete = new JDialog(parent);
        autocomplete.setFocusable(false);
        autocomplete.setFocusableWindowState(false);
        autocomplete.setUndecorated(true);
        autocomplete.setAlwaysOnTop(true);
        autocomplete.setResizable(false);
        autocomplete.add(new BorderlessScrollPane(list, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        list.setVisibleRowCount(5);
        setFocusTraversalKeysEnabled(false);
        getDocument().addDocumentListener(this);

        setupInputMap();
    }


    /**
     * Adds binds for up, down and tab keys
     */
    private void setupInputMap() {
//        getInputMap().put(KeyStroke.getKeyStroke("DOWN"), new AbstractAction() {
//
//            public void actionPerformed(ActionEvent e) {
//                moveSelectionDown();
//            }
//        });
//        getInputMap().put(KeyStroke.getKeyStroke("UP"), new AbstractAction() {
//
//            public void actionPerformed(ActionEvent e) {
//                moveSelectionUp();
//            }
//        });
//        getInputMap().put(KeyStroke.getKeyStroke("TAB"), new AbstractAction() {
//
//            public void actionPerformed(ActionEvent e) {
//                complete();
//            }
//        });
//        getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), new AbstractAction() {
//
//            public void actionPerformed(ActionEvent e) {
//                autocomplete.setVisible(false);
//            }
//        });
    }


    @Override
    public void setVisible(boolean aFlag) {
        participants = new ArrayList<Object>();
        super.setVisible(aFlag);
    }


    public MetabolicReaction getReaction(Identifier id) {
        throw new UnsupportedOperationException();

//        int nR = counts.get(0); // reac
//        int nP = counts.get(1); // prod
//
//        MetabolicReaction rxn = new MetabolicReaction(id, null, null);
//
//        for (int i = 0; i < nR; i++) {
//            Object p = participants.get(i);
//            if (p instanceof MetaboliteImplementation) {
//                rxn.addReactant(new MetabolicParticipant((MetaboliteImplementation) p));
//            } else {
//                MetaboliteImplementation m = new MetaboliteImplementation(BasicChemicalIdentifier.nextIdentifier(), (String) p, "");
//                rxn.addReactant(new MetabolicParticipant(m));
//            }
//        }
//        for (int i = 0; i < nP; i++) {
//            Object p = participants.get(i + nR);
//            if (p instanceof MetaboliteImplementation) {
//                rxn.addProduct(new MetabolicParticipant((MetaboliteImplementation) p));
//            } else {
//                MetaboliteImplementation m = new MetaboliteImplementation(BasicChemicalIdentifier.nextIdentifier(), (String) p, "");
//                rxn.addProduct(new MetabolicParticipant(m));
//            }
//        }
//
//
//        return rxn;
    }


    public void complete() {
//        AnnotatedEntity entity = (AnnotatedEntity) model.get(list.getSelectedIndex() == -1 ? 0 : list.getSelectedIndex());
//
//        String currString = getCurrentParticipant();
//        String insert = getText().replaceAll(getCurrentParticipant(), entity.getName());
//        System.out.println(insert);
//        setText(insert);
//        int i = getEntityCount() - 1;
//        participants.add(i, entity);
//        previousCount = getEntityCount();
//        autocomplete.setVisible(false);
    }


    public void moveSelectionDown() {
        throw new UnsupportedOperationException();

        //list.setSelectedIndex(list.getSelectedIndex() < model.getSize() ? list.getSelectedIndex() + 1 : list.getSelectedIndex());
    }


    public void moveSelectionUp() {
        throw new UnsupportedOperationException();

        //list.setSelectedIndex(list.getSelectedIndex() > 0 ? list.getSelectedIndex() - 1 : list.getSelectedIndex());
    }


    public void insertUpdate(DocumentEvent e) {
//
//        if (previousCount == getEntityCount()) {
//            return;
//        }
//
//        try {
//            String participant = getCurrentParticipant();
//            int i = getEntityCount() - 1;
//            if (i < participants.size()) {
//                participants.set(i, participant);
//            } else {
//                participants.add(participant);
//            };
//
//            for (Object string : participants) {
//                System.out.print(string.getClass().getSimpleName() + ", ");
//            }
//            System.out.println("");
//
//            SearchableIndex index = search.getCurrentIndex();
//
//            Query query = search.getQuery(fields, participant + "~");
//            query.combine(new Query[]{search.getQuery(FieldType.TYPE, MetaboliteImplementation.BASE_TYPE)});
//
//            Collection<AnnotatedEntity> entities = index.getRankedEntities(query, N_SUGGESTIONS, MetaboliteImplementation.class);
//
//            if (!entities.isEmpty()) {
//                model.removeAllElements();
//                for (AnnotatedEntity entity : entities) {
//                    model.addElement(entity);
//                }
//                autocomplete.pack();
//                if (!autocomplete.isVisible()) {
//                    autocomplete.setVisible(true);
//                }
//            }
//
//            Rectangle location = modelToView(getCaretPosition());
//            Point p = location.getLocation();
//            SwingUtilities.convertPointToScreen(p, this);
//            p.setLocation(p.x + 20, p.y);
//            autocomplete.setLocation(p);
//
//        } catch (BadLocationException ex) {
//            java.util.logging.Logger.getLogger(OldReactionTextField.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            java.util.logging.Logger.getLogger(OldReactionTextField.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParseException ex) {
//            java.util.logging.Logger.getLogger(OldReactionTextField.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }


    public void removeUpdate(DocumentEvent e) {
    }


    public void changedUpdate(DocumentEvent e) {
    }

    private List<Integer> counts = new ArrayList();


    public int getEntityCount() {
        String equation = getText();
        Matcher arrow = ReactionParser.EQUATION_ARROW.matcher(equation);
        String main = equation;
        int count = 0;
        counts.clear();
        if (arrow.find()) {
            String[] sides = ReactionParser.EQUATION_ARROW.split(equation);
            for (int i = 0; i < sides.length; i++) {
                int value = 0;
                for (String participant : ReactionParser.EQUATION_ADDITION.split(sides[i])) {
                    String clean = REMOVE_TRAILING_CHARS.matcher(participant).replaceAll("").trim();
                    count += clean.isEmpty() ? 0 : 1;
                    value += clean.isEmpty() ? 0 : 1;
                }
                counts.add(value);
            }
        } else {
            for (String participant : ReactionParser.EQUATION_ADDITION.split(main)) {
                String clean = REMOVE_TRAILING_CHARS.matcher(participant).replaceAll("").trim();
                count += clean.isEmpty() ? 0 : 1;
            }
        }
        return count;
    }


    public String getCurrentParticipant() {
        String equation = getText();
        Matcher arrow = ReactionParser.EQUATION_ARROW.matcher(equation);
        String side = equation;
        if (arrow.find()) {
            String[] sides = ReactionParser.EQUATION_ARROW.split(equation);
            side = sides[sides.length - 1];
        }
        String[] participants = ReactionParser.EQUATION_ADDITION.split(side);
        String participant = participants[participants.length - 1];

        return REMOVE_TRAILING_CHARS.matcher(participant).replaceAll("").trim();
    }
//    List<TextRange> bins = new ArrayList();
//    LinkedList<Object> plist = new LinkedList();
//
//    public int getEditLocation(int offset) {
//        System.out.println("offset:" + offset);
//        Iterator<TextRange> r = bins.iterator();
//        while (r.hasNext()) {
//            TextRange range = r.next();
//            if (range.contains(offset)) {
//                return bins.indexOf(range);
//            }
//        }
//        return -1;
//    }
//
//    public void calculateBins() {
//        String equation = getText();
//        Matcher arrowMatcher = ReactionParser.EQUATION_ARROW.matcher(equation);
//
//
//        if (arrowMatcher.find()) {
//            String arrow = "";
//            for (int j = 1; j < arrowMatcher.groupCount(); j++) {
//                if (arrowMatcher.group(j) != null) {
//                    arrow = arrowMatcher.group(j);
//                }
//            }
//
//            String left = equation.substring(0, arrowMatcher.start());
//            String right = equation.substring(arrowMatcher.start() + arrow.length(), equation.length());
//
//            System.out.println("left: " + left);
//            System.out.println("right: " + right);
//
//            bins.clear();
//            bins.addAll(bins(left, 0));
//            bins.addAll(bins(right, left.length()));
//
//        } else {
//            bins.clear();
//            bins.addAll(bins(equation, 0));
//        }
//
//        System.out.println("bins: " + bins);
//
//    }
//
//    private List<TextRange> bins(String equ, int start) {
//        String substring = equ;
//        List<TextRange> bins = new ArrayList<TextRange>();
//        Matcher m = ReactionParser.EQUATION_ADDITION.matcher(substring);
//        while (m.find()) {
//            int ind = m.start();
//            bins.add(new TextRange(start, start + ind));
//            start = start + ind + 1;
//            substring = substring.substring(start);
//            m = ReactionParser.EQUATION_ADDITION.matcher(substring);
//        }
//        bins.add(new TextRange(start, start + substring.length()));
//        return bins;
//    }
}
//class TextRange {
//
//    final int start;
//    final int end;
//
//    public TextRange(int start, int end) {
//        this.start = start;
//        this.end = end;
//    }
//
//    @Override
//    public String toString() {
//        return start + "," + end;
//    }
//
//    public boolean contains(int offset) {
//        return offset >= start && offset <= end;
//    }
//}
