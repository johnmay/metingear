package uk.ac.ebi.metingear.view;

import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;

import java.util.List;

/**
 * @version $Rev$
 */
public interface PlugableDialog {

    public List<String> getMenuPath();

    public Class<? extends ControlDialog> getDialogClass();

    public ContextResponder getContext();

}
