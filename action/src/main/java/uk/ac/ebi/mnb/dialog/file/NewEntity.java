/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
package uk.ac.ebi.mnb.dialog.file;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import uk.ac.ebi.mnb.interfaces.Updatable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 * @name NewEntity - 2011.10.04 <br> Base class for new entities. Provides
 * operations on basic information setting (i.e. Abbreviation, Name and
 * Accession).
 */
public abstract class NewEntity extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(NewEntity.class);


    private JTextField name = FieldFactory.newField(20);

    private JTextField abbreviation = FieldFactory.newField(4);

    private CellConstraints cc = new CellConstraints();

    private Updatable updateable;

    private static final Map<String, Byte> nameIndexMap = new HashMap();

    private static Pattern clean = Pattern.compile("[^A-z0-9]+");
    private static Pattern split = Pattern.compile("\\s+|\\-+");


    /**
     * Provide the frame to attach the dialog to and the default identifier type
     * BasicChemicalIdentifier, BasicProteinIdentifier BasicReactionIdentifier
     *
     * @param frame
     */
    public NewEntity(JFrame frame,
                     TargetedUpdate updater,
                     ReportManager messages,
                     SelectionController controller,
                     UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "SaveDialog");


        this.updateable = updater;

    }


    @Override
    public JPanel getForm() {

        JPanel panel = super.getForm();

        panel.setLayout(new FormLayout("right:p, 4dlu, p",
                                       "p, 4dlu, p:grow"));


        panel.add(LabelFactory.newFormLabel("Name", "An official or trivial name the new entity"), cc.xy(1, 1));
        panel.add(name, cc.xy(3, 1));

        panel.add(LabelFactory.newFormLabel("Abbreviation",
                                            "A short 2-5 character abbreviation of the new entity"),
                  cc.xy(1, 3));
        panel.add(abbreviation, cc.xy(3, 3));

        name.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String nm = name.getText();
                String abbr = abbreviation.getText();
                abbreviation.setText(createAbbreviation(nm, 4));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // suppressed
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // suppressed
            }
        });

        return panel;

    }


    public static String createAbbreviation(String text, int target) {

        if (text.length() < target) {
            return text.toLowerCase(Locale.ENGLISH);
        }

        StringBuilder builder = new StringBuilder(6);

        String[] chunks = split.split(text.trim());

        int extra = (target - chunks.length) / chunks.length;

        for (String chunk : chunks) {
            chunk = clean.matcher(chunk).replaceAll("");
            if (!chunk.isEmpty())
                builder.append(chunk.charAt(0));
            for (int i = 0; i < extra && chunk.length() > i + 1; i++)
                builder.append(chunk.charAt(i + 1));
        }

        return builder.toString().toLowerCase(Locale.ENGLISH);

    }

    /**
     * Returns the value of the name field
     *
     * @return
     */
    public String getName() {
        return name.getText().trim();
    }


    /**
     * Returns the value of the name field
     *
     * @return
     */
    public String getAbbreviation() {
        return abbreviation.getText().trim();
    }


    /**
     * Return the appropiate identifier
     *
     * @return
     */
    public abstract Identifier getIdentifier();


    @Override
    public boolean update() {
        return updateable.update();
    }
}
