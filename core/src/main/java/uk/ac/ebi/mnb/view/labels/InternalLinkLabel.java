
/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.view.labels;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mnb.interfaces.SelectionController;

import javax.swing.*;
import java.awt.event.ActionEvent;




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
        EntityCollection manager = selector.getSelection();
        manager.clear().add(entity);
        selector.setSelection(manager); // push updates
    }


}

