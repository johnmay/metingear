/**
 * MetaboliteTable.java
 *
 * 2011.09.05
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

import uk.ac.ebi.chemet.render.table.renderers.AnnotationCellRenderer;
import uk.ac.ebi.chemet.render.table.renderers.BooleanCellRenderer;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTable;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.interfaces.Rating;
import uk.ac.ebi.chemet.render.table.editors.RatingCellEditor;
import uk.ac.ebi.chemet.render.table.renderers.RatingCellRenderer;
import uk.ac.ebi.chemet.render.table.renderers.StructuralValidityRenderer;
import uk.ac.ebi.core.tools.StructuralValidity;
import uk.ac.ebi.mnb.editors.CrossReferenceCellEditor;


/**
 *          MetaboliteTable – 2011.09.05 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
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

        //   setDefaultRenderer(MolecularFormula.class,
        //                      new FormulaCellRender());






        setDefaultRenderer(Rating.class, new RatingCellRenderer());
        setDefaultEditor(Rating.class, new RatingCellEditor());
        // setDefaultEditor(Rating.class, new ITunesRatingTableCellEditor());

//        setRowHeight(64);// only set when chem structure is to be displayed

        //addMouseListener(new DoubleClickListener(this));

    }
}
