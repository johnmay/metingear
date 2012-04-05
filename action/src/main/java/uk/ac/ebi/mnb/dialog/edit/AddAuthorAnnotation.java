package uk.ac.ebi.mnb.dialog.edit;

/**
 * AddAuthorAnnotation.java
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
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditListener;
import uk.ac.ebi.annotation.AuthorAnnotation;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.FieldFactory;

/**
 * @name    AddAuthorAnnotation - 2011.10.04 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AddAuthorAnnotation extends ControllerDialog {

    private JTextField author;
    private JTextField description;
    private CellConstraints cc = new CellConstraints();

    public AddAuthorAnnotation(JFrame frame, TargetedUpdate updater, ReportManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "SaveDialog");
        description = FieldFactory.newField(30);
        author = FieldFactory.newField(System.getProperties().getProperty("user.name"));
        setDefaultLayout();
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Add an author annotation to one or more entities");
        return label;
    }

    @Override
    public JPanel getForm() {
        JPanel panel = super.getForm();

        panel.setLayout(new FormLayout("p, 4dlu, p",
                                       "p, 4dlu, p"));

        panel.add(LabelFactory.newFormLabel("Author"), cc.xy(1, 1));
        panel.add(author, cc.xy(3, 1));
        panel.add(LabelFactory.newFormLabel("Description"), cc.xy(1, 3));
        panel.add(description, cc.xy(3, 3));

        return panel;
    }

    @Override
    public void process() {
        for (AnnotatedEntity entity : getSelection().getEntities()) {
            entity.addAnnotation(new AuthorAnnotation(author.getText().trim(), description.getText().trim()));
        }
    }

    @Override
    public boolean update() {
        return update(getSelection());
    }
}
