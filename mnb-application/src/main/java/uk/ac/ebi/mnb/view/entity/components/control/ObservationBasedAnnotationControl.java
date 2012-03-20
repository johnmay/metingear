/**
 * ObservationBasedAnnotationControl.java
 *
 * 2012.02.14
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
package uk.ac.ebi.mnb.view.entity.components.control;

import java.awt.event.ActionEvent;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.Annotation;
import uk.ac.ebi.interfaces.Observation;
import uk.ac.ebi.interfaces.annotation.ObservationBasedAnnotation;


/**
 *
 *          ObservationBasedAnnotationControl 2012.02.14
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *
 *          Class description
 *
 */
public class ObservationBasedAnnotationControl implements AnnotationTableControl {

    private static final Logger LOGGER = Logger.getLogger(ObservationBasedAnnotationControl.class);

    private JList observationList;


    public ObservationBasedAnnotationControl(JList observationList) {
        this.observationList = observationList;
    }


    public Object getController(final Annotation annotation, final AnnotatedEntity entity) {

        final ObservationBasedAnnotation<Observation> oba = (ObservationBasedAnnotation) annotation;

        if (!oba.getObservations().isEmpty()) {
            return new GeneralAction("ShowEvidence") {

                public void actionPerformed(ActionEvent e) {

                    observationList.removeSelectionInterval(0, observationList.getModel().getSize());

                    for (Observation observation : oba.getObservations()) {
                        DefaultListModel model = (DefaultListModel) observationList.getModel();
                        int index = model.indexOf(observation);
                        if (index != -1) {
                            observationList.addSelectionInterval(index, index);
                        }
                    }
                }
            };
        }

        return null;


    }
}
