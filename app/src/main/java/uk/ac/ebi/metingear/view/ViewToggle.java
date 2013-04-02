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
package uk.ac.ebi.metingear.view;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.LabeledComponentGroup;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.caf.utility.font.EBIIcon;
import uk.ac.ebi.mdk.domain.entity.*;
import uk.ac.ebi.mnb.view.entity.ProjectView;

import javax.swing.*;
import java.awt.Color;
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

        metabolites.setIcon(EBIIcon.TOP_LEVEL_CHEMICAL.create().size(18f).icon());
        products.setIcon(EBIIcon.TOP_LEVEL_STRUCTURES.create().size(18f).icon());
        genes.setIcon(EBIIcon.TOP_LEVEL_GENE.create().size(18f).icon());
        reactions.setIcon(EBIIcon.DIRECTION_REVERSIBLE.create().size(18f).icon());
        metabolites.setSelectedIcon(EBIIcon.TOP_LEVEL_CHEMICAL.create()
                                           .size(18f).color(Color.WHITE).icon());
        products.setSelectedIcon(EBIIcon.TOP_LEVEL_STRUCTURES.create().size(18f).color(Color.WHITE).icon());
        genes.setSelectedIcon(EBIIcon.TOP_LEVEL_GENE.create().size(18f).color(Color.WHITE).icon());
        reactions.setSelectedIcon(EBIIcon.DIRECTION_REVERSIBLE.create().size(18f).color(Color.WHITE).icon());

        genes.putClientProperty("JButton.buttonType", "segmentedTextured");
        genes.putClientProperty("JButton.segmentPosition", "first");
        genes.setFocusable(false);
        genes.setToolTipText("Genes");

        products.putClientProperty("JButton.buttonType", "segmentedTextured");
        products.putClientProperty("JButton.segmentPosition", "middle");
        products.setFocusable(false);
        products.setToolTipText("Gene Products");

        metabolites.putClientProperty("JButton.buttonType", "segmentedTextured");
        metabolites.putClientProperty("JButton.segmentPosition", "middle");
        metabolites.setFocusable(false);
        metabolites.setToolTipText("Metabolites");

        reactions.putClientProperty("JButton.buttonType", "segmentedTextured");
        reactions.putClientProperty("JButton.segmentPosition", "last");
        reactions.setFocusable(false);
        reactions.setToolTipText("Reactions");

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
        return new LabeledComponentGroup(viewLabel.getText(), genes, products, metabolites, reactions).getComponent();
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
