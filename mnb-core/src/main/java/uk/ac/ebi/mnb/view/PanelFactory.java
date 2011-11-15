/**
 * PanelFactory.java
 *
 * 2011.11.07
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
package uk.ac.ebi.mnb.view;

import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *          PanelFactory - 2011.11.07 <br>
 *          A class to manage JPanel generation
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class PanelFactory {

    private static final Logger LOGGER = Logger.getLogger(PanelFactory.class);
    private static final Color DIALOG_BACKGROUND = new Color(237, 237, 237);

    public static JPanel create() {
        return new JPanel();
    }

    /**
     * Creates a panel the dialog background (Light Gray-ish)
     * @return
     */
    public static JPanel createDialogPanel() {
        JPanel panel = create();
        panel.setBackground(DIALOG_BACKGROUND);
        return panel;
    }

    /**
     * Creates a JGoodies FormLayout panel using the provided encoding spec
     * @param encodedColumnSpec
     * @param encodedRowSpec
     * @return
     */
    public static JPanel createDialogPanel(String encodedColumnSpec, String encodedRowSpec) {
        JPanel panel = createDialogPanel();
        panel.setLayout(new FormLayout(encodedColumnSpec, encodedRowSpec));
        return panel;
    }

    /**
     * Creates a white panel for display info cleanly
     */
    public static JPanel createInfoPanel() {
        JPanel panel = create();
        panel.setBackground(Color.WHITE);
        return panel;
    }

    /**
     * Creates a JGoodies FormLayout panel using the provided encoding spec
     * @param encodedColumnSpec
     * @param encodedRowSpec
     * @return
     */
    public static JPanel createInfoPanel(String encodedColumnSpec, String encodedRowSpec) {
        JPanel panel = createInfoPanel();
        panel.setLayout(new FormLayout(encodedColumnSpec, encodedRowSpec));
        return panel;
    }
}
