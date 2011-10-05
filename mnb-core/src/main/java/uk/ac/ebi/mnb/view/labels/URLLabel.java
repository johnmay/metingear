
/**
 * URLLabel.java
 *
 * 2011.09.26
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

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.log4j.Logger;


/**
 *          URLLabel – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class URLLabel extends ThemedLabel {

    private static final Logger LOGGER = Logger.getLogger(URLLabel.class);
    private URI uri;


    public URLLabel(URL url, String name) {
        super(name);
        try {
            this.uri = url.toURI();
        } catch( URISyntaxException ex ) {
            ex.printStackTrace();
        }
        if( Desktop.isDesktopSupported() ) {
            addMouseListener(new Clicked());
        } else {
            LOGGER.info("Desktop is not supported");
        }
    }


    private class Clicked extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            try {
                Desktop.getDesktop().browse(uri);
            } catch( IOException ex ) {
                LOGGER.error("Could not open browser");
            }
        }


        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }


        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }


    }


}

