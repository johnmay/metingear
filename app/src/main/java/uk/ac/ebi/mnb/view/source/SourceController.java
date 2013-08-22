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
package uk.ac.ebi.mnb.view.source;

import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListClickListener;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.explodingpixels.widgets.PopupMenuCustomizer;
import uk.ac.ebi.chemet.render.source.CollectionSourceItem;
import uk.ac.ebi.chemet.render.source.EntitySourceItem;
import uk.ac.ebi.chemet.render.source.EntitySubset;
import uk.ac.ebi.chemet.render.source.ReconstructionSourceItem;
import uk.ac.ebi.chemet.render.source.TaskSourceItem;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Entity;
import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reaction;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.tool.task.RunnableTask;
import uk.ac.ebi.metingeer.interfaces.menu.ContextAction;
import uk.ac.ebi.mnb.core.TaskManager;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.mnb.menu.popup.CloseProject;
import uk.ac.ebi.mnb.menu.popup.SetActiveProject;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTable;
import uk.ac.ebi.mnb.view.entity.ProjectView;
import uk.ac.ebi.mnb.view.entity.general.GeneralTable;
import uk.ac.ebi.mnb.view.entity.general.GeneralTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * SourceController.java – MetabolicDevelopmentKit – Jun 3, 2011 Class is a
 * wrapper around SoureListModel from Mac Widgets creating the child components
 *
 * @author johnmay <johnmay@ebi.ac.uk, john.wilkinsonmay@gmail.com>
 */
