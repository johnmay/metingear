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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import mnb.io.tabular.NamedEntityResolver;
import mnb.io.tabular.parser.ReactionParser;
import mnb.io.tabular.parser.UnparsableReactionError;
import mnb.io.tabular.preparse.PreparsedReaction;
import mnb.io.tabular.type.ReactionColumn;
import org.apache.log4j.Logger;
import org.apache.lucene.search.TermQuery;
import uk.ac.ebi.caf.component.ReplacementHandler;
import uk.ac.ebi.caf.component.SuggestionField;
import uk.ac.ebi.caf.component.SuggestionHandler;
import uk.ac.ebi.caf.component.complete.PrefixSearch;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.Compartment;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Membrane;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicReactionIdentifier;
import uk.ac.ebi.mdk.domain.tool.AutomaticCompartmentResolver;
import uk.ac.ebi.metingear.edit.entity.AddEntitiesEdit;
import uk.ac.ebi.metingear.search.FieldType;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 * @name NewMetabolite - 2011.10.04 <br> Class description
 */
public class NewReaction extends NewEntity {

    private static final Logger LOGGER = Logger.getLogger(NewReaction.class);
    private SuggestionField equation;
    private static CellConstraints cc = new CellConstraints();

    // stuff for lucene
    private static final String LUCENE_ESCAPE_CHARS = "[\\\\+\\-\\!\\(\\)\\:\\^\\[\\]\\{\\}\\~\\*\\?]";

    private static final Pattern LUCENE_PATTERN = Pattern.compile(
        LUCENE_ESCAPE_CHARS);

    private static final String REPLACEMENT_STRING = "\\\\$0";

    private        TermQuery typeFilter = new TermQuery(FieldType.TYPE.getTerm(
        DefaultEntityFactory.getInstance().getRootClass(
            Metabolite.class).getSimpleName()));
    private static String[]  fields     = new String[]{FieldType.NAME.getName(),
                                                       FieldType.ABBREVIATION.getName()};

    private final PrefixSearch compartments = PrefixSearch.forStrings(
        compartments());
    private final PrefixSearch chebi        = PrefixSearch.chebi();
    private PrefixSearch current;

