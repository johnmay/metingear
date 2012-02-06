/**
 * CrossReferenceEditor.java
 *
 * 2011.10.07
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
package uk.ac.ebi.mnb.dialog.popup;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.caf.component.ExpandingComponentList;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.mnb.view.MComboBox;
import uk.ac.ebi.resource.IdentifierFactory;
import uk.ac.ebi.chemet.render.components.IdentifierEditor;


/**
 * @name    CrossReferenceEditor - 2011.10.07 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class CrossReferenceEditorDialog extends PopupDialog {

    private static final Logger LOGGER = Logger.getLogger(CrossReferenceEditorDialog.class);

    private CellConstraints cc = new CellConstraints();

    private Map<String, Byte> map = new HashMap();

    private FormLayout layout;

    private List<MComboBox> comboboxes = new LinkedList();

    private List<JTextField> fields = new LinkedList();

    private IdentifierFactory ID_FACTORY = IdentifierFactory.getInstance();

    private JPanel panel;

    private ExpandingComponentList<IdentifierEditor> expand;


    public CrossReferenceEditorDialog(JFrame frame) {
        super(frame);

        expand = new ExpandingComponentList<IdentifierEditor>(this) {

            @Override
            public IdentifierEditor newComponent() {
                return new IdentifierEditor();
            }
        };
        expand.append(); // need first component
        getPanel().add(expand.getComponent());
        //     panel = getPanel();
    }


    public void setup(Collection<? extends CrossReference> references) {

        for (int i = 0; i < references.size(); i++) {
            // do with collection
            // expand.getComponent(i).setIdentifier(references.get(i).getIdentifier());
        }

//        layout = new FormLayout("min, p, min, min ", "");
//        panel.setLayout(layout);
//        comboboxes = new LinkedList();
//        fields = new LinkedList();
//
//
//        if (references.isEmpty()) {
//            layout.appendRow(new RowSpec(Sizes.PREFERRED));
//            MComboBox box = new MComboBox(map.keySet());
//            JTextField field = FieldFactory.newField(12);
//            fields.add(field);
//            comboboxes.add(box);
//        } else {
//            for (CrossReference reference : references) {
//                layout.appendRow(new RowSpec(Sizes.PREFERRED));
//                MComboBox box = new MComboBox(map.keySet());
//                box.setSelectedItem(reference.getIdentifier().getShortDescription());
//                JTextField field = FieldFactory.newField(reference.getIdentifier().getAccession());
//                field.setColumns(12);
//                fields.add(field);
//                comboboxes.add(box);
//            }
//        }
//
//        update();
    }


    private void update() {
    }


    public Collection<CrossReference> getCrossReferences() {

        Collection<CrossReference> xref = new ArrayList<CrossReference>();

        for (int i = 0; i < expand.getSize(); i++) {

            IdentifierEditor editor = expand.getComponent(i);

            if (editor.isFilled()) {
                Identifier id = editor.getIdentifier();
                xref.add(new CrossReference(id));
            }

        }

        return xref;

    }
}
