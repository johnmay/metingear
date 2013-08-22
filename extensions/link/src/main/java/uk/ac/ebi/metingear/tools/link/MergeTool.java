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
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.metingear.AppliableEdit;
import uk.ac.ebi.metingear.edit.entity.MergeMetaboliteEdit;
import uk.ac.ebi.metingear.view.AbstractControlDialog;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.undo.CompoundEdit;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author John May
 */
public final class MergeTool extends AbstractControlDialog {

    private final JComboBox mergeKey = ComboBoxFactory
            .newComboBox((Object[]) Key.values());

    public MergeTool(Window window) {
        super(window);
    }

    @Override public JComponent createForm() {
        JComponent component = super.createForm();
        component.setLayout(new FormLayout("p, 4dlu, p",
                                           "p"));

        CellConstraints cc = new CellConstraints();

        component.add(getLabel("mergeBy"), cc.xy(1, 1));
        component.add(mergeKey, cc.xy(3, 1));
        return component;
    }

    @Override public void process() {
        Reconstruction reconstruction = DefaultReconstructionManager
                .getInstance().active();
        Key key = (Key) mergeKey.getSelectedItem();
        Multimap<String, Metabolite> metabolites = HashMultimap
                .create(reconstruction.metabolome().size(), 2);
        for (Metabolite m : reconstruction.metabolome()) {
            metabolites.put(key.key(m), m);
        }

        CompoundEdit edit = new CompoundEdit();

        Set<String> keys = metabolites.keySet();
        for (String k : keys) {
            List<Metabolite> ms = new ArrayList<Metabolite>(metabolites.get(k));
            if (ms.size() > 1) {
                AppliableEdit subedit = new MergeMetaboliteEdit(ms, union(ms.get(0)), reconstruction);
                subedit.apply();
                edit.addEdit(subedit);
            }
        }

        edit.end();
        addEdit(edit);
    }

    private Metabolite union(Metabolite src) {
        EntityFactory factory = DefaultEntityFactory.getInstance();
        Metabolite dest = factory.metabolite();
        dest.setName(src.getName());
        dest.setAbbreviation(src.getAbbreviation());
        dest.setIdentifier(src.getIdentifier());
        dest.setRating(src.getRating());
        dest.addAnnotations(src.getAnnotations());
        return dest;
    }

    /**
     * How to key entities
     */
    enum Key {
        IDENTIFIER("Identifier") {
            @Override String key(AnnotatedEntity entity) {
                return entity.getIdentifier().getAccession();
            }
        },
        ABBREVIATION("Abbreviation") {
            @Override String key(AnnotatedEntity entity) {
                return entity.getAbbreviation();
            }
        },
        NAME("Name") {
            @Override String key(AnnotatedEntity entity) {
                return entity.getName();
            }
        };


        private final String displayName;

        Key(String displayName) {
            this.displayName = displayName;
        }

        abstract String key(AnnotatedEntity entity);


        @Override public String toString() {
            return displayName;
        }
    }
}
