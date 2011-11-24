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

package uk.ac.ebi.mnb.view.entity.metabolite;

import uk.ac.ebi.mnb.view.entity.AbstractEntityView;


/**
 * MetaboliteView.java – MetabolicDevelopmentKit – Jun 4, 2011
 *
 * @author johnmay <johnmay@ebi.ac.uk, john.wilkinsonmay@gmail.com>
 */
public class MetaboliteView
  extends AbstractEntityView {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      MetaboliteView.class);

    public MetaboliteView() {
        super("Metabolites", new MetaboliteTable(), new MetaboliteInspector());
    }



    /**
     * @inheritDoc
     */
    @Override
    public MetaboliteInspector getInspector() {
        return (MetaboliteInspector) super.getInspector();
    }


    /**
     * @inheritDoc
     */
    @Override
    public MetaboliteTable getTable() {
        return (MetaboliteTable) super.getTable();
    }


}

