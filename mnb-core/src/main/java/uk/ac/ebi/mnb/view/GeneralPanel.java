/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view;

import java.awt.LayoutManager;
import javax.swing.JPanel;
import uk.ac.ebi.mnb.settings.Settings;

/**
 * GeneralPanel.java
 * A general panel class with some convenience constructors
 *
 * @author johnmay
 * @date May 11, 2011
 */
public class GeneralPanel
        extends JPanel {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
            GeneralPanel.class);

    public GeneralPanel(LayoutManager layout) {
        super(layout);
        setBackground(Settings.getInstance().getTheme().getBackground());
    }

    public GeneralPanel() {
        setBackground(Settings.getInstance().getTheme().getBackground());
    }

    
}
