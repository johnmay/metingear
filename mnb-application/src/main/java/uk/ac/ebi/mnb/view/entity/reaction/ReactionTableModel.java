/**
 * ReactionTableModel.java
 *
 * 2011.09.26
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
package uk.ac.ebi.mnb.view.entity.reaction;

import java.util.ArrayList;
import java.util.Arrays;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.view.entity.DataType;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTableModel;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.Subsystem;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.AbstractAnnotatedEntity;
import uk.ac.ebi.interfaces.AnnotatedEntity;

/**
 *          ReactionTableModel – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReactionTableModel extends AbstractEntityTableModel {

    private static final Logger LOGGER = Logger.getLogger(ReactionTableModel.class);
    private static final ColumnDescriptor[] DEFAULT = new ColumnDescriptor[]{
        new ColumnDescriptor("Reversibility", null,
        DataType.FIXED,
        String.class),
        new ColumnDescriptor("Equation", null,
        DataType.FIXED,
        String.class),
        new ColumnDescriptor(new EnzymeClassification()),
        new ColumnDescriptor(new Subsystem())};

    public ReactionTableModel() {
        super();
        addColumns(Arrays.asList(DEFAULT));
    }

    @Override
    public void loadComponents() {

        Reconstruction project = ReconstructionManager.getInstance().getActiveReconstruction();

        if (project != null) {
            setEntities(new ArrayList<AnnotatedEntity>(project.getReactions()));
        }

    }

    @Override
    public Object getFixedType(AnnotatedEntity component, String name) {

        Reaction rxn = (Reaction) component;

        if (name.equals(DEFAULT[0].getName())) {
            return rxn.getReversibility();
        } else if (name.equals(DEFAULT[1].getName())) {
            return rxn.toString();
        }

        return "NA";

    }
}
