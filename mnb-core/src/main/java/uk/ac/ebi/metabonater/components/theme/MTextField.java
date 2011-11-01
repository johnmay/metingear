/**
 * MTextField.java
 *
 * 2011.10.07
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
package uk.ac.ebi.metabonater.components.theme;

import javax.swing.JTextField;
import javax.swing.text.Document;
import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.Theme;
import uk.ac.ebi.mnb.settings.Settings;

/**
 * @name    MTextField - 2011.10.07 <br>
 *          Metabonater Text Field (uses Theme values)
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MTextField extends JTextField {

    private static final Logger LOGGER = Logger.getLogger(MTextField.class);

    public MTextField() {
        Theme t = Settings.getInstance().getTheme();
        setFont(t.getBodyFont());
    }

    public MTextField(String text) {
        super(text);
        Theme t = Settings.getInstance().getTheme();
        setFont(t.getBodyFont());
    }

    public MTextField(int columns) {
        super(columns);
        Theme t = Settings.getInstance().getTheme();
        setFont(t.getBodyFont());
    }

    public MTextField(String text, int columns) {
        super(text, columns);
        Theme t = Settings.getInstance().getTheme();
        setFont(t.getBodyFont());
    }

    @Override
    public void updateUI() {
        Theme t = Settings.getInstance().getTheme();
        setFont(t.getBodyFont());
        super.updateUI();
    }
}
