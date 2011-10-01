/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import com.explodingpixels.widgets.WindowUtils;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JMenuBar;

/**
 * MainMenuBar.java
 *
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class MainMenuBar
    extends JMenuBar {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( MainMenuBar.class );
    private FileMenu file = new FileMenu();
    private BuildMenu build = new BuildMenu();
    private ClearMenu view = new ViewMenu();
    private EditMenu edit = new EditMenu();
    private ClearMenu select = new ClearMenu( "Select" );
    private RunMenu run = new RunMenu();
    private ToolsMenu tools = new ToolsMenu();

    public MainMenuBar() {
        add( file );
        add( edit );
        add( build );
        add( run );
        add( view );
        add( tools );
        add( select );
        setBorderPainted( false );
    }

    public EditMenu getEditMenu() {
        return edit;
    }
    public BuildMenu getBuildMenu() {
        return build;
    }

    public FileMenu getFileMenu() {
        return file;
    }

    public RunMenu getRunMenu() {
        return run;
    }

    public void setActiveDependingOnRequirements() {
        build.setActiveDependingOnRequirements();
    }
    private Color ACTIVE_TOP_GRADIENT_COLOR = new Color( 0xc8c8c8 );
    private Color ACTIVE_BOTTOM_GRADIENT_COLOR = new Color( 0xbcbcbc );
    private Color INACTIVE_TOP_GRADIENT_COLOR = new Color( 0xe9e9e9 );
    private Color INACTIVE_BOTTOM_GRADIENT_COLOR = new Color( 0xe4e4e4 );

    @Override
    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = ( Graphics2D ) g;
        boolean containedInActiveWindow = WindowUtils.isParentWindowFocused( this );
        Color topColor = containedInActiveWindow
                         ? ACTIVE_TOP_GRADIENT_COLOR : INACTIVE_TOP_GRADIENT_COLOR;
        Color bottomColor = containedInActiveWindow
                            ? ACTIVE_BOTTOM_GRADIENT_COLOR : INACTIVE_BOTTOM_GRADIENT_COLOR;
        GradientPaint paint = new GradientPaint( 0 , 1 , topColor , 0 , getHeight() , bottomColor );
        g2.setPaint( paint );
        g2.fillRect( 0 , 0 , getWidth() , getHeight() );
    }
}
