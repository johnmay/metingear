/*
 *     This file is part of Metabolic Network Builder
 *
 *     Metabolic Network Builder is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.mnb.view.entity;

import com.explodingpixels.macwidgets.HudWidgetFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import uk.ac.ebi.chemet.render.ViewUtilities;
import uk.ac.ebi.mnb.view.entity.AbstractEntityInspector;
import uk.ac.ebi.mnb.main.MainView;


/**
 * InspectorToolbar.java – MetabolicDevelopmentKit – Jun 6,
 * Transient panel with a CardLayout to switch between the Viewing and Editing states of the
 * inspector entry.
 * @author johnmay <johnmay@ebi.ac.uk, john.wilkinsonmay@gmail.com>
 */
public class InspectorToolbar
  extends JPanel {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      InspectorToolbar.class);
    private CardLayout layout = new CardLayout();
    // edit panel when editing an entry, view panel when view an entry
    private TriAreaComponent editPanel = new TriAreaComponent();
    private TriAreaComponent viewPanel = new TriAreaComponent();
    private String EDIT_PANEL_NAME = "Edit";
    private String VIEW_PANEL_NAME = "View";
    private AbstractEntityInspector inspector;


    public InspectorToolbar(AbstractEntityInspector inspectorPanel) {
        inspector = inspectorPanel;
        setLayout(layout);
        setUpViewPanel();
        setUpEditPanel();
        add(editPanel.getComponent(), EDIT_PANEL_NAME);
        add(viewPanel.getComponent(), VIEW_PANEL_NAME);
        setBackground(ViewUtilities.DARK_BACKGROUND);
        setBorder(Borders.DLU2_BORDER);
    }


    private void setUpEditPanel() {
//      editPanel.setOpaque( false );

        JButton discardButton = HudWidgetFactory.createHudButton("Discard Changes");
        JButton saveButton = HudWidgetFactory.createHudButton("Save");
        //editPanel.setLayout( new FormLayout( "right:p, p:grow , center:p, p:grow, left:p" , "pref" ) );
        CellConstraints cc = new CellConstraints();
        editPanel.addComponentToCenter(HudWidgetFactory.createHudButton("Add Annotation"), 4);
        editPanel.addComponentToCenter(HudWidgetFactory.createHudButton("Add Reaction"), 4);
        editPanel.addComponentToRight(discardButton, 4);
        editPanel.addComponentToRight(saveButton, 4);
        discardButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setViewMode();
            }


        });
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                inspector.store();
                MainView.getInstance().getViewController().update(inspector.getSelection());
                setViewMode();
            }


        });
    }


    /**
     * Sets the settings for the view panel. This panel is effectively just a single
     * button to switch to edit mode. In future we could add export/import options here
     * or under the edit Panel
     */
    private void setUpViewPanel() {
        JButton editButton = HudWidgetFactory.createHudButton("Edit");
        // viewPanel.setOpaque( false );
        // viewPanel.setLayout( new FormLayout( "right:p, p:grow , center:p, p:grow, left:p" , "pref" ) );
        CellConstraints cc = new CellConstraints();

        editButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setEditMode();
                // also set edit mode on the current inspector;
            }


        });
        viewPanel.addComponentToRight(editButton);
        viewPanel.addComponentToLeft(HudWidgetFactory.createHudButton("Export TSV"), 4);
        viewPanel.addComponentToLeft(HudWidgetFactory.createHudButton("Export SBML"), 4);

    }


    /**
     * Displays the editing panel
     */
    public void setEditMode() {
        layout.show(this, EDIT_PANEL_NAME);
        inspector.setEditable(true);
    }


    /**
     * Displays the viewing panel
     */
    public void setViewMode() {
        layout.show(this, VIEW_PANEL_NAME);
        inspector.setEditable(false);
    }


}

