/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.core;

import java.lang.String;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import uk.ac.ebi.mnb.view.ViewUtils;

/**
 * GeneralAction.java
 *
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public abstract class GeneralAction extends AbstractAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GeneralAction.class);
    private static ActionProperties actionProperties = ActionProperties.getInstance();
    private String command;
    public static final String PROJECT_REQUIRMENTS = "ProjectRequirements";
    public static final String EXPAND_BUTTON_OPEN_ICON = "OpenIcon";
    public static final String EXPAND_BUTTON_CLOSE_ICON = "CloseIcon";
    private String[] actionValues = new String[]{
        Action.NAME,
        Action.SHORT_DESCRIPTION,
        Action.ACCELERATOR_KEY,
        Action.LARGE_ICON_KEY,
        GeneralAction.PROJECT_REQUIRMENTS,
        EXPAND_BUTTON_OPEN_ICON,
        EXPAND_BUTTON_CLOSE_ICON // old use two different buttons and switch between them with setVisible
    };

    /**
     * Constructor loads the properties from action.properties (ActionProperties)
     * given the command name
     * @param command
     */
    public GeneralAction(String command) {

        this.command = command;

        for (String actionValue : actionValues) {
            String action = actionProperties.getProperty(command + ".Action." + actionValue);
            setLoadedValue(actionValue, action);
        }
    }

    /**
     * Set the loaded values of the key. If the key is ACCELERATOR_KEY then the
     * newValue is automatically cast to a KeyStroke.
     * @param key Action.NAME, Action.SHORT_DESCRIPTION etc...
     * @param propertyValue
     */
    private void setLoadedValue(String key, Object propertyValue) {

        if (key == null || propertyValue == null) {
            return;
        }


        if (key.equals(GeneralAction.PROJECT_REQUIRMENTS)) {
            putValue(key, propertyValue.toString());
        } else if (key.equals(Action.LARGE_ICON_KEY)) {

            putValue(key, ViewUtils.createImageIcon(propertyValue.toString(), ""));
        } else {
            Object alteredvalue = key.equals(Action.ACCELERATOR_KEY) ? KeyStroke.getKeyStroke(
                    (String) propertyValue) : propertyValue;
            putValue(key, alteredvalue);
        }

    }

    public String getName() {
        return actionProperties.getProperty(command + ".Action.Name");
    }
}
