/**
 * ExpandableComponentGroup.java
 *
 * 2011.11.14
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
package uk.ac.ebi.mnb.core;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.render.factory.LabelFactory;

/**
 *          ExpandableComponentGroup - 2011.11.14 <br>
 *          Combines a ExpandButton with Label and Separator as well as creating
 *          the actions needed.
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ExpandableComponentGroup extends JComponent {

    private static final Logger LOGGER = Logger.getLogger(ExpandableComponentGroup.class);

    public ExpandableComponentGroup(String name, JComponent component) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        ExpandComponent excomp = new ExpandComponent(component);
        JToggleButton button = new ExpandButton(excomp);

        JComponent controller = Box.createHorizontalBox();

        JLabel label = LabelFactory.newFormLabel(name);
        label.setVerticalAlignment(JLabel.CENTER);

        controller.add(button);
        controller.add(Box.createRigidArea(new Dimension(16, 16)));
        controller.add(label);
        controller.add(Box.createRigidArea(new Dimension(16, 16)));

        JSeparator separator = new JSeparator();

        controller.add(separator);

        add(controller);
        add(component);

      //  component.setVisible(false);
    }
}
