/**
 * MetabolitePanel.java
 *
 * 2011.09.30
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
package uk.ac.ebi.mnb.view.entity.protein;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;

import uk.ac.ebi.interfaces.*;
import uk.ac.ebi.mnb.view.*;
import uk.ac.ebi.mnb.view.entity.AbstractEntityPanel;

import javax.swing.*;
import javax.swing.text.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.*;

/**
 *          MetabolitePanel – 2011.09.30 <br>
 *          Product panel renderer.
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ProductPanel
        extends AbstractEntityPanel {

    private static final Logger LOGGER = Logger.getLogger(ProductPanel.class);
    private GeneProduct entity;
    private JLabel formula;
    private JTextField generic;
    //
    private JTextPane sequence;
    private DefaultListModel sequenceListModel;
    //
    private CellConstraints cc = new CellConstraints();

    public ProductPanel() {
        super("Gene Product", new AnnotationRenderer());
        sequenceListModel = new DefaultListModel();
        sequence = new JTextPane();
        sequence.setFont(ViewUtils.COURIER_NEW_PLAIN_11);
        sequence.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(sequence.viewToModel(e.getPoint()));
            }
        });

    }

    @Override
    public boolean update() {

        sequence.setText(entity.getSequence().getSequenceAsString());
        Style style = sequence.addStyle("Red", null);
        StyleConstants.setForeground(style, Color.red);
        //   sequence.getStyledDocument().setCharacterAttributes(10, 10, sequence.getStyle("Red"), true);

        return super.update();

    }

    @Override
    public boolean setEntity(AnnotatedEntity entity) {
        this.entity = (GeneProduct) entity;
        return super.setEntity(entity);
    }
    private JScrollPane pane;

    @Override
    public JPanel getBasicPanel() {

        JPanel panel = super.getBasicPanel();

        FormLayout layout = (FormLayout) panel.getLayout();
        layout.appendRow(new RowSpec(Sizes.PREFERRED));
        pane = new BorderlessScrollPane(sequence);
        pane.setPreferredSize(new Dimension(500, 110));
        panel.add(pane, cc.xyw(1, layout.getRowCount(), 5));
        layout.appendRow(new RowSpec(Sizes.DLUY4));

        return panel;

    }

    /**
     * Returns the specific information panel
     */
    public JPanel getSynopsis() {

        JPanel panel = new GeneralPanel();
        panel.setBackground(Color.YELLOW);
        return panel;

    }

    /**
     * Returns the internal reference panel information panel
     */
    public JPanel getInternalReferencePanel() {

        JPanel panel = new JPanel();

        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Internal references"));

        return panel;

    }
}
