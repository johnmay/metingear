
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
package uk.ac.ebi.mnb.menu.view;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 *          ToggleInspectorToolbar – 2011.09.30 <br>
 *          Toggles the inspector toolbar
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ToggleInspectorToolbar extends GeneralAction {

    private static final Logger LOGGER = Logger.getLogger(ToggleInspectorToolbar.class);


    public ToggleInspectorToolbar() {
        super("ToggleInspectorToolbar");
    }


    public void actionPerformed(ActionEvent e) {

        boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();

        MainView.getInstance().getViewController().getActiveView().update();

    }


}

