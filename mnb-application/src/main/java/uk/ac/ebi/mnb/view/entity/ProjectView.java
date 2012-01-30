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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import uk.ac.ebi.core.ReconstructionManager;
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
import uk.ac.ebi.chemet.io.external.RunnableTask;
import uk.ac.ebi.core.GeneImplementation;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.Multimer;
import uk.ac.ebi.core.ProteinProduct;
import uk.ac.ebi.core.RNAProduct;
import uk.ac.ebi.core.RibosomalRNA;
import uk.ac.ebi.core.TransferRNA;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.interfaces.Gene;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.interfaces.EntityView;
import uk.ac.ebi.interfaces.entities.EntityCollection;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.ViewInfo;
import uk.ac.ebi.mnb.view.entity.gene.GeneView;
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
        extends JPanel
        implements ViewController {

    private static final org.apache.log4j.Logger LOGGER =
                                                 org.apache.log4j.Logger.getLogger(
            ProjectView.class);
    // underlying  components
    private ReactionView reactions = null;
    private MetaboliteView metabolites = null;
    private GeneView genes = null;
    private ProductView products = null;
    private TaskView tasks = null;
    private SearchView search = null;
    private CardLayout layout;
    private Map<String, AbstractEntityView> viewMap;
    private EntityCollection selection = new EntityMap();
    private ViewInfo selector;

    public ProjectView() {

        products = new ProductView();
        reactions = new ReactionView();
        metabolites = new MetaboliteView();
        tasks = new TaskView();
        search = new SearchView();
        genes = new GeneView();

        layout = new CardLayout();
        setLayout(layout);
        add(products, products.getClass().getSimpleName());
        add(reactions, reactions.getClass().getSimpleName());
        add(metabolites, metabolites.getClass().getSimpleName());
        add(tasks, tasks.getClass().getSimpleName());
        add(search, search.getClass().getSimpleName());
        add(genes, genes.getClass().getSimpleName());


        viewMap = new HashMap();
        viewMap.put(Metabolite.BASE_TYPE, metabolites);
        viewMap.put(Reaction.BASE_TYPE, reactions);

        viewMap.put(ProteinProduct.BASE_TYPE, products);
        viewMap.put(RibosomalRNA.BASE_TYPE, products);
        viewMap.put(TransferRNA.BASE_TYPE, products);
        viewMap.put(RNAProduct.BASE_TYPE, products);
        viewMap.put(Multimer.BASE_TYPE, products);

        viewMap.put(RunnableTask.BASE_TYPE, tasks);
        viewMap.put(GeneImplementation.BASE_TYPE, genes);

    }

    public void setBottomBarLabel(JLabel label) {
        for (AbstractEntityView view : viewMap.values()) {
            view.setBottomBarLabel(label);
        }
    }

    /**
     * Sets the association with the buttons to change the view
     * @param selector
     */
    public void setViewSelector(ViewInfo selector) {
        this.selector = selector;
    }

    /**
     * Returns the currently active view
     * @return
     */
    public EntityView getActiveView() {

        for (AbstractEntityView pane : Arrays.asList(reactions, metabolites, products, genes)) {
            if (pane != null && pane.isVisible()) {
                return pane;
            }
        }

        // might be problamatic
        return null;

    }

    public void setProductView() {
        layout.show(this, products.getClass().getSimpleName());
        selector.setSelected(ProteinProduct.BASE_TYPE);
    }

    public void setReactionView() {
        layout.show(this, reactions.getClass().getSimpleName());
        selector.setSelected(Reaction.BASE_TYPE);
    }

    public void setMetaboliteView() {
        layout.show(this, metabolites.getClass().getSimpleName());
        selector.setSelected(Metabolite.BASE_TYPE);
    }

    public void setGeneView() {
        layout.show(this, genes.getClass().getSimpleName());
        selector.setSelected(GeneImplementation.BASE_TYPE);
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
        Reconstruction reconstruction = manager.getActive();

        if (reconstruction == null) {
            products.clear();
            metabolites.clear();
            reactions.clear();
            genes.clear();
            return false;
        }

        LOGGER.info("Sending update signal to all views");

        products.update();
        metabolites.update();
        reactions.update();
        tasks.update();
        genes.update();



        try {
            // don't update search view (getSearchView().update() for that) but update
            // the indexer
            SearchManager.getInstance().updateCurrentIndex(reconstruction);
        } catch (CorruptIndexException ex) {
            LOGGER.info(ex.getMessage());
            MainView.getInstance().addWarningMessage("Unable to index component for searching");
        } catch (LockObtainFailedException ex) {
            LOGGER.info(ex.getMessage());
            MainView.getInstance().addWarningMessage("Unable to index component for searching");
        } catch (IOException ex) {
            LOGGER.info(ex.getMessage());
            MainView.getInstance().addWarningMessage("Unable to index component for searching");
        }


        return true; // update successful

    }

    @Override
    public boolean update(EntityCollection selection) {

        LOGGER.info("sending targeted updated...");

        if (selection.getGeneProducts().isEmpty() == false) {
            products.update(selection);
        }
        if (selection.hasSelection(Metabolite.class)) {
            metabolites.update(selection);
        }
        if (selection.hasSelection(MetabolicReaction.class)) {
            reactions.update(selection);
        }
        if (selection.hasSelection(RunnableTask.class)) {
            tasks.update(selection);
        }
        if (selection.hasSelection(Gene.class)) {
            genes.update(selection);
        }

        return true; // for now
    }

    /**
     * Returns the selection manager for the active view. If no view is active
     * then an empty selection is returned
     *
     * @return
     */
    public EntityCollection getSelection() {

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
    public boolean setSelection(EntityCollection selection) {

        if (selection.isEmpty()) {
            return false;
        }

        LOGGER.debug("Setting selection in table: " + selection.getEntities());

        AnnotatedEntity entity = selection.getFirstEntity();

        AbstractEntityView view = viewMap.get(entity.getBaseType());

        layout.show(this, view.getClass().getSimpleName());

        selector.setSelected(entity.getBaseType());

        return view.setSelection(selection);

    }
}
