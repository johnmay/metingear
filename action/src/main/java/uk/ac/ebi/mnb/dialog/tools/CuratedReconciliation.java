/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
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
package uk.ac.ebi.mnb.dialog.tools;

import uk.ac.ebi.mnb.dialog.tools.curate.MetaboliteCurator;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.service.DefaultServiceManager;
import uk.ac.ebi.mnb.interfaces.MainController;

import javax.swing.*;

/**
 * ResolveMissingInfo 2012.01.11
 *
 * @author johnmay
 * @author $Author$ (this version)
 *         <p/>
 *         Class provides resolution to small molecule metabolites that are not assigned
 *         to a database or structure
 * @version $Rev$ : Last Changed $Date$
 */
public class CuratedReconciliation
        extends DelayedBuildAction {

    private MetaboliteCurator dialog;
    private MainController controller;

    public CuratedReconciliation(MainController controller) {
        super(CuratedReconciliation.class.getSimpleName());
        this.controller = controller;
    }

    @Override
    public void buildComponents() {
        dialog = new MetaboliteCurator((JFrame) controller, DefaultServiceManager.getInstance(), controller.getUndoManager());
    }

    @Override
    public void activateActions() {

        EntityCollection manager = controller.getViewController().getSelection();

        dialog.setSkipall(false); // reset the skip-all flag

        for (Metabolite metabolite : manager.get(Metabolite.class)) {
            dialog.setup(metabolite);
            dialog.setVisible(true);
        }

        controller.update(manager);

    }


}
