
/**
 * AnnotationRenderer.java
 *
 * 2011.09.26
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
package uk.ac.ebi.mnb.view;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import uk.ac.ebi.mnb.view.labels.MLabel;
import uk.ac.ebi.mnb.view.labels.URLLabel;
import org.apache.log4j.Logger;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import uk.ac.ebi.annotation.chemical.ChemicalStructure;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.interfaces.Annotation;
import uk.ac.ebi.interfaces.vistors.AnnotationVisitor;


/**
 *          AnnotationRenderer – 2011.09.26 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class AnnotationRenderer implements AnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(AnnotationRenderer.class);
    private AtomContainerRenderer renderer =
                                  new AtomContainerRenderer(
      Arrays.asList(new BasicSceneGenerator(),
                    new BasicBondGenerator(),
                    new BasicAtomGenerator()),
      new AWTFontManager());
    private StructureDiagramGenerator sdg = new StructureDiagramGenerator();


    public AnnotationRenderer() {
    }


    public JLabel getLabel(Annotation annotation) {
        JLabel label = new MLabel(annotation.getShortDescription() + ":", SwingConstants.RIGHT);
        label.setToolTipText(annotation.getLongDescription());
        return label;
    }


    /**
     * Visits the annotation an returns a subpanel
     * @param annotation
     * @return
     */
    public JComponent visit(ChemicalStructure annotation) {


        BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);

        IMolecule molecule = new Molecule(annotation.getMolecule());
        sdg.setMolecule((IMolecule) molecule);
        try {
            Graphics2D g2 = (Graphics2D) img.getGraphics();
            g2.fill(new Rectangle(0, 0, 128, 128));
            sdg.generateCoordinates();
            renderer.paint(sdg.getMolecule(), new AWTDrawVisitor(g2), new Rectangle(0, 0, 128, 128),
                           true);
            g2.dispose();
        } catch( CDKException ex ) {
            ex.printStackTrace();
        }


        return new JLabel(new ImageIcon(img));

    }


    /**
     * Visits the annotation an returns a subpanel
     * @param annotation
     * @return
     */
    public JComponent visit(CrossReference annotation) {

        return new URLLabel(annotation.getIdentifier().getURL(),
                            annotation.getIdentifier().getAccession());

    }


    /**
     * Visits the annotation an returns a subpanel
     * @param annotation
     * @return
     */
    public JComponent visit(MolecularFormula annotation) {

        return new MLabel(ViewUtils.htmlWrapper(MolecularFormulaManipulator.getHTML(annotation.
          getFormula())));

    }


    /**
     * Visits the annotation an returns a subpanel
     * @param annotation
     * @return
     */
    public JComponent visit(Annotation annotation) {

        if( annotation instanceof CrossReference ) {
            return visit((CrossReference) annotation);
        } else if( annotation instanceof MolecularFormula ) {
            return visit((MolecularFormula) annotation);
        } else if( annotation instanceof ChemicalStructure ) {
            return visit((ChemicalStructure) annotation);
        }

        return new MLabel(annotation.toString());

    }


}

