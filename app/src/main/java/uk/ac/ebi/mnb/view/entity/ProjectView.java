/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
package uk.ac.ebi.mnb.view.entity;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import uk.ac.ebi.mdk.domain.entity.*;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.tool.task.RunnableTask;
import uk.ac.ebi.metingear.search.SearchManager;
import uk.ac.ebi.metingear.view.ViewToggle;
import uk.ac.ebi.mnb.core.EntityMap;
import uk.ac.ebi.mnb.interfaces.EntityView;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.view.entity.gene.GeneView;
import uk.ac.ebi.mnb.view.entity.general.GeneralView;
import uk.ac.ebi.mnb.view.entity.metabolite.MetaboliteView;
import uk.ac.ebi.mnb.view.entity.protein.ProductView;
import uk.ac.ebi.mnb.view.entity.reaction.ReactionView;
import uk.ac.ebi.mnb.view.entity.tasks.TaskView;
import uk.ac.ebi.mnb.view.source.SourceController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


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

    private GeneralView general = null;

    private CardLayout layout;

    private Map<Class<? extends Entity>, AbstractEntityView> viewMap;

    private ViewToggle selector;

    private EntityFactory factory = DefaultEntityFactory.getInstance();

    private EntityCollection selection = new EntityMap(factory);

    private SourceController controller;

    public ProjectView() {

        genes = new GeneView();
        products = new ProductView();
        reactions = new ReactionView();
        metabolites = new MetaboliteView();
        tasks = new TaskView();
        general = new GeneralView();

        layout = new CardLayout();
        setLayout(layout);
        add(products, GeneProduct.class.getName());
        add(reactions, Reaction.class.getName());
        add(metabolites, Metabolite.class.getName());
        add(tasks, tasks.getClass().getSimpleName());
        add(general, general.getClass().getSimpleName());
        add(genes, Gene.class.getName());

        viewMap = new HashMap<Class<? extends Entity>, AbstractEntityView>();
        viewMap.put(Metabolite.class, metabolites);
        viewMap.put(Reaction.class, reactions);

        viewMap.put(GeneProduct.class, products);

        viewMap.put(RunnableTask.class, tasks);
        viewMap.put(Gene.class, genes);

    }


    public void setBottomBarLabel(JLabel label) {
        for (AbstractEntityView view : viewMap.values()) {
            view.setBottomBarLabel(label);
        }
    }


    /**
     * Sets the association with the buttons to change the view
     *
     * @param selector
     */
    public void setViewSelector(ViewToggle selector) {
        if (selector == null)
            throw new NullPointerException("ViewToggle should not be null");
        this.selector = selector;
    }

    public void setSourceController(SourceController controller) {
        if (controller == null)
            throw new NullPointerException("SourceController should not be null");
        this.controller = controller;
    }

    /**
     * Returns the currently active view
     *
     * @return
     */
    public EntityView getActiveView() {

        for (AbstractEntityView pane : Arrays.asList(reactions, metabolites, products, genes, general)) {
            if (pane != null && pane.isVisible()) {
                return pane;
            }
        }

        // might be problamatic
        return null;

    }


    public void setView(Class<? extends Entity> c) {
        layout.show(this, factory.getRootClass(c).getName());
        if (controller != null)
            controller.setSelected(c);
        if (selector != null)
            selector.setSelected(c);
        // update available menu items
        MainView.getInstance().getJMenuBar().updateContext();
    }


    public void setProductView() {
        setView(GeneProduct.class);
    }


    public void setReactionView() {
        setView(Reaction.class);
    }


    public void setMetaboliteView() {
        setView(Metabolite.class);
    }


    public void setGeneView() {
        setView(Gene.class);
    }


    public void setTaskView() {
        layout.show(this, tasks.getClass().getSimpleName());
    }


    public void setGenericView() {
        layout.show(this, general.getClass().getSimpleName());
    }


    public void clear() {
        products.clear();
        metabolites.clear();
        reactions.clear();
        genes.clear();
    }

    /**
     * Updates all child views (products, metabolites, reactions and tasks) calling
     * {@see EntityView#update()}
     */
    public boolean update() {

        DefaultReconstructionManager manager = DefaultReconstructionManager.getInstance();
        Reconstruction reconstruction = manager.getActive();

        if (reconstruction == null) {
            clear();
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


    public GeneralView getSearchView() {
        return general;
    }


    /**
     * At the moment sets view to that of first item and then selects all provided. This method presumes
     * all entities are the same
     *
     * @param selection
     *
     * @return
     */
    @Override
    public boolean setSelection(EntityCollection selection) {

        if (selection.isEmpty()) {
            return false;
        }

        LOGGER.debug("Setting selection in table: " + selection.getEntities());

        AnnotatedEntity entity = selection.getFirstEntity();

        setView(entity.getClass());

        selector.setSelected(DefaultEntityFactory.getInstance().getRootClass(entity.getClass()));

        return getActiveView().setSelection(selection);

    }
}
