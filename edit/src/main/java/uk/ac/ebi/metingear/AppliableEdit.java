package uk.ac.ebi.metingear;

import javax.swing.undo.UndoableEdit;

/**
 * An appliable edit is an undoable edit which also defines an {@link #apply()}
 * method to actually perform the edit. This is useful when the edit requires
 * several non-trivial steps in the correct order.
 *
 * @author John May
 */
public interface AppliableEdit extends UndoableEdit {

    /**
     * Apply the edit
     */
    public void apply();

}
