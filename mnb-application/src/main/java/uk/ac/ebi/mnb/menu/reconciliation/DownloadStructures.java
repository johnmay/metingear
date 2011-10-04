
/**
 * DownloadStructures.java
 *
 * 2011.09.27
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
package uk.ac.ebi.mnb.menu.reconciliation;

import java.util.Collection;
import uk.ac.ebi.mnb.core.DelayedBuildAction;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.mnb.main.MainView;


/**
 *          DownloadStructures – 2011.09.27 <br>
 *          Downloads structures using xrefs
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class DownloadStructures
  extends DelayedBuildAction {

    private static final Logger LOGGER = Logger.getLogger(DownloadStructures.class);
    private DownloadStructuresDialog dialog;


    public DownloadStructures() {
        super("DownloadStructures");
    }


    @Override
    public void buildComponents() {
        dialog = new DownloadStructuresDialog();
    }


    @Override
    public void activateActions() {

        Collection<AnnotatedEntity> components = MainView.getInstance().getViewController().
          getSelection();

        if( components.isEmpty() == false ) {
            dialog.setComponents(components);
            dialog.setVisible(true);
            
            // modal dialog won't execute till after
            MainView.getInstance().getViewController().update();
        }

    }


}

