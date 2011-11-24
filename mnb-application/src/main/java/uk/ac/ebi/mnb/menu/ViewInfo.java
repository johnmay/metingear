/**
 * ViewSelector.java
 *
 * 2011.11.01
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.menu;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.LabeledComponentGroup;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.core.GeneImplementation;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.Multimer;
import uk.ac.ebi.core.ProteinProduct;
import uk.ac.ebi.core.RNAProduct;
import uk.ac.ebi.mnb.core.GeneralAction;
import uk.ac.ebi.ui.component.factory.LabelFactory;
import uk.ac.ebi.mnb.view.entity.ProjectView;
import uk.ac.ebi.visualisation.ViewUtils;

/**
 *          ViewSelector - 2011.11.01 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ViewInfo {

    private static final Logger LOGGER = Logger.getLogger(ViewInfo.class);
    private JToggleButton genes = new JToggleButton(new ViewGenes());
    private JToggleButton products = new JToggleButton(new ViewProducts());
    private JToggleButton metabolites = new JToggleButton(new ViewMetabolites());
    private JToggleButton reactions = new JToggleButton(new ViewReactions());
    private ProjectView controller;
    private Map<String, JToggleButton> buttonMap = new HashMap();
    private BottomBar bottombar = new BottomBar(BottomBarSize.SMALL);
    private JLabel info = LabelFactory.newLabel("");

    public ViewInfo(ProjectView controller) {
        this.controller = controller;
        controller.setBottomBarLabel(info);

        controller.setViewSelector(this);

        genes.setSelectedIcon(ViewUtils.getIcon("images/toolbar/gen-selected.png"));
        products.setSelectedIcon(ViewUtils.getIcon("images/toolbar/pro-selected.png"));
        metabolites.setSelectedIcon(ViewUtils.getIcon("images/toolbar/met-selected.png"));
        reactions.setSelectedIcon(ViewUtils.getIcon("images/toolbar/rxn-selected.png"));

        genes.putClientProperty("JButton.buttonType", "segmentedTextured");
        genes.putClientProperty("JButton.segmentPosition", "first");
        genes.setFocusable(false);

        products.putClientProperty("JButton.buttonType", "segmentedTextured");
        products.putClientProperty("JButton.segmentPosition", "middle");
        products.setFocusable(false);

        metabolites.putClientProperty("JButton.buttonType", "segmentedTextured");
        metabolites.putClientProperty("JButton.segmentPosition", "middle");
        metabolites.setFocusable(false);

        reactions.putClientProperty("JButton.buttonType", "segmentedTextured");
        reactions.putClientProperty("JButton.segmentPosition", "last");
        reactions.setFocusable(false);

        ButtonGroup group = new ButtonGroup();
        group.add(genes);
        group.add(metabolites);
        group.add(products);
        group.add(reactions);


        buttonMap.put(Metabolite.BASE_TYPE, metabolites);
        buttonMap.put(Reaction.BASE_TYPE, reactions);

        buttonMap.put(ProteinProduct.BASE_TYPE, products);
        buttonMap.put(RNAProduct.BASE_TYPE, products);
        buttonMap.put(Multimer.BASE_TYPE, products);

//        buttonMap.put(RunnableTask.BASE_TYPE, tasks);
        buttonMap.put(GeneImplementation.BASE_TYPE, genes);

        bottombar.addComponentToCenter(info);

    }

    public JComponent getButtonGroup() {
        return new LabeledComponentGroup("View", genes, products, metabolites, reactions).getComponent();
    }

    public BottomBar getBottomBar() {
        return bottombar;
    }

    /**
     * Sets the toggle for the provided base type
     */
    public void setSelected(String type) {
        if (buttonMap.get(type) != null) {
            for (JToggleButton button : buttonMap.values()) {
                button.setSelected(false);
            }
            buttonMap.get(type).setSelected(true);
            info.setText("");
            info.repaint();
        }
    }

    private class ViewGenes
            extends GeneralAction {

        public ViewGenes() {
            super("ViewGenes");
        }

        public void actionPerformed(ActionEvent e) {
            controller.setGeneView();
        }
    }

    private class ViewProducts
            extends GeneralAction {

        public ViewProducts() {
            super("ViewProducts");
        }

        public void actionPerformed(ActionEvent e) {
            controller.setProductView();
        }
    }

    private class ViewMetabolites
            extends GeneralAction {

        public ViewMetabolites() {
            super("ViewMetabolites");
        }

        public void actionPerformed(ActionEvent e) {
            controller.setMetaboliteView();
        }
    }

    private class ViewReactions
            extends GeneralAction {

        public ViewReactions() {
            super("ViewReactions");
        }

        public void actionPerformed(ActionEvent e) {
            controller.setReactionView();
        }
    }
}
