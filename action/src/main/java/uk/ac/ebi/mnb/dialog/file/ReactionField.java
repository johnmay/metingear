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

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.ui.component.table.accessor.EntityValueAccessor;
import uk.ac.ebi.mdk.ui.component.table.accessor.NameAccessor;
import uk.ac.ebi.metingear.search.FieldType;
import uk.ac.ebi.metingear.search.SearchManager;
import uk.ac.ebi.metingear.search.SearchableIndex;
import uk.ac.ebi.mnb.dialog.popup.AutoComplete;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;


/**
 * ReactionField - 2011.12.02 <br>
 * Class extends JTextField and allows auto-competition of metabolite names
 * and parsing of a reaction
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class ReactionField
        extends JTextField {

    private static final Logger LOGGER = Logger.getLogger(ReactionField.class);

    private EntityValueAccessor accessor = new NameAccessor(); // list uses names

    private AutoComplete autocomplete;

    private TermQuery typeFilter = new TermQuery(FieldType.TYPE.getTerm(DefaultEntityFactory.getInstance().getRootClass(Metabolite.class).getSimpleName()));

    private static String[] fields = new String[]{FieldType.NAME.getName(),
                                                  FieldType.ABBREVIATION.getName()};

    private Replacement r = null;

    private DocumentListener listener;

    private static final String LUCENE_ESCAPE_CHARS = "[\\\\+\\-\\!\\(\\)\\:\\^\\[\\]\\{\\}\\~\\*\\?]";

    private static final Pattern LUCENE_PATTERN = Pattern.compile(LUCENE_ESCAPE_CHARS);

    private static final String REPLACEMENT_STRING = "\\\\$0";


    public ReactionField(final JDialog dialog) {

        autocomplete = new AutoComplete(dialog);

        listener = new ReactionFieldListener();

        setFocusTraversalKeysEnabled(false);

        getDocument().addDocumentListener(listener);

        getCaret().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                try {
                    Rectangle location = modelToView(getCaretPosition());
                    Point p = location.getLocation();
                    SwingUtilities.convertPointToScreen(p, dialog);
                    p.setLocation(p.x + 20, p.y);
                    autocomplete.setLocation(p);
                } catch (BadLocationException ex) {
                    LOGGER.error("Unable to set location of autocomplete dialog");
                }
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("DOWN"), new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                autocomplete.selectPrevious();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("UP"), new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                autocomplete.selectNext();
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("TAB"), new AbstractAction() {

            public void actionPerformed(ActionEvent e) {

                String item = autocomplete.getSelectedItem();

                // disengage listener whilst updating
                getDocument().removeDocumentListener(listener);

                String text = getText();
                if (r.start > 0 && r.end < text.length()) {
                    setText(text.substring(0, r.start) + item + text.substring(r.end, text.length()));
                } else if (r.start > 0) {
                    setText(text.substring(0, r.start) + item);
                } else if (r.end < text.length()) {
                    setText(item + text.substring(r.end, text.length()));
                } else {
                    setText(item);
                }

                getDocument().addDocumentListener(listener);

                autocomplete.setVisible(false);

            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                autocomplete.setVisible(false);
            }
        });


    }


    private class ReactionFieldListener
            implements DocumentListener {

        public void changedUpdate(DocumentEvent e) {
        }


        public void insertUpdate(DocumentEvent e) {
            try {
                // find the start of this metabolite name
                Document doc = e.getDocument();
                String text = doc.getText(0, doc.getLength());
                r = getReplacement(text, e.getOffset() + 1); // get replacement expects index after that which was just inserted

                String inserted = e.getDocument().getText(e.getOffset(), e.getLength());

                // ignore space inserts
                if (inserted.equals(" ")) {
                    return;
                }

                if (!r.text.isEmpty()) {

                    // search metabolite name and offer suggestions
                    SearchableIndex index = SearchManager.getInstance().getCurrentIndex();

                    String searchableText =
                            LUCENE_PATTERN.matcher(r.text).replaceAll(REPLACEMENT_STRING);

                    Query baseQuery = SearchManager.getInstance().getQuery(fields, searchableText + "~");

                    BooleanQuery query = new BooleanQuery();
                    query.add(baseQuery, BooleanClause.Occur.MUST);
                    query.add(typeFilter, BooleanClause.Occur.MUST);


                    List<AnnotatedEntity> entities = index.getRankedEntities(baseQuery,
                                                                             25,
                                                                             Metabolite.class);

                    LOGGER.info("Suggestions for '" + r.text + "' :" + entities);

                    // replace name with suggestions
                    if (!entities.isEmpty()) {
                        autocomplete.setItems(entities, accessor);
                        autocomplete.repaint();
                        autocomplete.pack();
                        autocomplete.setVisible(true);
                    }

                } else {
                    autocomplete.setVisible(false);
                    autocomplete.clear();
                }


            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(ReactionField.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                LOGGER.error("Unable to search suggested query");
            } catch (BadLocationException ex) {
                LOGGER.error("Unable to get substring from field: " + ex.getMessage());
            }

        }


        public void removeUpdate(DocumentEvent e) {
            //    throw new UnsupportedOperationException("Not supported yet.");
        }
    }


    public static Replacement getReplacement(String text, int offset) {

        for (int i = offset; i >= 0; i--) {
            int start = Math.max(0, i - 1);
            int end = Math.min(start + (i > 0 ? 3 : 2), text.length());

            String substring = text.substring(start, end);

            // ends of field
            if (substring.length() == 3) {
                if (substring.equals(" + ")) {
                    return end >= offset ? new Replacement(offset, offset, "")
                                         : new Replacement(end, offset, text.substring(end, offset));
                } else if (substring.matches("<[ -=]>")
                        || substring.matches("<[ -=]{2}")
                        || substring.matches("[ -=]{2}>")) {
                    return end >= offset ? new Replacement(offset, offset, "")
                                         : new Replacement(end, offset, text.substring(end, offset));
                }
            } else if (substring.length() == 2) {
                if (((substring.equals("+ ") && i != offset && i + 1 != offset)
                        || (substring.equals(" +") && i != 0))) {
                    return new Replacement(end, offset, text.substring(end, offset));
                } else if (substring.matches("<[ -=]")
                        || substring.matches("[ -=]>")
                        || substring.matches(" <")
                        || substring.matches("> ")) {
                    return end >= offset ? new Replacement(offset, offset, "")
                                         : new Replacement(end, offset, text.substring(end, offset));
                }
            }


        }
        return new Replacement(0, offset, text.substring(0, offset));
    }
}


class Replacement {

    public final int start;

    public final int end;

    public final String text;


    public Replacement(int start, int end, String text) {
        this.start = start;
        this.end = end;
        this.text = text;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Replacement other = (Replacement) obj;
        if (this.start != other.start) {
            return false;
        }
        if (this.end != other.end) {
            return false;
        }
        if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
            return false;
        }
        return true;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.start;
        hash = 53 * hash + this.end;
        hash = 53 * hash + (this.text != null ? this.text.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        return "(" + start + "," + end + "):" + text;
    }
}
