/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import java.awt.Color;
import uk.ac.ebi.mnb.menu.file.ImportSBMLAction;
import uk.ac.ebi.mnb.menu.file.SaveAsProjectAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import uk.ac.ebi.mnb.menu.file.ExportSIFAction;
import uk.ac.ebi.mnb.menu.file.ExportSBMLAction;
import uk.ac.ebi.mnb.menu.file.ImportPeptidesAction;
import uk.ac.ebi.mnb.menu.file.ImportXLSAction;
import uk.ac.ebi.mnb.menu.file.NewProjectAction;
import uk.ac.ebi.mnb.menu.file.OpenProjectAction;
import uk.ac.ebi.mnb.menu.file.SaveProjectAction;

/**
 * FileMenu.java
 *
 *
 * @author johnmay
 * @date Apr 13, 2011
 */
public class FileMenu
        extends ClearMenu {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FileMenu.class);
    private SaveAsProjectAction saveAs = new SaveAsProjectAction();
    private NewProjectAction newProjectAction = new NewProjectAction();

    public FileMenu() {
        super("File");
        add(new DynamicMenuItem(newProjectAction));
        add(new DynamicMenuItem(new OpenProjectAction()));
        add(new DynamicMenuItem(new SaveProjectAction()));
        add(new DynamicMenuItem(saveAs));
        add(new JSeparator());
        add(new ImportMenu());
        add(new DynamicMenuItem(new ImportSBMLAction()));
        add(new DynamicMenuItem(new ImportXLSAction()));
        add(new JSeparator());
        add(new ExportMenu());
        add(new ExportSBMLAction());



    }
    private Color ACTIVE_TOP_GRADIENT_COLOR = new Color(0xc8c8c8);
    private Color ACTIVE_BOTTOM_GRADIENT_COLOR = new Color(0xbcbcbc);
    private Color INACTIVE_TOP_GRADIENT_COLOR = new Color(0xe9e9e9);
    private Color INACTIVE_BOTTOM_GRADIENT_COLOR = new Color(0xe4e4e4);

//    @Override
//    protected void paintBorder( Graphics g ) {
//        //super.paintBorder( g );
//    }
//
//    @Override
//    protected void paintComponent( Graphics g ) {
//        Graphics2D g2 = ( Graphics2D ) g;
//        boolean containedInActiveWindow = WindowUtils.isParentWindowFocused( this );
//        Color topColor = containedInActiveWindow
//                         ? ACTIVE_TOP_GRADIENT_COLOR : INACTIVE_TOP_GRADIENT_COLOR;
//        Color bottomColor = containedInActiveWindow
//                            ? ACTIVE_BOTTOM_GRADIENT_COLOR : INACTIVE_BOTTOM_GRADIENT_COLOR;
//        GradientPaint paint = new GradientPaint( 0 , 1 , topColor , 0 , getHeight() , bottomColor );
//        g2.setPaint( paint );
//        g2.fillRect( 0 , 0 , getWidth() , getHeight() );
//        super.paintComponent( g );
//    }
    public NewProjectAction getNewProjectAction() {
        return newProjectAction;
    }

    public void promptForSave() {
        saveAs.activateActions();
    }

    /**
     * Import sub menu of File
     */
    private class ImportMenu extends JMenu {

        public ImportMenu() {

            super("Import Data");
            add(new DynamicMenuItem(new ImportPeptidesAction()));
        }
    }

    /**
     * Export sub menu of File
     */
    private class ExportMenu extends JMenu {

        public ExportMenu() {
            super("Export");
            add(new DynamicMenuItem(new ExportSIFAction()));
            add(new JMenuItem("Selected Metabolites (.mdl)"));
            add(new JMenuItem("Selected Metabolites (.sbml)"));
            add(new JMenuItem("Selected Proteins (.fasta)"));
            add(new JMenuItem("Selected Reactions (.sbml)"));
            add(new JMenuItem("Selected Reactions (.rxn)"));
        }
    }
}