public class SourceController
        implements SourceListSelectionListener,
                   SourceListClickListener,
                   PopupMenuCustomizer {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
                    SourceController.class);

    public SourceListModel model;

    private SourceListCategory reconstructions;

    private SourceListCategory reconstruction;

    private SourceListItem products;

    private SourceListItem metabolites;

    private SourceListItem reactions;

    private SourceListItem pathways;

    private SourceListCategory tasks;

    private SourceListCategory collections;

    private SourceListItem genes;

    private SetActiveProject setActiveProject = new SetActiveProject();

    private Object selected;

    private List<EntitySourceItem> items = new ArrayList<EntitySourceItem>(); // list of items to update

    private Map<AnnotatedEntity, EntitySourceItem> itemMap = new IdentityHashMap<AnnotatedEntity, EntitySourceItem>();

    private SourceList source;

    private JPopupMenu popupMenu = new JPopupMenu();

    public SourceController() {

        model = new SourceListModel();

        reconstructions = new SourceListCategory("Reconstructions");
        reconstruction = new SourceListCategory("Active Reconstruction");
        collections = new SourceListCategory("Collections");
        products = new SourceListItem("Gene Products");
        metabolites = new SourceListItem("Metabolites");
        reactions = new SourceListItem("Reactions");
        tasks = new SourceListCategory("Tasks");
        genes = new SourceListItem("Genes");

        // could put genes/metabolites ect under an active project category
        model.addCategory(reconstructions);
        model.addCategory(reconstruction);
        model.addItemToCategory(genes, reconstruction);
        model.addItemToCategory(products, reconstruction);
        model.addItemToCategory(metabolites, reconstruction);
        model.addItemToCategory(reactions, reconstruction);
        model.addCategory(tasks);
        model.addCategory(collections);
    }

    public void setSelected(Class<? extends Entity> c) {
        if (Metabolite.class.isAssignableFrom(c)) {
            setSelected(metabolites);
        } else if (Reaction.class.isAssignableFrom(c)) {
            setSelected(reactions);
        } else if (GeneProduct.class.isAssignableFrom(c)) {
            setSelected(products);
        } else if (Gene.class.isAssignableFrom(c)) {
            setSelected(genes);
        }
    }

    public void setSelected(SourceListItem item) {
        // check the item isn't currently selected
        if (!isSelected(item)) {
            source.setSelectedItem(item);
        }
    }

    public boolean isSelected(SourceListItem item) {
        if (source == null) {
            return false;
        }
        return item.equals(source.getSelectedItem());
    }


    public void setSource(SourceList source) {
        this.source = source;
    }

    public void cleanModel() {

        for (SourceListItem item : Arrays
                .asList(genes, products, metabolites, reactions)) {
            removeLeaves(item);
        }

    }


    public void removeLeaves(SourceListItem item) {
        List<SourceListItem> children = item.getChildItems();

        for (int i = 0; i < children.size(); i++) {
            SourceListItem child = children.get(i);
            //            List<SourceListItem> grandchildren = child.getChildItems();
            //            if (!grandchildren.isEmpty()) {
            //                removeLeaves(child);
            //            } else {
            model.removeItemFromItem(child, item);
            // }
        }


    }


    /**
     * Updates all currently available items to that in the active
     * reconstruction.
     */
    public boolean update() {


        // metabolite first
        ReconstructionManager manager = DefaultReconstructionManager
                .getInstance();

        // with each update item we remove it from the item collector. then at the end all items
        // still in the collector are removed
        Set<AnnotatedEntity> itemCollector = new HashSet<AnnotatedEntity>();
        itemCollector.addAll(itemMap.keySet());
        genes.setCounterValue(0);
        products.setCounterValue(0);
        metabolites.setCounterValue(0);
        reactions.setCounterValue(0);

        if (!manager.isEmpty()) {

            Reconstruction active = manager.active();

            // reconstructions
            for (Reconstruction recon : manager.reconstructions()) {
                if (!itemMap.containsKey(recon)) {
                    EntitySourceItem item = new ReconstructionSourceItem(recon,
                                                                         reconstructions);
                    itemMap.put(recon, item);
                    model.addItemToCategory(item, reconstructions);
                }
                itemCollector.remove(recon);
                itemMap.get(recon).update();
            }


            for (int i = 0; i < collections.getItemCount(); i++) {
                model.removeItemFromCategoryAtIndex(collections, i);
            }

            if (active != null) {
                for (EntityCollection subset : active.getSubsets()) {
                    if (subset instanceof EntitySubset) {
                        model.addItemToCategory(new CollectionSourceItem((EntitySubset) subset), collections);
                    }
                }
            }


            if (active != null) {
                genes.setCounterValue(active.genome().genes().size());
                products.setCounterValue(active.proteome().size());
                metabolites.setCounterValue(active.metabolome().size());
                reactions.setCounterValue(active.reactome().size());
            }

        }


        // task are independant from reconstruction
        // products
        for (RunnableTask t : TaskManager.getInstance().getTasks()) {
            if (!itemMap.containsKey(t)) {
                EntitySourceItem item = new TaskSourceItem(t, tasks);
                itemMap.put(t, item);
                model.addItemToCategory(item, tasks);
            }
            itemMap.get(t).update();
            itemCollector.remove(t);

        }

        logger.debug("Removing objects: " + itemCollector.size());
        // remove collected items
        for (AnnotatedEntity deprecatedEntity : itemCollector) {
            EntitySourceItem item = itemMap.get(deprecatedEntity);
            if (item != null) {
                item.remove(model);
            }
            itemMap.remove(deprecatedEntity);
        }

        return true;

    }


    /**
     * Mouse event listeners
     *
     * @param item
     */
    @Override
    public void sourceListItemSelected(SourceListItem item) {
        sourceListItemClicked(item, Button.LEFT, 1);
    }


    @Override
    public void sourceListItemClicked(SourceListItem item,
                                      Button button,
                                      int clickCount) {


        popupMenu.setVisible(false);

        if (button.equals(Button.RIGHT)) {
            customizePopup(popupMenu);
            popupMenu.setVisible(true);
            popupMenu.setLocation(MouseInfo.getPointerInfo().getLocation());
            return;
        }

        selected = item;

        ProjectView view = (ProjectView) MainView.getInstance()
                                                 .getViewController();

        //        if (item instanceof EntitySourceItem && !(item instanceof ReconstructionSourceItem)) {
        //            EntityCollection selection = view.getSelection();// reuse view selection object
        //            selection.clear().add(((EntitySourceItem) item).getEntity());
        //            view.setSelection(selection);
        //        }
        if (item instanceof CollectionSourceItem) {

            CollectionSourceItem collectionItem = (CollectionSourceItem) item;
            EntitySubset subset = collectionItem.getSubset();

            view.setGenericView();

            AbstractEntityTable table = (AbstractEntityTable) view
                    .getActiveView().getTable();

            if (table instanceof GeneralTable) {
                ((GeneralTableModel) table.getModel())
                        .setGeneralEntities(subset.getEntities());
            }

            view.getActiveView().update();

        } else if (item instanceof ReconstructionSourceItem && clickCount > 1) {
            setActiveProject.setEnabled(setActiveProject.getContext(selected));
            setActiveProject.actionPerformed(null);
        } else if (item instanceof SourceListItem) {
            if (item == metabolites) {
                view.setMetaboliteView();
            } else if (item == reactions) {
                view.setReactionView();
            } else if (item == products) {
                view.setProductView();
            } else if (item == genes) {
                view.setGeneView();
            } else if (item instanceof EntitySourceItem
                    && ((EntitySourceItem) item)
                    .getEntity() instanceof RunnableTask) {
                view.setTaskView();
            } else {
                logger.debug("did not handle source item click: " + item
                        .getText());
            }
        }

    }


    @Override
    public void sourceListCategoryClicked(SourceListCategory category,
                                          Button button,
                                          int clickCount) {
        if (category.equals(tasks)) {
            ((ProjectView) MainView.getInstance().getViewController())
                    .setTaskView();
        }
    }

    List<ContextAction> actions = new ArrayList<ContextAction>();


    public void customizePopup(JPopupMenu popup) {

        // if there's no item add them all
        if (popup.getComponents().length == 0) {
            for (Action action : Arrays.asList(setActiveProject,
                                               new CloseProject())) {
                actions.add((ContextAction) action);
                popup.add(action);
            }
        }

        // set active/inactive given the context of the current selection
        for (ContextAction action : actions) {
            ((AbstractAction) action).setEnabled(action.getContext(selected));
        }

    }
}
