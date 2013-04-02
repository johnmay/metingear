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
import uk.ac.ebi.mnb.view.entity.AbstractEntityView;

/**
 *
 * ReactionsView.java – MetabolicDevelopmentKit – Jun 4, 2011
 * Displays the reactions in the current project
 *
 * @author johnmay <johnmay@ebi.ac.uk, john.wilkinsonmay@gmail.com>
 *
 */
public class ReactionView
        extends AbstractEntityView {

    private static final Logger LOGGER = Logger.getLogger(ReactionView.class);

    public ReactionView() {
        super("Reactions", new ReactionTable(), new ReactionInspector());
    }

    /**
     * @inheritDoc
     */
    @Override
    public ReactionInspector getInspector() {
        return (ReactionInspector) super.getInspector();
    }

    /**
     * @inheritDoc
     */
    @Override
    public ReactionTable getTable() {
        return (ReactionTable) super.getTable();
    }
}
