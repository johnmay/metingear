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
package uk.ac.ebi.mnb.view.entity.gene;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import org.apache.log4j.Logger;
import org.biojava3.core.sequence.template.Sequence;
import uk.ac.ebi.caf.component.BorderlessScrollPane;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mnb.view.entity.AbstractEntityPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;


/**
 * MetabolitePanel – 2011.09.30 <br> Product panel renderer.
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class GenePanel
        extends AbstractEntityPanel {

    private static final Logger LOGGER = Logger.getLogger(GenePanel.class);

    private Gene entity;

    private JLabel formula;

    private JTextField generic;
    //

    private JScrollPane sequencePane;

    private JTextPane sequence;

    private DefaultListModel sequenceListModel;
    //

    private CellConstraints cc = new CellConstraints();


    public GenePanel() {
        super("Gene");
        sequenceListModel = new DefaultListModel();

        sequence = new JTextPane();
        sequence.setFont(new Font("Courier New", Font.PLAIN, 10));


        //        sequence.addMouseListener(new MouseAdapter() {
        //            @Override
        //            public void mouseClicked(MouseEvent e) {
        //                System.out.println(sequence.viewToModel(e.getPoint()));
        //            }
        //        });

    }


    /**
     * Updates the displayed sequence Sends update signal to AbstractEntityPanel
     * to update Name, Abbreviation and Identifier
     *
     * @return
     */
    @Override
    public boolean update() {

        if (super.update()) {

            // set the sequence
            Sequence seq = entity.getSequence();

            sequence.setText(seq != null ? seq.getSequenceAsString() : "no sequence set");

            return true;

        }

        return false;

    }


    @Override
    public Collection<? extends AnnotatedEntity> getReferences() {
        return entity.getProducts();
    }

    @Override
    public boolean setEntity(AnnotatedEntity entity) {
        this.entity = (Gene) entity;
        return super.setEntity(entity);
    }


    /**
     * Appends a JTextPane displaying the product sequence to the basic
     * information panel
     *
     * @return
     */
    @Override
    public JPanel getBasicPanel() {

        JPanel panel = super.getBasicPanel();

        FormLayout layout = (FormLayout) panel.getLayout();
        layout.appendRow(new RowSpec(Sizes.PREFERRED));
        sequencePane = new BorderlessScrollPane(sequence);
        sequencePane.setPreferredSize(new Dimension(500, 80));
        panel.add(sequencePane, cc.xyw(1, layout.getRowCount(), 5));

        return panel;

    }


    /**
     * Returns the synopsis information panel for the gene product
     */
    public JPanel getSynopsis() {

        JPanel panel = PanelFactory.createInfoPanel();
        panel.add(LabelFactory.newLabel("No synopsis implemented"));
        return panel;

    }
}
