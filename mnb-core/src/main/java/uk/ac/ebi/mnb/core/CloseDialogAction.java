/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.core;

import com.community.xanadu.components.transition.CloseTransition;
import com.community.xanadu.components.transition.impl.FadeOutTransition;
import com.community.xanadu.components.transition.impl.PinchTransition;
import com.community.xanadu.utils.ThreadUtils;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;

/**
 * CloseDialogAction.java
 *
 *
 * @author johnmay
 * @date Apr 27, 2011
 */
public class CloseDialogAction extends GeneralAction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CloseDialogAction.class);
    private JDialog dialog;

    public CloseDialogAction(JDialog dialog) {
        super("CloseDialog");
        this.dialog = dialog;
    }

    public CloseDialogAction(JDialog dialog, boolean named) {
        super(named ? "CloseDialog" : "");
        this.dialog = dialog;
    }

    public void actionPerformed(ActionEvent e) {
        dialog.setVisible(false);
    }
}
