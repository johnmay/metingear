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
package uk.ac.ebi.metingear.view;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.LabeledComponentGroup;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.mdk.domain.entity.*;
import uk.ac.ebi.mnb.view.entity.ProjectView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;


/**
 * ViewSelector - 2011.11.01 <br>
 * Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class ViewToggle {

    private static final Logger LOGGER = Logger.getLogger(ViewToggle.class);

    private JToggleButton genes = new JToggleButton(new ViewGenes());

    private JToggleButton products = new JToggleButton(new ViewProducts());

    private JToggleButton metabolites = new JToggleButton(new ViewMetabolites());

    private JToggleButton reactions = new JToggleButton(new ViewReactions());

    private ProjectView controller;

    private Map<Class<? extends Entity>, JToggleButton> buttonMap = new HashMap();

    private BottomBar bottombar = new BottomBar(BottomBarSize.SMALL);

    private JLabel info      = LabelFactory.newLabel("");
    private JLabel viewLabel = LabelFactory.newLabel("View");


    public ViewToggle(ProjectView controller) {
        this.controller = controller;
        controller.setBottomBarLabel(info);

        controller.setViewSelector(this);

        genes.setSelectedIcon(ResourceUtility.getIcon(getClass(), "gen-selected.png"));
        products.setSelectedIcon(ResourceUtility.getIcon(getClass(), "pro-selected.png"));
        metabolites.setSelectedIcon(ResourceUtility.getIcon(getClass(), "met-selected.png"));
        reactions.setSelectedIcon(ResourceUtility.getIcon(getClass(), "rxn-selected.png"));

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


        buttonMap.put(Metabolite.class, metabolites);
        buttonMap.put(Reaction.class, reactions);

        buttonMap.put(GeneProduct.class, products);

//        buttonMap.put(RunnableTask.BASE_TYPE, tasks);
        buttonMap.put(Gene.class, genes);

        bottombar.addComponentToCenter(info);

    }


    public JComponent getButtonGroup() {
        return new LabeledComponentGroup(viewLabel, genes, products, metabolites, reactions).getComponent();
    }


    public BottomBar getBottomBar() {
        return bottombar;
    }


    public void setSelected(Class<? extends Entity> c) {
        if (buttonMap.get(c) != null) {
            for (JToggleButton button : buttonMap.values()) {
                button.setSelected(false);
            }
            buttonMap.get(c).setSelected(true);
            viewLabel.setText(((EntityViewInfo) buttonMap.get(c).getAction()).getViewLabel());
            info.setText("");
            info.repaint();
        }
    }


    private class ViewGenes
            extends EntityViewInfo {

        public ViewGenes() {
            super("ViewGenes", "Genes");
        }


        public void actionPerformed(ActionEvent e) {
            controller.setGeneView();
            viewLabel.setText(getViewLabel());
        }
    }


    private class ViewProducts
            extends EntityViewInfo {

        public ViewProducts() {
            super("ViewProducts", "Gene Products");
        }


        public void actionPerformed(ActionEvent e) {
            controller.setProductView();
            viewLabel.setText(getViewLabel());
        }
    }


    private class ViewMetabolites
            extends EntityViewInfo {

        public ViewMetabolites() {
            super("ViewMetabolites", "Metabolites");
        }


        public void actionPerformed(ActionEvent e) {
            controller.setMetaboliteView();
            viewLabel.setText(getViewLabel());
        }
    }


    private class ViewReactions
            extends EntityViewInfo {

        public ViewReactions() {
            super("ViewReactions", "Reactions");
        }


        public void actionPerformed(ActionEvent e) {
            controller.setReactionView();
            viewLabel.setText(getViewLabel());
        }
    }

    private abstract class EntityViewInfo extends GeneralAction {

        private String viewLabel;

        private EntityViewInfo(String command, String viewLabel) {
            super(ViewToggle.class, command);
            this.viewLabel = viewLabel;
        }

        public String getViewLabel() {
            return viewLabel;
        }
    }

}