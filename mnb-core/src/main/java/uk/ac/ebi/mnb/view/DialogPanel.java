/**
 * Preferences.java
 *
 * 2011.10.02
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

import java.awt.LayoutManager;
import javax.swing.JPanel;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.interfaces.Theme;

/**
 * DialogPanel.java
 * Similar to panel but allows a difference color scheme using
 * getDialogBackground() method from {@see Theme}
 *
 * @author johnmay
 * @date May 11, 2011
 */
public class DialogPanel
        extends JPanel {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
            DialogPanel.class);

    public DialogPanel(LayoutManager layout) {
        this();
        setLayout(layout);
        Theme t = Settings.getInstance().getTheme();
        setBackground(t.getDialogBackground());
    }

    public DialogPanel() {
        Theme t = Settings.getInstance().getTheme();
        setBackground(t.getDialogBackground());
    }
}
