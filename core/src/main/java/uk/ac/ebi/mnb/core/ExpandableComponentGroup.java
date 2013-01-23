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
import javax.swing.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.LabelFactory;


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

    private ExpandComponent excomp;

    private JToggleButton button;


    public ExpandableComponentGroup(String name, JComponent component) {
        this(name, component, null);
    }


    public ExpandableComponentGroup(String name, JComponent component, JDialog root) {
        setLayout(new FormLayout("p:grow",
                                 "min, 2dlu, p:grow"));
        excomp = new ExpandComponent(component, root);
        button = new ExpandButton(excomp);

        Box controller = Box.createHorizontalBox();

        button.setSelected(false);

        JLabel label = LabelFactory.newLabel(name);
        label.setVerticalAlignment(JLabel.CENTER);


        CellConstraints cc = new CellConstraints();

        controller.add(button);
        controller.add(Box.createRigidArea(new Dimension(16, 16)));
        controller.add(label);
        controller.add(Box.createGlue());


        JSeparator separator = new JSeparator();

        controller.add(separator);

        add(controller, cc.xy(1,1));
        add(component, cc.xy(1,3, CellConstraints.FILL, CellConstraints.FILL));

        component.setVisible(false);

    }


    public JToggleButton getButton() {
        return button;
    }


    public void toggle() {
        button.doClick();
    }
}
