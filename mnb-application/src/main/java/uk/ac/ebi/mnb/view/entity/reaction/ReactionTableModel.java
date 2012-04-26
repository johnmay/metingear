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

import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.Subsystem;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Reaction;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTableModel;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.DataType;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * ReactionTableModel – 2011.09.26 <br>
 * Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
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

        Reconstruction project = DefaultReconstructionManager.getInstance().getActive();

        setEntities(project != null ? project.getReactome() : new ArrayList());

    }


    @Override
    public Object getFixedType(AnnotatedEntity component, String name) {

        Reaction rxn = (Reaction) component;

        if (name.equals(DEFAULT[0].getName())) {
            return rxn.getDirection();
        } else if (name.equals(DEFAULT[1].getName())) {
            return rxn.toString();
        }

        return "N/A";

    }
}
