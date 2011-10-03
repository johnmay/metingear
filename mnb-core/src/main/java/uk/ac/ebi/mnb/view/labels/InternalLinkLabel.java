
/**
 * InternalLinkLabel.java
 *
 * 2011.09.28
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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.SelectionController;




/**
 *          InternalLinkLabel – 2011.09.28 <br>
 *          Creates an internal link label to an entity in the gui. Clicking on the label
 *          will invoke the setSelected method of EntitySelector 
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class InternalLinkLabel extends ActionLabel {

    private static final Logger LOGGER = Logger.getLogger(InternalLinkLabel.class);


    public InternalLinkLabel(AnnotatedEntity entity, SelectionController selector) {
        super(entity.getIdentifier().getAccession(), new ShowItem(selector, entity));
    }
    public InternalLinkLabel(AnnotatedEntity entity, String label, SelectionController selector) {
        super(label, new ShowItem(selector, entity));
    }


}
class ShowItem extends AbstractAction {

    private SelectionController selector;
    private AnnotatedEntity entity;


    public ShowItem(SelectionController selector, AnnotatedEntity entity) {
        this.entity = entity;
        this.selector = selector;
    }


    public void actionPerformed(ActionEvent e) {
        selector.setSelection(entity);
    }


}

