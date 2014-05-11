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
package uk.ac.ebi.mnb.view.entity.metabolite;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.utility.font.EBIIcon;
import uk.ac.ebi.caf.utility.font.IconBuilder;
import uk.ac.ebi.mdk.domain.annotation.ACPAssociated;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.Lumped;
import uk.ac.ebi.mdk.domain.annotation.MolecularFormula;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Rating;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.StarRating;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.observation.MatchedEntity;
import uk.ac.ebi.mdk.domain.observation.Observation;
import uk.ac.ebi.mdk.tool.domain.StructuralValidity;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;
import uk.ac.ebi.mnb.edit.RemoveAnnotationEdit;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTableModel;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.DataType;

import javax.swing.ImageIcon;
import javax.swing.undo.CompoundEdit;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * MetaboliteTableModel – 2011.09.06 <br> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class MetaboliteTableModel
        extends AbstractEntityTableModel {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteTableModel.class);

    private static final ColumnDescriptor[] DEFAULT = new ColumnDescriptor[]{
            new ColumnDescriptor("Generic", null,
                                 DataType.FIXED,
                                 Boolean.class),
            new ColumnDescriptor(new CrossReference()),
            new ColumnDescriptor(new MolecularFormula()),
            new ColumnDescriptor("Validity", null,
                                 DataType.FIXED,
                                 StructuralValidity.class),
            new ColumnDescriptor("Match", null,
                                 DataType.FIXED,
                                 Match.class),
            new ColumnDescriptor("Rating", null, DataType.FIXED, Rating.class),
            new ColumnDescriptor("Structures", null, DataType.FIXED, Integer.class),
            new ColumnDescriptor(Lumped.getInstance()),
            new ColumnDescriptor(ACPAssociated.getInstance())
    };


    public MetaboliteTableModel() {
        super();
        addColumns(Arrays.asList(DEFAULT));
    }


    @Override
    public Collection<? extends AnnotatedEntity> getEntities() {

        Reconstruction project = DefaultReconstructionManager.getInstance()
                                                             .active();

        if (project != null) {
            return project.metabolome().toList();
        }

        return Collections.emptyList();

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
    @SuppressWarnings("unchecked")
    public void setValueAt(Object value, int rowIndex, int columnIndex) {

        if (getColumnClass(columnIndex) == CrossReference.class) {
            AnnotatedEntity entity = getEntity(rowIndex);
            List<Annotation> annotations = new ArrayList<Annotation>(entity.getAnnotationsExtending(CrossReference.class));
            CompoundEdit edit = new CompoundEdit();
            edit.addEdit(new RemoveAnnotationEdit(entity, annotations));
            edit.addEdit(new AddAnnotationEdit(entity, (Collection<Annotation>) value));
            edit.end();
            // apply the edit
            for (Annotation annotation : annotations) {
                entity.removeAnnotation(annotation);
            }
            entity.addAnnotations((Collection<Annotation>) value);
            MainView.getInstance().getUndoManager().addEdit(edit);
            update(entity);
            return;
        }
        else if (getColumnClass(columnIndex) == Rating.class) {
            AnnotatedEntity entity = getEntity(rowIndex);
            entity.setRating((StarRating) value);
            update(entity);
            return;
        }

        super.setValueAt(value, rowIndex, columnIndex);
    }


    @Override
    public Object getFixedType(AnnotatedEntity component, String name) {

        Metabolite entity = (Metabolite) component;

        if (name.equals(DEFAULT[0].getName())) {

            return entity.isGeneric();
        }
        else if (name.equals("Rating")) {
            return component.getRating();

        }
        else if (name.equals("Validity")) {
            return StructuralValidity.getValidity(entity);
        }
        else if (name.equals("Match")) {
            Collection<Observation> matches = entity.getObservations(MatchedEntity.class);
            if (matches.isEmpty()) {
                return Match.NONE;
            }
            else if (matches.size() == 1) {
                return Match.UNIQUE;
            }
            else {
                return Match.MULTIPLE;
            }
        }
        else if (name.equals("Structures")) {
            return entity.getStructures().size();
        }

        return "NA";
    }

    enum Match {
        NONE(new Color(0xFF8B91)),
        MULTIPLE(new Color(0xB6C1FF)),
        UNIQUE(new Color(0xA2FF90));

        private final ImageIcon icon;

        Match(Color color) {
            icon = EBIIcon.DATABASE_CROSSLINK.create().color(color).icon();
        }
        
        ImageIcon icon() {
            return icon;
        }
    }
}
