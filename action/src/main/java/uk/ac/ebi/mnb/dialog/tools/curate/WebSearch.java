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
package uk.ac.ebi.mnb.dialog.tools.curate;

import com.google.common.base.Joiner;
import com.jgoodies.forms.layout.CellConstraints;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.ui.tool.annotation.CrossreferenceModule;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URLEncoder;


/**
 *
 * WebSearch 2012.01.13
 *
 * @version $Rev$ : Last Changed $Date$
 * @author johnmay
 * @author $Author$ (this version)
 *
 * Class description
 *
 */
public class WebSearch
        implements CrossreferenceModule {

    private static final Logger LOGGER = Logger.getLogger(WebSearch.class);

    private final JPanel component;

    private final JTextField field;

    private final JButton search;

    private final JCheckBox restrict = CheckBoxFactory.newCheckBox("Restrict search", "Restricts the search results to a chemical databases; " + Joiner.on(", ").join(SITES));

    private static final String GOOGLE_SEARCH_FORMAT = "http://www.google.com/search?ie=UTF-8&q=%s";

    private static final String[] SITES = new String[]{
        "site:ebi.ac.uk/chebi",
        "site:pubchem.ncbi.nlm.nih.gov",
        "site:metacyc.org",
        "site:ebi.ac.uk/chembl",
        "site:hmdb.ca",
        "site:molecular-networks.com/biopath",
        "site:chemspider.com"
    };


    private final UndoManager undoManager;

    public WebSearch(UndoManager undoManager) {

        this.undoManager = undoManager;

        component = PanelFactory.createDialogPanel("left:p:grow, 4dlu, min", "p, 4dlu, p");
        field = FieldFactory.newField(30);
        search = ButtonFactory.newButton("Search", new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                try {

                    String text = field.getText() + (restrict.isSelected() ? " " + Joiner.on(" OR ").join(SITES) : "");

                    String query = URLEncoder.encode(text, "UTF-8");
                    String address = String.format(GOOGLE_SEARCH_FORMAT, query);

                    Desktop.getDesktop().browse(new URI(address));
                } catch (Exception ex) {
                    LOGGER.error("Unable to open browser: " + ex.getMessage());
                }
            }
        });

        restrict.setSelected(true);

        CellConstraints cc = new CellConstraints();
        component.add(restrict, cc.xy(1, 1));
        component.add(search, cc.xy(3, 1));
        component.add(field, cc.xyw(1, 3, 3));

    }


    public String getDescription() {
        return "Web Search";
    }


    public JComponent getComponent() {
        return component;
    }


    public void setup(Metabolite metabolite) {
        field.setText(metabolite.getName());
    }


    public boolean canTransferAnnotations() {
        return false;
    }


    public void transferAnnotations() {
        // do nothing
    }
}
