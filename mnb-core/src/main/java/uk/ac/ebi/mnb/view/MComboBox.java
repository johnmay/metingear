/**
 * ComboBox.java
 *
 * 2011.09.30
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

import java.util.Collection;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.caf.component.theme.Theme;

/**
 *          ComboBox – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MComboBox extends JComboBox {

    private static final Logger LOGGER = Logger.getLogger(MComboBox.class);

    public MComboBox() {
        Theme theme = Settings.getInstance().getTheme();
        setForeground(theme.getForeground());
        setFont(theme.getBodyFont());
    }

    public MComboBox(Object[] items) {
        super(items);
        Theme theme = Settings.getInstance().getTheme();
        setForeground(theme.getForeground());
        setFont(theme.getBodyFont());
    }

    public MComboBox(Collection<String> items) {
        super(new DefaultComboBoxModel(items.toArray(new String[0])));
        Theme theme = Settings.getInstance().getTheme();
        setForeground(theme.getForeground());
        setFont(theme.getBodyFont());
    }
}
