/**
 * ProteinTableModel.java
 *
 * 2011.09.28
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
package uk.ac.ebi.mnb.view.entity.gene;

import org.apache.log4j.Logger;
import org.biojava3.core.sequence.template.Sequence;
import uk.ac.ebi.core.ReconstructionImpl;
import uk.ac.ebi.core.DefaultReconstructionManager;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.Gene;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTableModel;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.DataType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *          ProteinTableModel – 2011.09.28 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class GeneTableModel extends AbstractEntityTableModel {

    private static final Logger LOGGER = Logger.getLogger(GeneTableModel.class);
    private static final ColumnDescriptor[] DEFAULT = new ColumnDescriptor[]{
        new ColumnDescriptor("Sequence", null,
                             DataType.FIXED,
                             String.class)
    };

    public GeneTableModel() {
        super();
        addColumns(Arrays.asList(DEFAULT));
    }

    @Override
    public void loadComponents() {

        ReconstructionImpl recon = DefaultReconstructionManager.getInstance().getActive();

        if (recon != null) {
            super.setEntities(new ArrayList<AnnotatedEntity>(recon.getGenes()));
        }

    }

    @Override
    public Object getFixedType(AnnotatedEntity component, String name) {
        Gene gene = (Gene) component;

        if (name.equals("Sequence")) {
            Sequence sequence = gene.getSequence();
            return sequence != null ? sequence.getSequenceAsString() : "";
        }

        return "NA";

    }
}
