/**
 * GapAnalysis.java
 *
 * 2011.12.02
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
package uk.ac.ebi.mnb.menu;

import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.entities.EntityCollection;
import uk.ac.ebi.interfaces.entities.Reconstruction;
import uk.ac.ebi.mdk.domain.tool.ReconstructionManager;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import uk.ac.ebi.mnb.dialog.tools.gap.NonConsumptionMetabolites;
import uk.ac.ebi.mnb.dialog.tools.gap.NonProductionMetabolites;
import uk.ac.ebi.mnb.dialog.tools.gap.RootNonProductionMetabolites;
import uk.ac.ebi.mnb.dialog.tools.gap.TerminalNonConsumptionMetabolites;
import uk.ac.ebi.mnb.interfaces.MainController;
import uk.ac.ebi.optimise.SimulationUtil;

/**
 *          GapAnalysis - 2011.12.02 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class GapAnalysis extends ContextMenu {

    private static final Logger LOGGER = Logger.getLogger(GapAnalysis.class);
    private ContextMenu find;
    private ContextMenu fill;

    public GapAnalysis(MainController controller) {
        super("Gap Analysis", controller);

        find = new FindGap(controller);
        fill = new FillGap(controller);

        add(find);
        add(fill);

    }

    @Override
    public void updateContext() {
        super.updateContext();
        find.updateContext();
        fill.updateContext();
    }

    class FindGap extends ContextMenu {

        public FindGap(final MainController controller) {
            super("Find", controller);

            add(new NonProductionMetabolites(controller), new ContextResponder() {
                public boolean getContext(ReconstructionManager reconstructions,
                                          Reconstruction active,
                                          EntityCollection selection) {                  
                    return active != null && active.hasMatrix() && SimulationUtil.isAvailable();
                }
            });
            add(new RootNonProductionMetabolites(controller), new ContextResponder() {
                public boolean getContext(ReconstructionManager reconstructions,
                                          Reconstruction active,
                                          EntityCollection selection) {
                    return active != null && active.hasMatrix() && SimulationUtil.isAvailable();
                }
            });
            add(new NonConsumptionMetabolites(controller), new ContextResponder() {
                public boolean getContext(ReconstructionManager reconstructions,
                                          Reconstruction active,
                                          EntityCollection selection) {
                    return active != null && active.hasMatrix() && SimulationUtil.isAvailable();
                }
            });
            add(new TerminalNonConsumptionMetabolites(controller), new ContextResponder() {
                public boolean getContext(ReconstructionManager reconstructions,
                                          Reconstruction active,
                                          EntityCollection selection) {
                    return active != null && active.hasMatrix() && SimulationUtil.isAvailable();
                }
            });
            
            

        }
    }

    class FillGap extends ContextMenu {

        public FillGap(MainController controller) {
            super("Fill", controller);
        }
    }
}
