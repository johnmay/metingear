/*
 *     This file is part of Metabolic Network Builder
 *
 *     Metabolic Network Builder is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.main;

import javax.swing.JPopupMenu;
import uk.ac.ebi.core.ReconstructionManager;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListClickListener;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.explodingpixels.widgets.PopupMenuCustomizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import mnb.view.old.TaskManager;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.chemet.render.source.EntitySourceItem;
import uk.ac.ebi.chemet.render.source.MetaboliteSourceItem;
import uk.ac.ebi.chemet.render.source.ProductSourceItem;
import uk.ac.ebi.chemet.render.source.ReactionSourceItem;
import uk.ac.ebi.chemet.render.source.ReconstructionSourceItem;
import uk.ac.ebi.chemet.render.source.TaskSourceItem;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.metabolomes.core.gene.GeneProduct;
import uk.ac.ebi.metabolomes.run.RunnableTask;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.menu.popup.CloseProject;
import uk.ac.ebi.mnb.menu.popup.SetActiveProject;
import uk.ac.ebi.mnb.menu.popup.ContextSensitiveAction;
import uk.ac.ebi.mnb.view.entity.ProjectView;

/**
 * SourceController.java – MetabolicDevelopmentKit – Jun 3, 2011
 * Class is a wrapper around SoureListModel from Mac Widgets creating the child components
 * @author johnmay <johnmay@ebi.ac.uk, john.wilkinsonmay@gmail.com>
 */
public class SourceController
        implements SourceListSelectionListener,
        SourceListClickListener,
        PopupMenuCustomizer {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(
            SourceController.class);
    public final SourceListModel model;
    private SourceListCategory reconstructions;
    private SourceListCategory activeProject;
    private SourceListItem products;
    private SourceListItem metabolites;
    private SourceListItem reactions;
    private SourceListItem pathways;
    private SourceListCategory tasks;
    private SetActiveProject setActiveProject = new SetActiveProject();
    private Object selected;
    private List<EntitySourceItem> items = new ArrayList(); // list of items to update
    private Map<AnnotatedEntity, EntitySourceItem> itemMap = new HashMap();

    public SourceController() {

        model = new SourceListModel();

        reconstructions = new SourceListCategory("Reconstructions");
        activeProject = new SourceListCategory("Active Reconstruction");
        products = new SourceListItem("Gene Products");
        metabolites = new SourceListItem("Metabolites");
        reactions = new SourceListItem("Reactions");
        pathways = new SourceListItem("Pathways");
        tasks = new SourceListCategory("Tasks");

        // could put genes/metabolites ect under an active project category
        model.addCategory(reconstructions);
        model.addCategory(activeProject);
        model.addItemToCategory(products, activeProject);
        model.addItemToCategory(metabolites, activeProject);
        model.addItemToCategory(reactions, activeProject);
        model.addItemToCategory(pathways, activeProject);
        model.addCategory(tasks);


    }

    /**
     * Updates all currently available items to that in the active reconstruction.
     */
    public boolean update() {


        // metabolite first
        ReconstructionManager manager = ReconstructionManager.getInstance();

        // with each update item we remove it from the item collector. then at the end all items
        // still in the collector are removed
        Set<AnnotatedEntity> itemCollector = new HashSet();
        itemCollector.addAll(itemMap.keySet());

        if (manager.hasProjects()) {

            // reconstructions
            for (int i = 0; i < manager.size(); i++) {
                Reconstruction reconstruction = manager.getProject(i);
                if (itemMap.containsKey(reconstruction) == false) {
                    EntitySourceItem item = new ReconstructionSourceItem(reconstruction,
                            reconstructions);
                    itemMap.put(reconstruction, item);
                    model.addItemToCategory(item, reconstructions);
                }
                itemCollector.remove(reconstruction);
                itemMap.get(reconstruction).update();
            }

            Reconstruction reconstruction = manager.getActiveReconstruction();

            // metabolites
            for (Metabolite m : reconstruction.getMetabolites()) {
                if (itemMap.containsKey(m) == false) {
                    EntitySourceItem item = new MetaboliteSourceItem(m, metabolites);
                    itemMap.put(m, item);
                    model.addItemToItem(item, metabolites);
                }
                itemMap.get(m).update();
                itemCollector.remove(m);

            }

            // reactions

            for (Reaction r : reconstruction.getReactions()) {
                if (itemMap.containsKey(r) == false) {
                    EntitySourceItem item = new ReactionSourceItem(r, reactions);
                    itemMap.put(r, item);
                    model.addItemToItem(item, reactions);
                }
                itemMap.get(r).update();
                itemCollector.remove(r);

            }


            // products
            for (GeneProduct p : reconstruction.getGeneProducts().getAllProducts()) {
                if (itemMap.containsKey(p) == false) {
                    EntitySourceItem item = new ProductSourceItem(p, products);
                    itemMap.put(p, item);
                    model.addItemToItem(item, products);
                }
                itemMap.get(p).update();
                itemCollector.remove(p);

            }



        }


        // task are independant from reconstruction
        // products
        for (RunnableTask t : TaskManager.getInstance().getTasks()) {
            System.out.println("task: " + t);
            if (itemMap.containsKey(t) == false) {
                EntitySourceItem item = new TaskSourceItem(t, tasks);
                itemMap.put(t, item);
                model.addItemToCategory(item, tasks);
            }
            itemMap.get(t).update();
            itemCollector.remove(t);

        }


        // remove collected items
        for (AnnotatedEntity deprecatedEntity : itemCollector) {
            EntitySourceItem item = itemMap.get(deprecatedEntity);
            item.remove(model);
            itemMap.remove(deprecatedEntity);
        }

        return true;

    }

    /**
     * Mouse event listeners
     * @param item
     */
    @Override
    public void sourceListItemSelected(SourceListItem item) {
        sourceListItemClicked(item, Button.LEFT, 1);
    }

    @Override
    public void sourceListItemClicked(SourceListItem item, Button button, int clickCount) {


        selected = item;

        ProjectView view = (ProjectView) MainView.getInstance().getViewController();

        if (item instanceof EntitySourceItem && !(item instanceof ReconstructionSourceItem)) {
            view.setSelection(((EntitySourceItem) item).getEntity());
        } else if (item instanceof ReconstructionSourceItem && clickCount > 1) {
            setActiveProject.setContext(selected);
            setActiveProject.actionPerformed(null);
        } else if (item instanceof SourceListItem) {
            if (item == metabolites) {
                view.setMetaboliteView();
            } else if (item == reactions) {
                view.setReactionView();
            } else if (item == products) {
                view.setProductView();
            } else {
                System.out.println("Unhandled item clicked: " + item.getText());
            }
        }
    }

    @Override
    public void sourceListCategoryClicked(SourceListCategory category, Button button, int clickCount) {
        if (category.equals(tasks)) {
            ((ProjectView)MainView.getInstance().getViewController()).setTaskView();
        }
    }
    List<ContextSensitiveAction> actions = new ArrayList();

    public void customizePopup(JPopupMenu popup) {

        ReconstructionManager manager = ReconstructionManager.getInstance();

        // if there's no item add them all
        if (popup.getComponents().length == 0) {
            for (Action action : Arrays.asList(setActiveProject,
                    new CloseProject())) {
                actions.add((ContextSensitiveAction) action);
                popup.add(action);
            }
        }

        // set active/inactive given the context of the current selection
        for (ContextSensitiveAction action : actions) {
            action.setContext(selected);
        }

    }
}
