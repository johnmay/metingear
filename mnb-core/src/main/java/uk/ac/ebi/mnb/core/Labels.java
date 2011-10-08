/**
 * LabelFactory.java
 *
 * 2011.10.08
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

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.view.labels.MLabel;

/**
 * @name    LabelFactory - 2011.10.08 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class Labels {

    private static final Logger LOGGER = Logger.getLogger(Labels.class);

    public static JLabel newLabel(String text) {
        return new MLabel(text);
    }

    /**
     * Returns a right aligned label
     * @param text
     * @return
     */
    public static  JLabel newFormLabel(String text) {
        return new MLabel(text, SwingConstants.RIGHT);
    }

    /**
     * Returns a new form label (right aligned) with the option of adding tool tip text
     * @param text
     * @param tooltip
     * @return
     */
    public static JLabel newFormLabel(String text, String tooltip) {
        JLabel label =  new MLabel(text, SwingConstants.RIGHT);
        label.setToolTipText(tooltip);
        return label;
    }
}
