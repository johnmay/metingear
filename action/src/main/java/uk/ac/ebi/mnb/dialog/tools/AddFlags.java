/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
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

package uk.ac.ebi.mnb.dialog.tools;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;

/**
 * @author John May
 */
public class AddFlags extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(AddFlags.class);

    public AddFlags(MainController controller) {
        super(AddFlags.class.getSimpleName(), controller);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        AnnotationFactory factory = DefaultAnnotationFactory.getInstance();

        for (AnnotatedEntity entity : getSelection().getEntities()) {
            entity.addAnnotations(factory.getMatchingFlags(entity));
        }

        update(getSelection());

    }

}
