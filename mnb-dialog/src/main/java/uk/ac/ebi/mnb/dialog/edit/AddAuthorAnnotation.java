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
import javax.swing.SwingConstants;
import uk.ac.ebi.annotation.AuthorAnnotation;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.mnb.interfaces.ViewController;
import uk.ac.ebi.mnb.view.ContextDialog;
import uk.ac.ebi.mnb.view.labels.DialogLabel;

/**
 * @name    AddAuthorAnnotation - 2011.10.04 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AddAuthorAnnotation extends ContextDialog {

    private JTextField author;
    private JTextField description;
    private CellConstraints cc = new CellConstraints();

    public AddAuthorAnnotation(JFrame frame, ViewController controller) {
        super(frame, controller, "SaveDialog");
        description = new JTextField(30);
        author = new JTextField(System.getProperties().getProperty("user.name"));
        setDefaultLayout();
    }

    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Add an author annotation to one or more entities");
        return label;
    }

    @Override
    public JPanel getOptions() {
        JPanel panel = super.getOptions();

        panel.setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p"));

        panel.add(new DialogLabel("Author:", SwingConstants.RIGHT), cc.xy(1, 1));
        panel.add(author, cc.xy(3, 1));
        panel.add(new DialogLabel("Description:", SwingConstants.RIGHT), cc.xy(1, 3));
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
        return getController().update();
    }
}
