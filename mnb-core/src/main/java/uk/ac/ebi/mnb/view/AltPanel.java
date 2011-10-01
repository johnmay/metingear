/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view;

import java.awt.LayoutManager;
import javax.swing.JPanel;
import uk.ac.ebi.mnb.core.ApplicationPreferences;
import uk.ac.ebi.mnb.view.ViewUtils;


/**
 * GeneralPanel.java
 * A general panel class with some convenience constructors
 *
 * @author johnmay
 * @date May 11, 2011
 */
public class AltPanel
  extends JPanel {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      AltPanel.class);


    public AltPanel(LayoutManager layout) {
        super(layout);
        setBackground(ApplicationPreferences.getInstance().getTheme().getAltBackground());
    }


    public AltPanel() {
        setBackground(ApplicationPreferences.getInstance().getTheme().getAltBackground());
    }


}

