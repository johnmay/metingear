/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.chemet.render.source;

import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;


/**
 *
 * @author johnmay
 */
public interface Accessor {


    public String access(AnnotatedEntity entity);

}

