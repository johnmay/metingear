/**
 * MetaboliteTableModel.java
 *
 * 2011.09.06
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
package uk.ac.ebi.mnb.view.entity.metabolite;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.ACPAssociated;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.MolecularFormula;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.annotation.Lumped;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.*;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.tool.domain.StructuralValidity;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTableModel;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 *          MetaboliteTableModel – 2011.09.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetaboliteTableModel
        extends AbstractEntityTableModel {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteTableModel.class);

    private static final ColumnDescriptor[] DEFAULT = new ColumnDescriptor[]{
        new ColumnDescriptor("Generic", null,
                             DataType.FIXED,
                             Boolean.class),
        new ColumnDescriptor(new CrossReference()),
        new ColumnDescriptor(new AtomContainerAnnotation()),
        new ColumnDescriptor(new MolecularFormula()),
        new ColumnDescriptor("Validity", null,
                             DataType.FIXED,
                             StructuralValidity.class),
        new ColumnDescriptor("Rating", null, DataType.FIXED, Rating.class),
        new ColumnDescriptor( Lumped.getInstance() ),
        new ColumnDescriptor( ACPAssociated.getInstance() )
    };


    public MetaboliteTableModel() {
        super();
        addColumns(Arrays.asList(DEFAULT));
    }


    @Override
    public void loadComponents() {

        Reconstruction project = DefaultReconstructionManager.getInstance().getActive();

        if (project != null) {
            setEntities(project.getMetabolome());
        }

    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        if (getColumnClass(columnIndex) == CrossReference.class
            || getColumnClass(columnIndex) == Rating.class) {
            return true;
        }

        return super.isCellEditable(rowIndex, columnIndex);
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        if (getColumnClass(columnIndex) == CrossReference.class) {
            AnnotatedEntity entity = getEntity(rowIndex);
            List<Annotation> annotations = new ArrayList(entity.getAnnotationsExtending(CrossReference.class));
            for (int i = 0; i < annotations.size(); i++) {
                entity.removeAnnotation(annotations.get(i));
            }
            entity.addAnnotations((Collection<Annotation>) aValue);
            update(entity);
            return;
        } else if (getColumnClass(columnIndex) == Rating.class) {
            AnnotatedEntity entity = getEntity(rowIndex);
            entity.setRating((StarRating) aValue);
            update(entity);
            return;
        }

        super.setValueAt(aValue, rowIndex, columnIndex);
    }


    @Override
    public Object getFixedType(AnnotatedEntity component, String name) {

        Metabolite entity = (Metabolite) component;

        if (name.equals(DEFAULT[0].getName())) {

            return entity.isGeneric();
        } else if (name.equals("Rating")) {
            return component.getRating();

        } else if (name.equals("Validity")) {
            return StructuralValidity.getValidity(entity);
        }

        return "NA";

    }
}
