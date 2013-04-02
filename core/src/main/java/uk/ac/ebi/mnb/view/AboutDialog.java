/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.view;

import uk.ac.ebi.caf.component.theme.ThemeManager;
import uk.ac.ebi.caf.utility.ResourceUtility;

import javax.swing.*;
import java.awt.*;


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

        logo = new JLabel(ResourceUtility.getIcon("/uk/ac/ebi/chemet/render/images/networkbuilder_256x256.png"));
        label = new JLabel(
          "<html><b>Metingear</b><br/>John May<br/>EMBL-EBI<br/>2013</html>");
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, progressBarMax);
        progressBar.setBorderPainted(false);
        setBackground(ThemeManager.getInstance().getTheme().getBackground());
        setTitle("About Metingear");
        setUndecorated(isSplash);
        setAlwaysOnTop(true);
        setResizable(false);
        add(logo, BorderLayout.WEST);
        add(label, BorderLayout.CENTER);
        if( isSplash ) {
            add(progressBar, BorderLayout.SOUTH);
        }
        label.setForeground(new Color(40, 40, 40));
        label.setFont(ThemeManager.getInstance().getTheme().getBodyFont());
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

