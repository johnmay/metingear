
/**
 * MetaboliteInspector.java
 *
 * 2011.09.06
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
package uk.ac.ebi.mnb.view.entity.metabolite;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.JTextField;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.TransparentTextField;
import uk.ac.ebi.mnb.view.labels.BoldLabel;
import uk.ac.ebi.mnb.view.entity.EntityInspector;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.mnb.main.MainFrame;


/**
 *          MetaboliteInspector – 2011.09.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetaboliteInspector
  extends EntityInspector {

    private static final Logger LOGGER = Logger.getLogger(MetaboliteInspector.class);
    private static final BoldLabel idLabel = new BoldLabel("Identifier");
    private static final BoldLabel nameLabel = new BoldLabel("Name");
    private static final BoldLabel genericLabel = new BoldLabel("Generic");
    private static final JTextField idField = new TransparentTextField();
    private static final JTextField nameField = new TransparentTextField();
    private static final JTextField genericField = new TransparentTextField();


    public MetaboliteInspector() {
        super(new MetabolitePanel());
    }


    @Override
    public void store() {
        super.store();
    }


}

