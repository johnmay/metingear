/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mnb.view.old;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import uk.ac.ebi.metabolomes.core.gene.OldGeneProduct;
import uk.ac.ebi.metabolomes.core.gene.GeneProteinProduct;
import uk.ac.ebi.mnb.view.TransparentTextArea;
import uk.ac.ebi.chemet.render.ViewUtilities;
import org.apache.commons.lang.StringUtils;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.chemet.render.factory.FieldFactory;
import uk.ac.ebi.chemet.render.factory.LabelFactory;


/**
 * SynopsisPanel.java
 * Handles the basic details stored in the gene product
 *
 * @author johnmay
 * @date Apr 29, 2011
 */
public class SynopsisPanel
  extends JPanel {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      SynopsisPanel.class);
    private OldGeneProduct displayedProduct = null;
    private JTextField identifierField = null;
    private TransparentTextArea descriptionField = null;
    private JTextField sequenceLengthField = null;
    private TransparentTextArea sequenceField = null;
    private JScrollPane sequencePane = null;
    private JLabel enzymeLabel = null;
    private TransparentTextArea enzymeField = null;
    private JLabel enzymeNameLabel = null;
    private TransparentTextArea enzymeNameField = null;
    private FormLayout layout;
    private CellConstraints cc = new CellConstraints();
    private int width = 500;


    /**
     * Constructor for the inspector detail panel
     */
    public SynopsisPanel() {
        // this.width = width;
        layoutComponents();
    }


    public final void layoutComponents() {

        setBorder(Borders.DLU4_BORDER);
        // jgoodies for layout
        layout = new FormLayout(
          "right:p, 4dlu, p",
          "p,4dlu,p,4dlu,top:p,4dlu,p,4dlu,top:p,4dlu,top:p,4dlu,top:p:grow, 4dlu, p");
        setLayout(layout);
        identifierField = FieldFactory.newTransparentField(15);
        sequenceLengthField = FieldFactory.newTransparentField( 15);
        descriptionField = new TransparentTextArea(2, 60);
        descriptionField.setWrapStyleWord(true);
        enzymeLabel = LabelFactory.newLabel("EC:");
        enzymeField = new TransparentTextArea(1, 20);
        enzymeField.setVisible(false);
        enzymeLabel.setVisible(false);
        enzymeNameLabel = LabelFactory.newLabel("Enzyme Name:");
        enzymeNameField = new TransparentTextArea(1, 60);
        enzymeNameField.setWrapStyleWord(true);
        enzymeNameField.setVisible(false);
        enzymeNameLabel.setVisible(false);
        setBackground(Color.WHITE);


        sequenceField = new TransparentTextArea();
        sequenceField.setFont(ViewUtilities.COURIER_NEW_PLAIN_11);


        // layout
        // add( new JSeparator( JSeparator.HORIZONTAL ) , cc.xyw( 1 , 1 , layout.getColumnCount() ) );
        add(LabelFactory.newLabel("Identifier:"), cc.xy(1, 3));
        add(identifierField, cc.xy(3, 3));
        add(LabelFactory.newLabel("Description:"), cc.xy(1, 5));
        add(descriptionField, cc.xy(3, 5));
        add(LabelFactory.newLabel("Sequence Length:"), cc.xy(1, 7));
        add(sequenceLengthField, cc.xy(3, 7));

        add(enzymeLabel, cc.xy(1, 9));
        add(enzymeField, cc.xy(3, 9));
        add(enzymeNameLabel, cc.xy(1, 11));
        add(enzymeNameField, cc.xy(3, 11));

        add(LabelFactory.newLabel("Sequence:"), cc.xy(1, 13));
        add(sequenceField, cc.xy(3, 13));
        // add( new JSeparator( JSeparator.HORIZONTAL ) , cc.xyw( 1 , 15 , layout.getColumnCount() ) );

    }


    /**
     * Gets the displayed product from the inspection details panel
     * @return the current GeneProduct
     */
    public OldGeneProduct getDisplayedProduct() {
        return displayedProduct;
    }


    /**
     * Sets the displayed product
     * @param displayedProduct the new gene product to display
     */
    public void setDisplayedProduct(OldGeneProduct displayedProduct) {
        this.displayedProduct = displayedProduct;
        updateFields();
    }


    public void updateFields() {
        String accession = displayedProduct.getIdentifier().getAccession();
        identifierField.setText(accession);
        identifierField.setEditable(false);
        String sequence = displayedProduct.getSequence();
        sequenceField.setText(sequence);
        sequenceField.setEditable(false);
        descriptionField.setText("undef... add to observations/annotations");// displayedProduct.getDescription() );
        String units = (displayedProduct instanceof GeneProteinProduct) ? "aa" : "b";
        sequenceLengthField.setText(Integer.toString(sequence.length()) + " " + units);
        sequenceLengthField.setEditable(false);


        Collection<EnzymeClassification> enzymeAnnotations = displayedProduct.getAnnotations(
          EnzymeClassification.class);
//        Collection<FunctionalAnnotation> functionalAnnotations = displayedProduct.getAnnotations().get(
//          FunctionalAnnotation.class);

        if( enzymeAnnotations.size() > 0 ) {
            enzymeField.setText(StringUtils.join(enzymeAnnotations, ", \n"));
            enzymeField.setVisible(true);
            enzymeLabel.setVisible(true);
        } else {
            enzymeField.setText("-.-.-.-");
            enzymeField.setVisible(false);
            enzymeLabel.setVisible(false);
        }

//        if( functionalAnnotations.size() > 0 ) {
//            StringBuilder nameBuilder = new StringBuilder();
//            for( FunctionalAnnotation functionalAnnotation : functionalAnnotations ) {
//                nameBuilder.append(functionalAnnotation.getAnnotation().toString());
//                if( functionalAnnotations.indexOf(functionalAnnotation) != functionalAnnotations.
//                  size() - 1 ) {
//                    nameBuilder.append(", \n");
//                }
//            }
//            enzymeNameField.setText(nameBuilder.toString());
//            enzymeNameField.setVisible(true);
//            enzymeNameLabel.setVisible(true);
//        } else {
//            enzymeNameField.setText("...");
//            enzymeNameField.setVisible(false);
//            enzymeNameLabel.setVisible(false);
//        }
    }


    @Override
    public void setSize(Dimension d) {
        this.width = d.width;
        super.setSize(d);
    }


    public void setEditMode() {
        identifierField.setEditable(true);
        sequenceField.setEditable(true);
    }


    public void setViewMode() {
        identifierField.setEditable(false);
        sequenceField.setEditable(false);
    }


    public void saveChanges() {
        // set the value in the underlying gene product model
        // save state for undo command
    }


}

