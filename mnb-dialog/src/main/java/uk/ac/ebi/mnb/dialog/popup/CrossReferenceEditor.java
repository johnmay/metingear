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
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.interfaces.Theme;
import uk.ac.ebi.mnb.settings.Settings;
import uk.ac.ebi.mnb.view.MComboBox;
import uk.ac.ebi.mnb.view.labels.IconButton;
import uk.ac.ebi.resource.IdentifierFactory;
import uk.ac.ebi.ui.component.factory.FieldFactory;

/**
 * @name    CrossReferenceEditor - 2011.10.07 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class CrossReferenceEditor extends PopupDialog {

    private static final Logger LOGGER = Logger.getLogger(CrossReferenceEditor.class);
    private CellConstraints cc = new CellConstraints();
    private Map<String, Byte> map = new HashMap();
    private FormLayout layout;
    private List<MComboBox> comboboxes = new LinkedList();
    private List<JTextField> fields = new LinkedList();
    private IdentifierFactory ID_FACTORY = IdentifierFactory.getInstance();
    private JPanel panel;

    public CrossReferenceEditor(JFrame frame) {
        super(frame);
        for (Identifier id : IdentifierFactory.getInstance().getSupportedIdentifiers()) {
            map.put(id.getShortDescription(), id.getIndex());
        }
        panel = getPanel();
    }

    public void setup(Collection<? extends CrossReference> references) {



        layout = new FormLayout("min, p, min, min ", "");
        panel.setLayout(layout);
        comboboxes = new LinkedList();
        fields = new LinkedList();


        if (references.isEmpty()) {
            layout.appendRow(new RowSpec(Sizes.PREFERRED));
            MComboBox box = new MComboBox(map.keySet());
            JTextField field = FieldFactory.newField(12);
            fields.add(field);
            comboboxes.add(box);
        } else {
            for (CrossReference reference : references) {
                layout.appendRow(new RowSpec(Sizes.PREFERRED));
                MComboBox box = new MComboBox(map.keySet());
                box.setSelectedItem(reference.getIdentifier().getShortDescription());
                JTextField field = FieldFactory.newField(reference.getIdentifier().getAccession());
                field.setColumns(12);
                fields.add(field);
                comboboxes.add(box);
            }
        }

        update();

    }

    private void update() {
        panel.removeAll();
        Theme theme = Settings.getInstance().getTheme();
        for (int i = 0; i < fields.size(); i++) {
            JButton minus = new IconButton(theme.getMinusIcon(), new RemoveCrossReference(this, i));
            JButton plus = new IconButton(theme.getPlusIcon(), new AddCrossReference(this, i));
            minus.setEnabled(!(fields.size() == 1));
            panel.add(comboboxes.get(i), cc.xy(1, i + 1));
            panel.add(fields.get(i), cc.xy(2, i + 1));
            panel.add(minus, cc.xy(3, i + 1));
            panel.add(plus, cc.xy(4, i + 1));
        }

    }

    public Collection<CrossReference> getCrossReferences() {
        Collection<CrossReference> xref = new ArrayList<CrossReference>();

        for (int i = 0; i < fields.size(); i++) {
            MComboBox box = comboboxes.get(i);
            JTextField field = fields.get(i);
            String accession = field.getText().trim();
            if (box.getSelectedIndex() != 0 && accession.isEmpty() == false) {
                String desc = (String) box.getSelectedItem();
                Identifier id = IdentifierFactory.getInstance().ofIndex(map.get(desc));
                id.setAccession(accession);
                xref.add(new CrossReference(id));
            }
        }
        return xref;
    }

    private class RemoveCrossReference extends AbstractAction {

        private int index;
        private JDialog dialog;

        public RemoveCrossReference() {
        }

        public RemoveCrossReference(JDialog dialog, int index) {
            this.index = index;
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent e) {
            panel.removeAll();
            layout.removeRow(layout.getRowCount()); // going to repaint all regardless
            comboboxes.remove(index);
            fields.remove(index);
            update();
            dialog.pack();
        }
    }

    private class AddCrossReference extends AbstractAction {

        private int index;
        private JDialog dialog;

        public AddCrossReference() {
        }

        public AddCrossReference(JDialog dialog, int index) {
            this.index = index;
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent e) {
            layout.appendRow(new RowSpec(Sizes.PREFERRED));
            final MComboBox box = new MComboBox(map.keySet());
            final JTextField field = FieldFactory.newField(12);
            comboboxes.add(index + 1, box); // append after
            fields.add(index + 1, field);

            field.getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    String accession = field.getText().trim();
                    DefaultComboBoxModel model = (DefaultComboBoxModel) box.getModel();
                    model.removeAllElements();
                    for (Identifier id : ID_FACTORY.getMatchingIdentifiers(accession)) {
                        model.addElement(id.getShortDescription());
                    }
                    box.repaint();
                }

                public void removeUpdate(DocumentEvent e) {
                    DefaultComboBoxModel model = (DefaultComboBoxModel) box.getModel();
                    model.removeAllElements();

                    String accession = field.getText().trim();
                    
                    for (Identifier id : accession.isEmpty()
                                         ? ID_FACTORY.getSupportedIdentifiers()
                                         : ID_FACTORY.getMatchingIdentifiers(accession)) {
                        model.addElement(id.getShortDescription());
                    }
                    box.repaint();

                }

                public void changedUpdate(DocumentEvent e) {
                    String accession = field.getText().trim();
                    DefaultComboBoxModel model = (DefaultComboBoxModel) box.getModel();
                    model.removeAllElements();
                    for (Identifier id : ID_FACTORY.getMatchingIdentifiers(accession)) {
                        model.addElement(id.getShortDescription());
                    }
                    box.repaint();
                }
            });

            update();
            dialog.pack();
        }
    }
}
