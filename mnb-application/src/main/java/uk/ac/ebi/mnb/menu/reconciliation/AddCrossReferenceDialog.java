
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
import com.sun.awt.AWTUtilities;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import uk.ac.ebi.mnb.view.DropdownDialog;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.interfaces.Identifier;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.view.DialogPanel;
import uk.ac.ebi.resource.IdentifierFactory;


/**
 *          AddCrossReferenceDialog – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AddCrossReferenceDialog extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(AddCrossReferenceDialog.class);
    private static final Map<String, Byte> nameIndexMap = new HashMap();
    private JComboBox type;
    private JTextField accession;
    private AnnotatedEntity reconComponent = null;


    public AddCrossReferenceDialog() {

        super(MainFrame.getInstance(), MainFrame.getInstance(), "AddCrossReference");
        for( Identifier id : IdentifierFactory.getInstance().getSupportedIdentifiers() ) {
            nameIndexMap.put(id.getShortDescription(), id.getIndex());
        }
        type = new JComboBox(nameIndexMap.keySet().toArray());
        accession = new JTextField(20);


        layoutComponents();
        pack();

    }


    public void setComponent(AnnotatedEntity reconComponent) {
        this.reconComponent = reconComponent;
    }


    private void layoutComponents() {

        setLayout(new FormLayout("10dlu, pref, 10dlu", "10dlu, pref, 4dlu, pref, 10dlu"));

        CellConstraints cc = new CellConstraints();

        // options
        JComponent selection = new DialogPanel();
        selection.setLayout(new FormLayout("p, 4dlu, p", "p"));
        selection.add(type, cc.xy(1, 1));
        selection.add(accession, cc.xy(3, 1));


        // close and run buttons
        JComponent component = new DialogPanel();
        component.setLayout(new FormLayout("left:p, pref:grow, right:p", "p"));
        component.add(getClose(), cc.xy(1, 1));
        component.add(getActivate(), cc.xy(3, 1));

        add(selection, cc.xy(2, 2));
        add(component, cc.xy(2, 4));

    }


    @Override
    public void process() {

        // resolve
        Byte index = nameIndexMap.get((String) type.getSelectedItem());
        Identifier id = IdentifierFactory.getInstance().ofIndex(index);
        id.setAccession(accession.getText());

        // add to component
        reconComponent.addAnnotation(new CrossReference(id));


    }


    @Override
    public boolean update() {
        return MainFrame.getInstance().getViewController().update();
    }


}

