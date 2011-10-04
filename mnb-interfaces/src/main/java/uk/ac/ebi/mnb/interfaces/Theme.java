/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.interfaces;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author johnmay
 */
public interface Theme {

    public Color getWarningForeground();

    public Color getForeground();

    public Color getEmphasisedForeground();

    public Color getAltForeground();

    public Color getBackground();

    public Color getDialogBackground();

    public Font getBodyFont();

    public Font getHeaderFont();

    public Font getLinkFont();

    public float getDialogOpacity();
}
