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

package uk.ac.ebi.mnb.menu.build;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Sizes;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import mnb.view.old.TaskManager;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.metabolomes.core.reaction.ReactionBalancer;
import uk.ac.ebi.mnb.main.MainView;


/**
 * MassChargeBalanceDialog.java
 *
 *
 * @author johnmay
 * @date May 24, 2011
 */
public class MassChargeBalanceDialog
  extends DropdownDialog {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      MassChargeBalanceDialog.class);
    JCheckBox protonCheckBox;
    JCheckBox waterCheckBox;
    JCheckBox oxygenCheckBox;
    JCheckBox carbondioxideCheckBox;


    public MassChargeBalanceDialog() {
        super(MainView.getInstance(), MainView.getInstance(), "MassChargeBalance");
        protonCheckBox = new JCheckBox("<html>H<sup>+</sup></html>");
        waterCheckBox = new JCheckBox("<html>H<sub>2</sub>O</html>");
        oxygenCheckBox = new JCheckBox("<html>O<sub>2</sub></html>");
        layoutForm();
    }


    private void layoutForm() {
        FormLayout layout = new FormLayout("40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
                                           "p, 2dlu, p, 4dlu, p, 2dlu, p, 2dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setBorder(Borders.createEmptyBorder(Sizes.DLUX4, Sizes.DLUX4, Sizes.DLUX4,
                                                    Sizes.DLUX4));
        CellConstraints cc = new CellConstraints();
        builder.add(new JLabel("Balancing Molecules"), cc.xyw(1, 1, 5));
        builder.add(new JSeparator(), cc.xyw(1, 3, 5));
        builder.add(protonCheckBox, cc.xy(1, 5));
        builder.add(waterCheckBox, cc.xy(3, 5));
        builder.add(oxygenCheckBox, cc.xy(5, 5));
        builder.add(new JSeparator(), cc.xyw(1, 7, 5));
        builder.add(getClose(), cc.xy(3, 9));
        builder.add(getActivate(), cc.xy(5, 9));
        add(builder.getPanel());
    }


    @Override
    public void process() {
    }


    @Override
    public boolean update() {
        return true;
    }


}

