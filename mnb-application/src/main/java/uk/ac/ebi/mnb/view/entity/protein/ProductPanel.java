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
import uk.ac.ebi.mnb.core.MLabels;

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
    private JScrollPane sequencePane;
    private JTextPane sequence;
    private DefaultListModel sequenceListModel;
    //
    private CellConstraints cc = new CellConstraints();

    public ProductPanel() {
        super("Gene Product", new AnnotationRenderer());
        sequenceListModel = new DefaultListModel();

        sequence = new JTextPane();
        sequence.setFont(ViewUtils.COURIER_NEW_PLAIN_11);


//        sequence.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                System.out.println(sequence.viewToModel(e.getPoint()));
//            }
//        });

    }

    /**
     *
     * Updates the displayed sequence
     * Sends update signal to AbstractEntityPanel to update Name, Abbreviation and Identifier
     *
     * @return
     * 
     */
    @Override
    public boolean update() {

        // set the sequence
        sequence.setText(entity.getSequence().getSequenceAsString());

        return super.update();
        
    }

    @Override
    public boolean setEntity(AnnotatedEntity entity) {
        this.entity = (GeneProduct) entity;
        return super.setEntity(entity);
    }

    /**
     * 
     * Appends a JTextPane displaying the product sequence to the basic information panel
     *
     * @return
     * 
     */
    @Override
    public JPanel getBasicPanel() {

        JPanel panel = super.getBasicPanel();

        FormLayout layout = (FormLayout) panel.getLayout();
        layout.appendRow(new RowSpec(Sizes.PREFERRED));
        sequencePane = new BorderlessScrollPane(sequence);
        sequencePane.setPreferredSize(new Dimension(500, 110));
        panel.add(sequencePane, cc.xyw(1, layout.getRowCount(), 5));
        layout.appendRow(new RowSpec(Sizes.DLUY4));

        return panel;

    }

    /**
     *
     * Returns the synopsis information panel for the gene product
     *
     */
    public JPanel getSynopsis() {

        JPanel panel = new GeneralPanel();
        panel.add(MLabels.newLabel("No synopsis implemented"));
        return panel;

    }

 }
