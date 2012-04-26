/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.menu;

import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import uk.ac.ebi.mnb.dialog.file.*;
import uk.ac.ebi.mnb.dialog.file.importation.ImportModelSeed;
import uk.ac.ebi.mnb.dialog.file.importation.ImportSBML;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.file.*;
import uk.ac.ebi.mnb.menu.popup.CloseProject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;


/**
 * FileMenu.java
 *
 *
 * @author johnmay @date Apr 13, 2011
 */
public class FileMenu
        extends ContextMenu {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FileMenu.class);

    private SaveAsProjectAction saveAs = new SaveAsProjectAction();

    private NewProjectAction newProjectAction = new NewProjectAction();

    private JMenu recent = new JMenu("Open Recent...");

    private ContextMenu importMenu;

    private ContextMenu exportMenu;

    private ContextResponder activeProject = new ContextResponder() {

        public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
            return active != null;
        }
    };


    public FileMenu() {
        super("File", MainView.getInstance());

        MainView view = MainView.getInstance();

          add(newProjectAction);
        add(create(NewMetabolite.class), activeProject);
        add(create(NewReaction.class), activeProject);
        add(create(NewGeneProduct.class), activeProject);
        add(new OpenAction(this));
        add(recent);
        add(new JSeparator());
        add(new CloseProject(true), activeProject);
        add(new SaveAction(), activeProject);
        add(saveAs, activeProject);
        add(new JSeparator());

        importMenu = new ImportMenu();
        exportMenu = new ExportMenu();
        add(importMenu);
        add(new ImportSBML(view), activeProject);
        add(new ImportXLSAction(), activeProject);
        add(new ImportENAXML(), activeProject);
        add(new JSeparator());
        add(exportMenu);
        add(new ExportSBMLAction(), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return active != null && (!active.getReactome().isEmpty() || !active.getMetabolome().isEmpty());
            }
        });


        rebuildRecentlyOpen();

    }

    private Color ACTIVE_TOP_GRADIENT_COLOR = new Color(0xc8c8c8);

    private Color ACTIVE_BOTTOM_GRADIENT_COLOR = new Color(0xbcbcbc);

    private Color INACTIVE_TOP_GRADIENT_COLOR = new Color(0xe9e9e9);

    private Color INACTIVE_BOTTOM_GRADIENT_COLOR = new Color(0xe4e4e4);


    public void rebuildRecentlyOpen() {
        recent.removeAll(); // could just add and remove items... but for now
        LinkedList<String> items = DefaultReconstructionManager.getInstance().getRecent();
        for (int i = items.size() - 1; i >= 0; i--) {
            String path = items.get(i);
            File file = new File(path);
            if (file.exists()) {
                recent.add(new JMenuItem(new OpenAction(this, file)));
            }
        }

    }

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


    @Override
    public void updateContext() {
        super.updateContext();
        importMenu.updateContext();
        exportMenu.updateContext();
    }


    /**
     * Import sub menu of File
     */
    private class ImportMenu extends ContextMenu {

        public ImportMenu() {

            super("Import...", MainView.getInstance());
            add(new ImportPeptidesAction(), activeProject);
            add(new ImportKGML(), activeProject);
            add(new ImportModelSeed(MainView.getInstance(), MainView.getInstance()), activeProject);
        }
    }


    /**
     * Export sub menu of File
     */
    private class ExportMenu extends ContextMenu {

        public ExportMenu() {
            super("Export...", MainView.getInstance());
            add(create(ExportStoichiometricMatrix.class), new ContextResponder() {

                public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                    return active != null && active.hasMatrix();
                }
            });
            add(new ExportMetabolitesMDL(MainView.getInstance()), activeProject);
        }
    }
}
