
/**
 * CheckBox.java
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

import javax.swing.Action;
import javax.swing.JCheckBox;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.caf.component.theme.Theme;


/**
 *          CheckBox – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MCheckBox extends JCheckBox {

    private static final Logger LOGGER = Logger.getLogger(MCheckBox.class);


    public MCheckBox() {
        Theme theme = Settings.getInstance().getTheme();
        setFont(theme.getBodyFont());
        setForeground(theme.getForeground());
    }


    public MCheckBox(Action a) {
        super(a);
        Theme theme = Settings.getInstance().getTheme();
        setFont(theme.getBodyFont());
        setForeground(theme.getForeground());
    }


    public MCheckBox(String text) {
        super(text);
        Theme theme = Settings.getInstance().getTheme();
        setFont(theme.getBodyFont());
        setForeground(theme.getForeground());
    }


}

