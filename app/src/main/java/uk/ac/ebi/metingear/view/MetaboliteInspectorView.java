/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
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

package uk.ac.ebi.metingear.view;

import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.UndoableEditListener;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Display information on a metabolite.
 *
 * @author John May
 */
public final class MetaboliteInspectorView extends JPanel {

    private       Metabolite          mtbl;
    private final StructureViewWidget strView;
    private final MetaboliteTableInfo infView;

    public MetaboliteInspectorView(UndoableEditListener editListener) {

        this.strView = new StructureViewWidget(editListener);
        this.infView = new MetaboliteTableInfo(editListener);

        setBackground(Color.WHITE);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.6;
        gbc.weighty = 1;
        gbc.gridx = gbc.gridy = 0;
        add(strView.component(), gbc);
        gbc.weightx = 1;
        gbc.gridx = 1;
        add(infView.component(), gbc);

        infView.addTableModelListener(new TableModelListener() {
            @Override public void tableChanged(TableModelEvent e) {
                if (mtbl != null) {
                      MainView.getInstance().getViewController().update(EntityMap.singleton(DefaultEntityFactory.getInstance(),
                                                                                            mtbl));
                }
            }
        });
    }

    public void setMtbl(Metabolite mtbl) {
        this.mtbl = mtbl;
        strView.setMtbl(mtbl);
        infView.setMtbl(mtbl, rxns(mtbl));
    }
    
    private List<MetabolicReaction> rxns(Metabolite mtbl) {
        Reconstruction reconstruction = DefaultReconstructionManager.getInstance().active();
        if (mtbl == null)
            return Collections.<MetabolicReaction>emptyList();
        List<MetabolicReaction> reactions = new ArrayList<MetabolicReaction>(reconstruction.participatesIn(mtbl));
        return reactions;
    }

    public void clear() {
        this.mtbl = null;
    }
}
