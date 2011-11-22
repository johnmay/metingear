/**
 * FieldFactory.java
 *
 * 2011.11.17
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
package uk.ac.ebi.ui.component.factory;

import javax.swing.JTextField;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.settings.Settings;

/**
 *          FieldFactory - 2011.11.17 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class FieldFactory {

    private static final Logger LOGGER = Logger.getLogger(FieldFactory.class);

    public static JTextField newField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(Settings.getInstance().getTheme().getBodyFont());
        field.setForeground(Settings.getInstance().getTheme().getForeground());
        return field;
    }

    public static JTextField newField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(Settings.getInstance().getTheme().getBodyFont());
        field.setForeground(Settings.getInstance().getTheme().getForeground());
        return field;
    }

    public static JTextField newTransparentField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(Settings.getInstance().getTheme().getBodyFont());
        field.setForeground(Settings.getInstance().getTheme().getForeground());
        field.setBackground(null);
        field.setBorder(null);
        return field;
    }

    public static JTextField newTransparentField(int columns, boolean editable) {
        JTextField field = newTransparentField(columns);
        field.setEditable(editable);
        return field;
    }
}
