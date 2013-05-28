
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
package uk.ac.ebi.mnb.view.entity.reaction;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Subsystem;
import uk.ac.ebi.mdk.domain.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Rating;
import uk.ac.ebi.mdk.domain.entity.Reaction;
import uk.ac.ebi.mdk.ui.edit.table.RatingCellEditor;
import uk.ac.ebi.mdk.ui.render.table.AnnotationCellRenderer;
import uk.ac.ebi.mdk.ui.render.table.RatingCellRenderer;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTable;


/**
 *          ReactionTable – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReactionTable extends AbstractEntityTable {

    private static final Logger LOGGER = Logger.getLogger(ReactionTable.class);


    public ReactionTable() {
        super(new ReactionTableModel(), Reaction.class);

        AnnotationCellRenderer annotationRenderer = new AnnotationCellRenderer();
        setDefaultRenderer(EnzymeClassification.class,
                           annotationRenderer);
        setDefaultRenderer(Subsystem.class,
                           annotationRenderer);

        setDefaultRenderer(Rating.class, new RatingCellRenderer());
        setDefaultEditor(Rating.class, new RatingCellEditor());


    }


}

