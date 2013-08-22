package uk.ac.ebi.metingear.tools.link;/*
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.mdk.domain.annotation.Locus;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Reaction;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.domain.identifier.classification.ECNumber;
import uk.ac.ebi.metingear.EditBuilder;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.core.WarningMessage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author John May
 */
public final class AssociateReactions extends AbstractControlDialog {

    private final JComboBox reactionKeyComboBox = ComboBoxFactory
            .newComboBox((Object[]) Key.values());
    private final JComboBox productKeyComboBox = ComboBoxFactory
            .newComboBox((Object[]) Key.values());

    public AssociateReactions(Window window) {
        super(window);
    }

    @Override public JComponent createForm() {
        JComponent component = super.createForm();
        component.setLayout(new FormLayout("p, 4dlu, p",
                                           "p"));

        CellConstraints cc = new CellConstraints();

        Box mapping = Box.createHorizontalBox();
        mapping.add(reactionKeyComboBox);
        mapping.add(LabelFactory.newLabel(" to product "));
        mapping.add(productKeyComboBox);
        component.add(getLabel("mapWith"), cc.xy(1, 1));
        component.add(mapping, cc.xy(3, 1));

        return component;
    }

    @Override public void process() {

        Reconstruction reconstruction = DefaultReconstructionManager
                .getInstance().active();

        Key reactionKey = (Key) reactionKeyComboBox.getSelectedItem();
        Key productKey = (Key) productKeyComboBox.getSelectedItem();

        int n = reconstruction.reactome().size();

        Multimap<String, Reaction> reactionMap = HashMultimap.create(n, 1);

        for (Reaction reaction : reconstruction.reactome()) {
            for (String key : reactionKey.keys(reaction)) {
                reactionMap.put(key, reaction);
            }
        }

        if (reactionMap.isEmpty()) {
            addReport(new WarningMessage("no reactions were keyed using '" + reactionKey + "', check configuration"));
            return;
        }

        EditBuilder builder = new EditBuilder(reconstruction);

        for (GeneProduct gp : reconstruction.proteome()) {
            for (String key : productKey.keys(gp)) {
                for (Reaction reaction : reactionMap.get(key)) {
                    if(reaction instanceof MetabolicReaction){
                        builder.associate(gp).with((MetabolicReaction) reaction);
                    }
                }
            }
        }

        addEdit(builder.apply());
    }

    /**
     * How to key entities
     */
    enum Key {
        IDENTIFIER("Identifier") {
            @Override List<String> keys(AnnotatedEntity entity) {
                return nonNullSingleton(entity.getIdentifier());
            }
        },
        ABBREVIATION("Abbreviation") {
            @Override List<String> keys(AnnotatedEntity entity) {
                return nonNullSingleton(entity.getAbbreviation());
            }
        },
        NAME("Name") {
            @Override List<String> keys(AnnotatedEntity entity) {
                return nonNullSingleton(entity.getName());
            }
        },
        EC("E.C.") {
            @Override List<String> keys(AnnotatedEntity entity) {
                Collection<CrossReference> xrefs = entity
                        .getAnnotationsExtending(CrossReference.class);
                List<String> ecnumbers = new ArrayList<String>(xrefs.size());
                for (CrossReference xref : xrefs) {
                    if (xref.getIdentifier() instanceof ECNumber) {
                        ecnumbers.add(xref.getIdentifier().getAccession());
                    }
                }
                return ecnumbers;
            }
        },
        LOCUS("Locus") {
            @Override List<String> keys(AnnotatedEntity entity) {
                List<String> keys = new ArrayList<String>();
                for (Locus annotation : entity.getAnnotations(Locus.class)) {
                    keys.add(annotation.getValue());
                }
                return keys;
            }
        };

        private final String displayName;

        Key(String displayName) {
            this.displayName = displayName;
        }

        abstract List<String> keys(AnnotatedEntity entity);


        @Override public String toString() {
            return displayName;
        }
    }


    private static List<String> nonNullSingleton(String obj) {
        List<String> xs = new ArrayList<String>(1);
        if (obj != null)
            xs.add(obj.toString());
        return xs;
    }

    private static List<String> nonNullSingleton(Identifier id) {
        List<String> xs = new ArrayList<String>(1);
        if (id != null)
            xs.add(id.getAccession());
        return xs;
    }
}