    public NewReaction(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits);
        setDefaultLayout();
        setFocusTraversalKeysEnabled(false);
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                              Collections.<AWTKeyStroke>emptySet());
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                              Collections.<AWTKeyStroke>emptySet());
    }

    private static List<String> compartments() {
        List<String> compartments = new ArrayList<String>();
        for (final Compartment compartment : Organelle.values()) {
            compartments.add("[" + compartment.getAbbreviation() + "]");
            compartments.add("[" + compartment.getDescription() + "]");
        }
        for (final Compartment compartment : Membrane.values()) {
            compartments.add("[" + compartment.getAbbreviation() + "]");
            compartments.add("[" + compartment.getDescription() + "]");
        }
        return compartments;
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Please specify detail for a new reaction");
        return label;
    }

    @Override
    public JPanel getForm() {

        JPanel panel = super.getForm();

        equation = new SuggestionField(this, 80,
                                       new ReactionSuggestionHandler(),
                                       new ReplacementHandler());


        FormLayout layout = (FormLayout) panel.getLayout();
        layout.appendRow(new RowSpec(Sizes.DLUY4));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));
        JLabel label = LabelFactory.newFormLabel("Reaction Equation",
                                                 "Enter a text equation for your reaction (e.g. 1 A + B -> C [e])");
        label.setHorizontalAlignment(JLabel.LEADING);
        panel.add(label, cc.xyw(1, layout.getRowCount(),
                                layout.getColumnCount()));
        layout.appendRow(new RowSpec(Sizes.DLUY4));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));
        panel.add(equation, cc.xyw(1, layout.getRowCount(),
                                   layout.getColumnCount(),
                                   CellConstraints.FILL, CellConstraints.FILL));

        return panel;
    }

    @Override
    public Identifier getIdentifier() {
        return BasicReactionIdentifier.nextIdentifier();
    }

    @Override public void prepare() {
        List<String> names = new ArrayList<String>();
        Reconstruction reconstruction = DefaultReconstructionManager.getInstance().active();
        for (Metabolite m : reconstruction.metabolome()) {
            names.add(m.getName());
        }
        current = PrefixSearch.forStrings(names);
    }

    @Override
    public void process() {

        ReconstructionManager manager = DefaultReconstructionManager.getInstance();
        if (manager.active() != null) {
            Reconstruction reconstruction = manager.active();

            ReactionParser parser = new ReactionParser(
                new NamedEntityResolver(), new AutomaticCompartmentResolver());
            PreparsedReaction ppRxn = new PreparsedReaction();

            ppRxn.addValue(ReactionColumn.ABBREVIATION, getAbbreviation());
            ppRxn.addValue(ReactionColumn.DESCRIPTION, getName());
            ppRxn.addValue(ReactionColumn.EQUATION, equation.getText());

            try {
                MetabolicReaction reaction = parser.parseReaction(ppRxn);
                reaction.setIdentifier(getIdentifier());
                reconstruction.addReaction(reaction);
                AddEntitiesEdit edit = new AddEntitiesEdit(reconstruction,
                                                           EntityMap.singleton(
                                                               DefaultEntityFactory.getInstance(),
                                                               reaction));
                addEdit(edit);
            } catch (UnparsableReactionError ex) {
                addMessage(new ErrorMessage(
                    "Malformed reaction: " + ex.getMessage()));
            }

        }


    }

    private class Edit {
        private String content;
        private int    start;
        private int    end;

        private Edit(String content, int start, int end) {
            this.content = content;
            this.start = start;
            this.end = end;
        }
    }

    private Edit NO_EDIT = new Edit("", 0, 0);

    private static boolean breakChar(char c) {
        return c == ' ' || c == '-' || c == '<' || c == '>';
    }

    private Edit getEdit(DocumentEvent e) {
        String text = equation.getText();
        int end = e.getOffset() + e.getLength();
        if (end == text.length() || breakChar(text.charAt(end))) {
            int start = end - 1;
            if (text.charAt(start) == ' ') return NO_EDIT;
            while (start > 0 && text.charAt(start - 1) != ' ') {
                start--;
            }
            if (end - start < 3 && text.charAt(start) != '[') return NO_EDIT;
            String prefix = text.substring(start, end);
            return new Edit(prefix, start, end);
        }
        return NO_EDIT;
    }


    private class ReactionSuggestionHandler extends SuggestionHandler {


        @Override
        public Collection<Object> getSuggestions(DocumentEvent e) {

            Edit e2 = getEdit(e);
            String text = equation.getText();
            String prefix = text.substring(0, e2.start);
            String suffix = text.substring(e2.end, text.length());

            Collection<Object> suggestions = new LinkedHashSet<Object>();

            List<String> compartmentSuggestions = compartments.startsWith(
                e2.content);
            List<String> currentSuggestions = current.startsWith(e2.content);
            List<String> chebiSuggestions = chebi.startsWith(e2.content);

            Collections.sort(compartmentSuggestions,
                             PrefixSearch.suggestionOrder());
            Collections.sort(currentSuggestions,
                             PrefixSearch.suggestionOrder());
            Collections.sort(chebiSuggestions, PrefixSearch.suggestionOrder());


            for (final String suggestion : compartmentSuggestions) {
                if (prefix.length() == 0) {
                    suggestions.add(suggestion + ": " + suffix);
                } else {
                    suggestions.add(prefix + suggestion + suffix);
                }
            }
            for (final String suggestion : currentSuggestions) {
                suggestions.add(prefix + suggestion + suffix);
            }
            for (final String suggestion : chebiSuggestions) {
                suggestions.add(prefix + suggestion + suffix);
            }

            return suggestions;
        }

        // expand the range of the edit to the current metabolite
        private int[] expand(DocumentEvent event) throws BadLocationException {

            Document document = event.getDocument();
            int offset = event.getOffset() + 1;
            String text = document.getText(0, document.getLength());


            for (int i = offset + 1; i >= 0; i--) {

                int start = Math.max(0, i - 1);
                int end = Math.min(start + (i > 0 ? 3 : 2), text.length());

                String substring = text.substring(start, end);

                // ends of field
                if (substring.length() == 3) {
                    if (substring.equals(" + ")) {
                        return end >= offset ? new int[]{offset, offset}
                                             : new int[]{end, offset};
                    } else if (substring.matches(
                        "<[ -=]>") || substring.matches(
                        "<[ -=]{2}") || substring.matches(
                        "[ -=]{2}>") || substring.matches(
                        "<[ -=]{1}\\s") || substring.matches("[ -=]{1}>\\s")) {
                        return end >= offset ? new int[]{offset, offset}
                                             : new int[]{end, offset};

                    }
                }
                // catches end cases
                else if (substring.length() == 2) {
                    if (((substring.equals(
                        "+ ") && i != offset && i + 1 != offset) || (substring.equals(
                        " +") && i != 0))) {
                        return new int[]{end, offset};
                    } else if (substring.matches("<[ -=]") || substring.matches(
                        "[ -=]>") || substring.matches(
                        " <") || substring.matches("> ")) {
                        return end >= offset ? new int[]{offset, offset}
                                             : new int[]{end, offset};
                    }
                }

            }

            return new int[]{0, offset};

        }

    }


}
