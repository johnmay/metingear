/**
 * NewMetabolite.java
 *
 * 2011.10.04
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
package uk.ac.ebi.mnb.dialog.file;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditListener;
import mnb.io.tabular.NamedEntityResolver;
import mnb.io.tabular.parser.ReactionParser;
import mnb.io.tabular.parser.UnparsableReactionError;
import mnb.io.tabular.preparse.PreparsedReaction;
import mnb.io.tabular.type.ReactionColumn;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

/**
 * @name    NewMetabolite - 2011.10.04 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class NewReaction extends NewEntity {

    private static final Logger LOGGER = Logger.getLogger(NewReaction.class);
    private ReactionField equation;
    private static CellConstraints cc = new CellConstraints();

    public NewReaction(JFrame frame, TargetedUpdate updater, MessageManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits);
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Please specify detail for a new reaction");
        return label;
    }

    @Override
    public JPanel getOptions() {

        JPanel panel = super.getOptions();

        equation = new ReactionField(this);

        FormLayout layout = (FormLayout) panel.getLayout();
        layout.appendRow(new RowSpec(Sizes.DLUY4));
        layout.appendRow(new RowSpec(Sizes.PREFERRED));
        panel.add(equation, cc.xyw(1, layout.getRowCount(), 7));

        return panel;
    }

    @Override
    public void process() {

        ReconstructionManager manager = ReconstructionManager.getInstance();
        if (manager.hasProjects()) {
            Reconstruction reconstruction = manager.getActive();

            ReactionParser parser = new ReactionParser(new NamedEntityResolver());
            PreparsedReaction ppRxn = new PreparsedReaction();

            ppRxn.addValue(ReactionColumn.ABBREVIATION, getAbbreviation());
            ppRxn.addValue(ReactionColumn.DESCRIPTION, getName());
            ppRxn.addValue(ReactionColumn.EQUATION, equation.getText());

            try {
                reconstruction.addReaction(parser.parseReaction(ppRxn));
            } catch (UnparsableReactionError ex) {
                addMessage(new ErrorMessage("Cannot create reaction: " + ex.getMessage()));
            }

        }
    }
}
