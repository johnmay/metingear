/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package uk.ac.ebi.mnb.view.entity;

import java.awt.CardLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mnb.view.old.MatrixView;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import uk.ac.ebi.core.ReconstructionManager;
import mnb.view.old.MatrixModel;
import uk.ac.ebi.mnb.view.entity.metabolite.MetaboliteView;
import uk.ac.ebi.mnb.view.entity.protein.ProteinView;
import uk.ac.ebi.mnb.view.entity.reaction.ReactionsView;
import uk.ac.ebi.mnb.view.entity.search.SearchView;
import uk.ac.ebi.mnb.view.entity.tasks.TaskView;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.metabolomes.core.gene.GeneProduct;
import uk.ac.ebi.metabolomes.run.RunnableTask;
import uk.ac.ebi.mnb.interfaces.EntityView;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.search.SearchManager;

/**
 * ProjectPanel.java
 * ProjectPanel is the main panel view of the application. The object extends JTabbedPane
 * and manages access to the BrowserSplitPane, Reaction, Matrix and Pathway views
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public class ProjectView
        extends JPanel implements ViewController {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
            ProjectView.class);
    // underlying tab components
    private JScrollPane matrixPane;
    private MatrixView matrix = null;
    private ReactionsView reactions = null;
    private MetaboliteView metabolites = null;
//    private BrowserSplitPane productBrowser;
    private ProteinView products = null;
    private TaskView tasks = null;
    private SearchView search = null;
    private CardLayout layout;
    private Map<String, AbstractEntityView> viewMap;

    public ProjectView() {

//        productBrowser = BrowserSplitPane.getInstance();
        products = new ProteinView();
        reactions = new ReactionsView();
        metabolites = new MetaboliteView();
        tasks = new TaskView();
        search = new SearchView();

        layout = new CardLayout();
        setLayout(layout);
//        add(productBrowser, productBrowser.getName());
        add(products, products.getClass().getSimpleName());
        add(reactions, reactions.getClass().getSimpleName());
        add(metabolites, metabolites.getClass().getSimpleName());
        add(tasks, tasks.getClass().getSimpleName());
        add(search, search.getClass().getSimpleName());


        viewMap = new HashMap();
        viewMap.put(Metabolite.BASE_TYPE, metabolites);
        viewMap.put(Reaction.BASE_TYPE, reactions);
        viewMap.put(GeneProduct.BASE_TYPE, products);
        viewMap.put(RunnableTask.BASE_TYPE, tasks);


    }

    /**
     * Returns the currently active view
     * @return
     */
    public EntityView getActiveView() {

        for (AbstractEntityView pane : Arrays.asList(reactions, metabolites)) {
            if (pane != null && pane.isVisible()) {
                return pane;
            }
        }

        // might be problamatic
        return null;

    }

    public void setProductView() {
        layout.show(this, products.getClass().getSimpleName());
    }

    public void setReactionView() {
        layout.show(this, reactions.getClass().getSimpleName());
    }

    public void setMetaboliteView() {
        layout.show(this, metabolites.getClass().getSimpleName());
    }

    public void setTaskView() {
        layout.show(this, tasks.getClass().getSimpleName());
    }

    public void setSearchView() {
        layout.show(this, search.getClass().getSimpleName());
    }

    /**
     * 
     * Updates all child views (products, metabolites, reactions and tasks) calling
     * {@see EntityView#update()}
     *
     */
    public boolean update() {

        ReconstructionManager manager = ReconstructionManager.getInstance();
        Reconstruction reconstruction = manager.getActiveReconstruction();

        if (reconstruction == null) {
            return false;
        }

        logger.info("Sending update signal to all views");

        products.update();
        metabolites.update();
        reactions.update();
        tasks.update();



        try {
            // don't update search view (getSearchView().update() for that) but update
            // the indexer
            SearchManager.getInstance().updateCurrentIndex(reconstruction);
        } catch (CorruptIndexException ex) {
            logger.info(ex.getMessage());
            MainView.getInstance().addWarningMessage("Unable to index component for searching");
        } catch (LockObtainFailedException ex) {
            logger.info(ex.getMessage());
            MainView.getInstance().addWarningMessage("Unable to index component for searching");
        } catch (IOException ex) {
            logger.info(ex.getMessage());
            MainView.getInstance().addWarningMessage("Unable to index component for searching");
        }


        return true; // update successful

    }

    /**
     *
     * Convenience method for accessing the currently selected entity in the underlying view and
     * table. If more then one entity is selected the first selection is returned (as per JTable).
     *
     * This method combines calls to {@see getActiveView()} and {@see EntityView#getSelectedEntity()}
     * 
     * @return
     *
     */
    public AnnotatedEntity getSelectedEntity() {
        EntityView view = getActiveView();
        if (view != null) {
            return view.getSelectedEntity();
        }
        return null;
    }

    /**
     * 
     * Returns all currently selected entities in the active child view. 
     *
     * @return
     *
     */
    public Collection<AnnotatedEntity> getSelection() {

        EntityView view = getActiveView();

        if (view != null) {
            return view.getSelection();
        }

        return new ArrayList();

    }

    /**
     *
     * Returns all currently selected entities in the active child view that
     * are of class 'type'
     *
     * @param type
     * @return
     */
    public List<AnnotatedEntity> getActiveEntities(Class type) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    public ProteinView getProductView() {
        return products;
    }

    public MetaboliteView getMetaboliteView() {
        return metabolites;
    }

    public ReactionsView getReactionView() {
        return reactions;
    }

    public TaskView getTaskView() {
        return tasks;
    }

    public SearchView getSearchView() {
        return search;
    }

    /**
     * 
     * Changes the view and selects the given entity
     *
     * @param entity
     * 
     */
    public boolean setSelection(AnnotatedEntity entity) {

        AbstractEntityView view = viewMap.get(entity.getBaseType());

        layout.show(this, view.getClass().getSimpleName());

        view.setSelection(entity);

        return true; // succesful

    }

    /**
     * At the moment sets view to that of first item and then selects all provided. This method presumes
     * all entities are the same
     * @param entities
     * @return
     */
    public boolean setSelection(Collection<? extends AnnotatedEntity> entities) {

        if (entities.isEmpty()) {
            return false;
        }

        AnnotatedEntity entity = entities.iterator().next();

        AbstractEntityView view = viewMap.get(entity.getBaseType());

        layout.show(this, view.getClass().getSimpleName());

        return view.setSelection(entities);
    }

    /* deprecated methods */
    /**
     * Sets the tabbed pane view to the matrix view
     */
    @Deprecated
    public void setMatrixView() {
        if (matrix == null) {
            return;
        }
        //setSelectedComponent( matrixPane );
    }

    @Deprecated
    public void addMatrixView(MatrixModel model) {
        matrix = new MatrixView(model);
        matrixPane = new JScrollPane(matrix);
        //addTab( "Matrix View" , matrixPane );
        setMatrixView();
    }

    /**
     * Returns the instance of matrix view currently held in the project panel. This
     * will return null if no matrix view is instantiated
     * @return MatrixView object
     */
    @Deprecated
    public MatrixView getMatrixView() {
        return matrix;
    }
}
