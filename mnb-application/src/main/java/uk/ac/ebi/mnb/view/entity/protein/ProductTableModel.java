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
package uk.ac.ebi.mnb.view.entity.protein;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTableModel;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.DataType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * ProteinTableModel – 2011.09.28 <br> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class ProductTableModel extends AbstractEntityTableModel {

    private static final Logger LOGGER = Logger.getLogger(ProductTableModel.class);
    private static final ColumnDescriptor[] DEFAULT = new ColumnDescriptor[]{
            new ColumnDescriptor("Sequence", null,
                                 DataType.FIXED,
                                 String.class)
    };

    public ProductTableModel() {
        super();
        addColumns(Arrays.asList(DEFAULT));
    }

    @Override
    public Collection<? extends AnnotatedEntity> getEntities() {

        Reconstruction recon = DefaultReconstructionManager.getInstance()
                                                           .getActive();

        if (recon != null) {
            return recon.getProteome();
        }

        return Collections.EMPTY_LIST;

    }

    @Override
    public Object getFixedType(AnnotatedEntity component, String name) {
        GeneProduct product = (GeneProduct) component;

        if (name.equals("Sequence")) {
            return Joiner.on("/").join(product.getSequences());
        }

        return "NA";

    }
}
