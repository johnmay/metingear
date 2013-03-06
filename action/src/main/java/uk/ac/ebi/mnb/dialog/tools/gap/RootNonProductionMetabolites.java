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
package uk.ac.ebi.mnb.dialog.tools.gap;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.mdk.domain.entity.metabolite.CompartmentalisedMetabolite;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.matrix.StoichiometricMatrix;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.optimise.SimulationUtil;
import uk.ac.ebi.optimise.gap.GapFind;

import java.awt.event.ActionEvent;

/**
 *          NonProductionMetabolites - 2011.12.05 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class RootNonProductionMetabolites
        extends GeneralAction {

    private static final Logger LOGGER = Logger.getLogger(RootNonProductionMetabolites.class);
    private MainController controller;

    public RootNonProductionMetabolites(MainController controller) {
        super(RootNonProductionMetabolites.class.getSimpleName());
        this.controller = controller;
    }

    public void actionPerformed(ActionEvent e) {

        try {
            Reconstruction active = DefaultReconstructionManager.getInstance().getActive();
            StoichiometricMatrix<CompartmentalisedMetabolite,?> s = active.getMatrix();



            SimulationUtil.setup(); // make sure the paths are set

            GapFind gf = new GapFind(active.getMatrix());

            EntityCollection manager = controller.getViewController().getSelection();
            manager.clear();

            Integer[] indices = gf.getRootUnproducedMetabolites();
            LOGGER.debug("Root Non-Production Metabolites: " + Joiner.on(", ").join(indices));

            for (Integer i : indices) {
                Metabolite metabolite = s.getMolecule(i).metabolite;
                manager.add(metabolite);
            }

            controller.getViewController().setSelection(manager);

        } catch (Exception ex) {
            controller.getMessageManager().addReport(new ErrorMessage(ex.getLocalizedMessage()));

        } catch (UnsatisfiedLinkError ex) {
            controller.getMessageManager().addReport(new ErrorMessage(
                    "Please ensure the CPLEX library path is set correctly"));
        }
    }
}
