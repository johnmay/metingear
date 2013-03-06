/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
package uk.ac.ebi.mnb.main;

import com.explodingpixels.macwidgets.LabeledComponentGroup;
import com.explodingpixels.macwidgets.UnifiedToolBar;
import com.explodingpixels.macwidgets.plaf.EmphasizedLabelUI;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.LabelFactory;

/**
 *          Toolbar - 2011.11.03 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class Toolbar extends UnifiedToolBar {

    private static final Logger LOGGER = Logger.getLogger(Toolbar.class);

    public Toolbar() {
    }

    public void addComponentToRight(JLabel label, JComponent... components) {
        addComponentToRight(new LabeledComponentGroup(label, components).getComponent());
    }

    public void addComponentToRight(String text, JComponent... components) {
        JLabel label = LabelFactory.newLabel(text);
        label.setFont(label.getFont().deriveFont(11.0f));
        label.setUI(new EmphasizedLabelUI());
        addComponentToRight(new LabeledComponentGroup(label, components).getComponent());
    }
}
