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
import net.sf.jniinchi.INCHI_RET;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.smiles.SmilesGenerator;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
        Key type = (Key) mergeKey.getSelectedItem();
        Multimap<String, Metabolite> metabolites = HashMultimap.create(reconstruction.metabolome().size(), 2);

        for (Metabolite m : reconstruction.metabolome()) {
            for (String key : type.key(m)) {
                metabolites.put(key, m);
            }
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
            @Override Collection<String> key(AnnotatedEntity entity) {
                return Collections.singleton(entity.getIdentifier().getAccession());
            }
        },
        ABBREVIATION("Abbreviation") {
            @Override Collection<String> key(AnnotatedEntity entity) {
                return Collections.singleton(entity.getAbbreviation().toLowerCase(Locale.ENGLISH));
            }
        },
        NAME("Name") {
            @Override Collection<String> key(AnnotatedEntity entity) {
                return Collections.singleton(entity.getName().toLowerCase(Locale.ENGLISH));
            }
        },
        ABBREVIATION_CASE_SENS("Abbreviation (case sensative)") {
            @Override Collection<String> key(AnnotatedEntity entity) {
                return Collections.singleton(entity.getAbbreviation());
            }
        },
        NAME_CASE_SENS("Name (case sensative)") {
            @Override Collection<String> key(AnnotatedEntity entity) {
                return Collections.singleton(entity.getName());
            }
        },
        INCHI_KEY("InChI-Key") {
            @Override Collection<String> key(AnnotatedEntity entity) {
                Set<String> inchiKeys = new HashSet<String>();                    
                for (ChemicalStructure cs : entity.getAnnotations(ChemicalStructure.class)) {
                    try {
                        InChIGeneratorFactory igf = InChIGeneratorFactory.getInstance();
                        InChIGenerator ig = igf.getInChIGenerator(cs.getStructure());
                        if (ig.getReturnStatus() != INCHI_RET.OKAY && ig.getReturnStatus() != INCHI_RET.WARNING)
                            continue;
                        inchiKeys.add(ig.getInchiKey());
                    } catch (Exception e) {

                    }
                }
                return inchiKeys;
            }
        },
        UNIQUE_SMILES("Unique SMILES (non-stereo)") {
            @Override Collection<String> key(AnnotatedEntity entity) {
                Set<String> smis = new HashSet<String>();
                for (ChemicalStructure cs : entity.getAnnotations(ChemicalStructure.class)) {
                    try {
                        smis.add(SmilesGenerator.unique().create(cs.getStructure()));
                    } catch (Exception e) {

                    }
                }
                return smis;
            }
        };


        private final String displayName;

        Key(String displayName) {
            this.displayName = displayName;
        }

        abstract Collection<String> key(AnnotatedEntity entity);


        @Override public String toString() {
            return displayName;
        }
    }
}
