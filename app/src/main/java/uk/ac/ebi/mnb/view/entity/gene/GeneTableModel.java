/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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
package uk.ac.ebi.mnb.view.entity.gene;

import org.apache.log4j.Logger;
import org.biojava3.core.sequence.template.Sequence;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.Rating;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.StarRating;
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
public class GeneTableModel extends AbstractEntityTableModel {

    private static final Logger LOGGER = Logger.getLogger(GeneTableModel.class);
    private static final ColumnDescriptor[] DEFAULT = new ColumnDescriptor[]{
            new ColumnDescriptor("Sequence", null,
                                 DataType.FIXED,
                                 String.class),
            new ColumnDescriptor("Rating", null, DataType.FIXED, Rating.class)
    };

    public GeneTableModel() {
        super();
        addColumns(Arrays.asList(DEFAULT));
    }


    public Collection<? extends AnnotatedEntity> getEntities() {

        Reconstruction recon = DefaultReconstructionManager.getInstance()
                                                           .active();

        if (recon != null) {
            return recon.genome().genes();
        }

        return Collections.emptyList();

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (getColumnClass(columnIndex) == Rating.class) {
            return true;
        }
        return super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (getColumnClass(columnIndex) == Rating.class) {
            AnnotatedEntity entity = getEntity(rowIndex);
            entity.setRating((StarRating) value);
            update(entity);
            return;
        }
        super.setValueAt(value, rowIndex, columnIndex);
    }

    @Override
    public Object getFixedType(AnnotatedEntity component, String name) {
        Gene gene = (Gene) component;

        if (name.equals("Sequence")) {
            Sequence sequence = gene.getSequence();
            return sequence != null && sequence.getLength() > 0 ? sequence.getSequenceAsString() : "";
        } else if (name.equals("Rating")) {
            return component.getRating();
        }

        return "NA";

    }
}
