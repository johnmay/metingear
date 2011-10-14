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
import uk.ac.ebi.mnb.view.entity.protein.ProductView;
import uk.ac.ebi.mnb.view.entity.reaction.ReactionView;
import uk.ac.ebi.mnb.view.entity.search.SearchView;
import uk.ac.ebi.mnb.view.entity.tasks.TaskView;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.metabolomes.core.gene.OldGeneProduct;
import uk.ac.ebi.chemet.io.external.RunnableTask;
import uk.ac.ebi.core.AbstractGeneProduct;
import uk.ac.ebi.core.ProteinProduct;
import uk.ac.ebi.core.RNAProduct;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.GeneProduct;
import uk.ac.ebi.mnb.core.SelectionMap;
import uk.ac.ebi.mnb.interfaces.EntityView;
import uk.ac.ebi.mnb.interfaces.SelectionManager;
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
    private ReactionView reactions = null;
    private MetaboliteView metabolites = null;
//    private BrowserSplitPane productBrowser;
    private ProductView products = null;
    private TaskView tasks = null;
    private SearchView search = null;
    private CardLayout layout;
    private Map<String, AbstractEntityView> viewMap;
    private SelectionManager selection = new SelectionMap();


    public ProjectView() {

//        productBrowser = BrowserSplitPane.getInstance();
        products = new ProductView();
        reactions = new ReactionView();
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
        
        viewMap.put(ProteinProduct.BASE_TYPE, products);
        viewMap.put(RNAProduct.BASE_TYPE, products);

        viewMap.put(RunnableTask.BASE_TYPE, tasks);


    }

    /**
     * Returns the currently active view
     * @return
     */
    public EntityView getActiveView() {

        for (AbstractEntityView pane : Arrays.asList(reactions, metabolites, products)) {
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
     * Returns the selection manager for the active view. If no view is active
     * then an empty selection is returned
     *
     * @return
     */
    public SelectionManager getSelection() {

        EntityView view = getActiveView();

        if (view != null) {
            return view.getSelection();
        }

        return selection.clear();

    }

    public ProductView getProductView() {
        return products;
    }

    public MetaboliteView getMetaboliteView() {
        return metabolites;
    }

    public ReactionView getReactionView() {
        return reactions;
    }

    public TaskView getTaskView() {
        return tasks;
    }

    public SearchView getSearchView() {
        return search;
    }

    /**
     * At the moment sets view to that of first item and then selects all provided. This method presumes
     * all entities are the same
     * @param entities
     * @return
     */
    @Override
    public boolean setSelection(SelectionManager selection) {

        if(selection.isEmpty()){
            return false;
        }

        AnnotatedEntity entity = selection.getFirstEntity();

        AbstractEntityView view = viewMap.get(entity.getBaseType());

        layout.show(this, view.getClass().getSimpleName());

        return view.setSelection(selection);
        
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
