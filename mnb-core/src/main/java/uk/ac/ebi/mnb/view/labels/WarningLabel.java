
/**
 * WarningLabel.java
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
package uk.ac.ebi.mnb.view.labels;

import javax.swing.Icon;
import javax.swing.JLabel;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.core.ApplicationPreferences;


/**
 *          WarningLabel – 2011.09.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class WarningLabel extends JLabel {

    private static final Logger LOGGER = Logger.getLogger(WarningLabel.class);


    public WarningLabel(String text) {
        this();
        setText(text);
    }


    public WarningLabel(Icon icon) {
        this();
        setIcon(icon);
    }


    public WarningLabel() {
        setFont(ApplicationPreferences.getInstance().getTheme().getBodyFont());
        setForeground(ApplicationPreferences.getInstance().getTheme().getWarningForeground());
    }


}

