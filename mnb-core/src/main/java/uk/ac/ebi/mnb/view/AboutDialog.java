/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import uk.ac.ebi.visualisation.ViewUtils;


/**
 * AboutDialog.java
 * About dialog functions also as the splash screen, using the bool in the
 * constructor to indicate this sets the frame undecorated and adds a progress
 * bar to dialog
 *
 * @author johnmay
 * @date Apr 21, 2011
 */
public class AboutDialog extends JDialog {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(AboutDialog.class);
    private JLabel logo;
    private JLabel label;
    private JProgressBar progressBar;
    private static final int progressBarMax = 3;
    private int progressBarValue = 0;


    /**
     * Create a new about dialog/splash screen
     * @param isSplash Whether this will be the splash screen
     */
    public AboutDialog(boolean isSplash) {

        logo = new JLabel(new ImageIcon(ViewUtils.logo_128x128));
        label = new JLabel(
          "<html><b>Metabolic Network Builder</b><br/>John May<br/>EMBL-EBI<br/>2011</html>");
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, progressBarMax);
        progressBar.setBorderPainted(false);
        setBackground(ViewUtils.BACKGROUND);
        setTitle("About Metabolic Network Builder");
        setUndecorated(isSplash);
        setAlwaysOnTop(true);
        setResizable(false);
        add(logo, BorderLayout.WEST);
        add(label, BorderLayout.CENTER);
        if( isSplash ) {
            add(progressBar, BorderLayout.SOUTH);
        }
        label.setForeground(new Color(40, 40, 40));
        label.setFont(ViewUtils.HELVATICA_NEUE_PLAIN_15);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pack();
        setLocationRelativeTo(null);
    }


    public JProgressBar getProgressBar() {
        return progressBar;
    }


    public void increment() {
        progressBar.setValue(++progressBarValue);
    }


}

