package uk.ac.ebi.mnb.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.UndoableEntityEdit;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides undoable edit support for replacing a single annotation with
 * another.
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class ReplaceAnnotationEdit extends UndoableEntityEdit {

    private static final Logger LOGGER = Logger.getLogger(ReplaceAnnotationEdit.class);

    private AnnotatedEntity entity;
    private Annotation original;
    private Annotation replacement;
    
    public ReplaceAnnotationEdit(AnnotatedEntity entity,
                                 Annotation original,
                                 Annotation replacement){
        this.entity      = entity;
        this.original    = original;
        this.replacement = replacement;
    }

    /**
     * Can redo if the entity has the original annotation
     * @return
     */
    @Override
    public boolean canRedo() {
        return entity.hasAnnotation(original);
    }

    /**
     * Can undo if the entity has the replacement annotation
     * @return
     */
    @Override
    public boolean canUndo() {
        return entity.hasAnnotation(replacement);
    }

    /**
     * Removes original annotation and adds the replacement
     * @throws CannotRedoException
     */
    @Override
    public void redo() throws CannotRedoException {
        entity.removeAnnotation(original);
        entity.addAnnotation(replacement);
    }

    /**
     * Removes the replacement and adds the orginal
     * @throws CannotUndoException
     */
    @Override
    public void undo() throws CannotUndoException {
        entity.removeAnnotation(replacement);
        entity.addAnnotation(original);
    }

    @Override
    public Collection<AnnotatedEntity> getEntities() {
        return Arrays.asList(entity);
    }
}
