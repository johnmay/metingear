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
import uk.ac.ebi.mdk.domain.annotation.ACPAssociated;
import uk.ac.ebi.mdk.domain.annotation.Lumped;
import uk.ac.ebi.mdk.domain.annotation.MolecularFormula;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.Rating;
import uk.ac.ebi.mdk.tool.domain.StructuralValidity;
import uk.ac.ebi.mdk.ui.edit.table.RatingCellEditor;
import uk.ac.ebi.mdk.ui.render.table.AnnotationCellRenderer;
import uk.ac.ebi.mdk.ui.render.table.BooleanCellRenderer;
import uk.ac.ebi.mdk.ui.render.table.RatingCellRenderer;
import uk.ac.ebi.mdk.ui.render.table.StructuralValidityRenderer;
import uk.ac.ebi.mnb.editors.CrossReferenceCellEditor;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTable;


/**
 * MetaboliteTable – 2011.09.05 <br>
 * Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class MetaboliteTable
        extends AbstractEntityTable {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteTable.class);


    public MetaboliteTable() {
        super(new MetaboliteTableModel());
        //       setDefaultRenderer(ChemicalStructure.class,
        //                          new ChemStructureRenderer());
        AnnotationCellRenderer annotationRenderer = new AnnotationCellRenderer();
        BooleanCellRenderer booleanRenderer = new BooleanCellRenderer();

        setDefaultRenderer(Boolean.class,
                           booleanRenderer);
        setDefaultRenderer(CrossReference.class,
                           annotationRenderer);
        setDefaultEditor(CrossReference.class,
                         new CrossReferenceCellEditor());
        setDefaultRenderer(StructuralValidity.class,
                           new StructuralValidityRenderer());

        setDefaultRenderer(MolecularFormula.class,
                           annotationRenderer);
        setDefaultRenderer(Lumped.class,
                           annotationRenderer);
        setDefaultRenderer(ACPAssociated.class,
                           annotationRenderer);


        setDefaultRenderer(Rating.class, new RatingCellRenderer());
        setDefaultEditor(Rating.class, new RatingCellEditor());
        // setDefaultEditor(Rating.class, new ITunesRatingTableCellEditor());

//        setRowHeight(64);// only set when chem structure is to be displayed

        //addMouseListener(new DoubleClickListener(this));

    }
}
