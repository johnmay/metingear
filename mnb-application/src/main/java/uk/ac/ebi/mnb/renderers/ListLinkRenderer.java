/**
 * ListLinkRenderer.java
 *
 * 2011.10.06
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
package uk.ac.ebi.mnb.renderers;

import com.jgoodies.forms.factories.Borders;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AbstractAnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.Theme;
import uk.ac.ebi.mnb.settings.Settings;

/**
 * @name    ListLinkRenderer - 2011.10.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ListLinkRenderer extends JLabel implements ListCellRenderer {

    private static final Logger LOGGER = Logger.getLogger(ListLinkRenderer.class);

    public ListLinkRenderer() {
        setFont(Settings.getInstance().getTheme().getLinkFont());
        setBorder(Borders.EMPTY_BORDER);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        AbstractAnnotatedEntity entity = ((AbstractAnnotatedEntity) value);
        String name = entity.getName();
        setText(name.substring(0, Math.min(40, name.length())));
        setToolTipText(entity.getAbbreviation() + ":" + name);
        Theme theme = Settings.getInstance().getTheme();
        setForeground(isSelected ? theme.getEmphasisedForeground() : theme.getForeground());
        return this;
    }
}
