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

import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.metingear.view.ControlDialog;
import uk.ac.ebi.metingear.view.PlugableDialog;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;

import java.util.Arrays;
import java.util.List;

/**
 * @author John May
 */
public class AssociateReactionsPlugin implements PlugableDialog {

    @Override public List<String> getMenuPath() {
        return Arrays.asList("Tools", "Associate");
    }

    @Override public Class<? extends ControlDialog> getDialogClass() {
        return AssociateReactions.class;
    }

    @Override public ContextResponder getContext() {
        return new ContextResponder() {
            @Override
            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return active != null
                        && active.reactome().size() > 0
                        && active.proteome().size() > 0;
            }
        };
    }
}
