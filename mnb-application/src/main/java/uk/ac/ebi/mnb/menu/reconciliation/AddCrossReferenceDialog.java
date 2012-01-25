/**
 * AddCrossReferenceDialog.java
 *
 * 2011.09.26
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
package uk.ac.ebi.mnb.menu.reconciliation;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditListener;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.main.MainView;
import uk.ac.ebi.resource.IdentifierFactory;
import uk.ac.ebi.caf.component.factory.FieldFactory;

/**
 *          AddCrossReferenceDialog – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AddCrossReferenceDialog
        extends ControllerDialog
        {

    private static final Logger LOGGER = Logger.getLogger(AddCrossReferenceDialog.class);
    private static final Map<String, Byte> nameIndexMap = new HashMap();
    private JComboBox type;
    private JTextField accession;
    private CellConstraints cc = new CellConstraints();
    private AnnotatedEntity entity = null;

    public AddCrossReferenceDialog(JFrame frame,
                                   TargetedUpdate updater,
                                   MessageManager messages,
                                   SelectionController controller,
                                   UndoableEditListener undo) {

        super(frame, updater, messages, controller, undo, "AddCrossReference");
        for (Identifier id : IdentifierFactory.getInstance().getSupportedIdentifiers()) {
            nameIndexMap.put(id.getShortDescription(), id.getIndex());
        }
        type = new JComboBox(nameIndexMap.keySet().toArray());
        accession = FieldFactory.newField(20);

        setDefaultLayout();
    }

    public void setComponent(AnnotatedEntity reconComponent) {
        this.entity = reconComponent;
    }

    @Override
    public JPanel getOptions() {

        JPanel panel = super.getOptions();

        panel.setLayout(new FormLayout("p, 4dlu, p", "p"));

        panel.add(type, cc.xy(1, 1));
        panel.add(accession, cc.xy(3, 1));

        return panel;
    }

    @Override
    public void process() {

        // resolve
        Byte index = nameIndexMap.get((String) type.getSelectedItem());
        Identifier id = IdentifierFactory.getInstance().ofIndex(index);
        id.setAccession(accession.getText());

        // add to component
        entity.addAnnotation(new CrossReference(id));


    }

    @Override
    public boolean update() {
        return MainView.getInstance().getViewController().update();
    }
}
