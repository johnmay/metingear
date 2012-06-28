/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
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
package uk.ac.ebi.mnb.dialog.preferences;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 * @name    ResourceUpdater - 2011.10.15 <br>
 *          A UI panel to allow updating of mapped resources
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ResourceUpdater
        extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(ResourceUpdater.class);
    private CellConstraints cc = new CellConstraints();

    public ResourceUpdater() {

        super(new FormLayout("p", "p"));

        // name,
        // last updated
        // local size
        // button: update
    }
}
