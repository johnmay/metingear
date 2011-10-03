/*
 *     This file is part of Metabolic Network Builder
 *
 *     Metabolic Network Builder is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.mnb.view.entity.reaction;

import uk.ac.ebi.mnb.view.entity.AbstractEntityView;


/**
 *
 * ReactionsView.java – MetabolicDevelopmentKit – Jun 4, 2011
 * Displays the reactions in the current project
 *
 * @author johnmay <johnmay@ebi.ac.uk, john.wilkinsonmay@gmail.com>
 *
 */
public class ReactionsView
  extends AbstractEntityView {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      ReactionsView.class);
    private static final String name = "Reaction Panel";


    public ReactionsView() {
        super(new ReactionTable(), new ReactionInspector());
        setName(name);
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

