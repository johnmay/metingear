/**
 * NewEntity.java
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
package uk.ac.ebi.mnb.dialog.edit;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import uk.ac.ebi.interfaces.Identifier;
import uk.ac.ebi.mnb.interfaces.Updatable;
import uk.ac.ebi.mnb.view.MComboBox;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.mnb.view.labels.DialogLabel;
import uk.ac.ebi.resource.IdentifierFactory;

/**
 * @name    NewEntity - 2011.10.04 <br>
 *          Base class for new entities. Provides operations on basic information setting (i.e. Abbreviation, Name
 *          and Accession).
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class NewEntity extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(NewEntity.class);
    private Identifier identifier;
    private JComboBox type;
    private JTextField accession = new JTextField(10);
    private JTextField name = new JTextField(20);
    private JTextField abbreviation = new JTextField(5);
    private CellConstraints cc = new CellConstraints();
    private Updatable updateable;
    private static final Map<String, Byte> nameIndexMap = new HashMap();

    /**
     * Provide the frame to attach the dialog to and the default identifier type
     * BasicChemicalIdentifier, BasicProteinIdentifier BasicReactionIdentifier
     * @param frame
     * @param identifier
     */
    public NewEntity(JFrame frame, Updatable updateable, Identifier identifier) {
        super(frame, "SaveDialog");

        for (Identifier id : IdentifierFactory.getInstance().getSupportedIdentifiers()) {
            nameIndexMap.put(id.getShortDescription(), id.getIndex());
        }
        type = new JComboBox(nameIndexMap.keySet().toArray());

        this.identifier = identifier;
        this.updateable = updateable;

                setDefaultLayout();


    }

    @Override
    public JPanel getOptions() {

        JPanel panel = super.getOptions();

        panel.setLayout(new FormLayout("p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p",
                "p"));

        panel.add(type, cc.xy(1, 1));
        panel.add(accession, cc.xy(3, 1));

        panel.add(new DialogLabel("Name:", SwingUtilities.RIGHT), cc.xy(5, 1));
        panel.add(name, cc.xy(7, 1));

        panel.add(new DialogLabel("Abbreviation:", SwingUtilities.RIGHT), cc.xy(9, 1));
        panel.add(abbreviation, cc.xy(11, 1));

        return panel;

    }

    /**
     * Returns the value of the name field
     * @return
     */
    public String getName() {
        return name.getText().trim();
    }

    /**
     * Returns the value of the name field
     * @return
     */
    public String getAbbreviation() {
        return abbreviation.getText().trim();
    }

    /**
     * Returns the value of the name field
     * @return
     */
    public Identifier getIdentifier() {
        Identifier id = IdentifierFactory.getInstance().ofIndex(nameIndexMap.get(type.getSelectedItem()));
        id.setAccession(accession.getText().trim());
        return id;
    }

    @Override
    public boolean update() {
        return updateable.update();
    }
}
