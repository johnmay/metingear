/**
 * MRoundButton.java
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

import org.apache.log4j.Logger;

/**
 * @name    MRoundButton - 2011.10.07 <br>
 *         Allows a round icon button
 * from... http://stackoverflow.com/questions/778222/make-a-button-round
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
import java.awt.*;
import javax.swing.*;

public class MRoundButton extends JButton {

    public MRoundButton(Icon icon, Action action) {
        
        super(action);

        setIcon(icon);

// These statements enlarge the button so that it
// becomes a circle rather than an oval.
        Dimension size = getPreferredSize();
        size.width = size.height = Math.min(size.width,
                size.height);
        setMinimumSize(size);
        setPreferredSize(size);

        setContentAreaFilled(false);
        setBorder(null);

// This call causes the JButton not to paint
        // the background.
// This allows us to paint a round background.
    }



}