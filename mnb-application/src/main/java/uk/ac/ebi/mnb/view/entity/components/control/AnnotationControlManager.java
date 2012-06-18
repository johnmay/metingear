/**
 * AnnotationControlManager.java
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

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.annotation.ObservationBasedAnnotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;

import javax.swing.*;
import java.util.*;


/**
 *
 *          AnnotationControlManager 2012.02.14
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *
 *          Class description
 *
 */
public class AnnotationControlManager {

    private static final Logger LOGGER = Logger.getLogger(AnnotationControlManager.class);

    private Map<Class, AnnotationTableControl> cachedMap = new HashMap<Class, AnnotationTableControl>();

    private Map<Class, AnnotationTableControl> controlMap = new HashMap<Class, AnnotationTableControl>();

    private AnnotationTableControl NULL_CONTROLLER = new NullControl();


    public AnnotationControlManager(JList observationList) {
        controlMap.put(ObservationBasedAnnotation.class, new ObservationBasedAnnotationControl(observationList));
        controlMap.put(ChemicalStructure.class, new ChemicalStructureControl());
    }


    public Object getController(Annotation annotation, AnnotatedEntity entity) {

        if (!cachedMap.containsKey(annotation.getClass())) {
            cachedMap.put(annotation.getClass(), getExplicitContol(annotation.getClass()));
        }

        return cachedMap.get(annotation.getClass()).getController(annotation, entity);
    }


    protected AnnotationTableControl getExplicitContol(Class<?> valClass) {

        if (controlMap.containsKey(valClass)) {
            return controlMap.get(valClass);
        }

        Queue<Class<?>> queue = new LinkedList<Class<?>>(); // the BFS' "to be visited" queue
        Set<Class<?>> visited = new HashSet<Class<?>>();    // the class objects we have visited

        queue.add(valClass);
        visited.add(valClass);

        while (!queue.isEmpty()) {
            Class<?> curClass = queue.remove();

            // get the super types to visit.
            List<Class<?>> supers = new LinkedList<Class<?>>();
            for (Class<?> itrfce : curClass.getInterfaces()) {
                supers.add(itrfce);
            }
            Class<?> superClass = curClass.getSuperclass(); // this would be null for interfaces.
            if (superClass != null) {
                supers.add(superClass);
            }

            for (Class<?> ifs : supers) {
                if (controlMap.containsKey(ifs)) {
                    return controlMap.get(ifs);
                }
                if (!visited.contains(ifs)) {
                    queue.add(ifs);
                    visited.add(ifs);
                }
            }

        }

        return NULL_CONTROLLER;
    }


    private class NullControl implements AnnotationTableControl {

        public Object getController(Annotation annotation, AnnotatedEntity entity) {
            return null;
        }
    };
}
