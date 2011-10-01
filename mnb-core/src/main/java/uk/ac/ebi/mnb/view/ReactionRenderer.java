
/**
 * ReactionRenderer.java
 *
 * 2011.09.27
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.apache.log4j.Logger;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.chemet.entities.reaction.Reversibility;
import uk.ac.ebi.core.Metabolite;


/**
 *          ReactionRenderer – 2011.09.27 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReactionRenderer {

    private static final Logger LOGGER = Logger.getLogger(ReactionRenderer.class);
    private AtomContainerRenderer renderer =
                                  new AtomContainerRenderer(
      Arrays.asList(new BasicSceneGenerator(),
                    new BasicBondGenerator(),
                    new BasicAtomGenerator()),
      new AWTFontManager());
    private StructureDiagramGenerator sdg = new StructureDiagramGenerator();


    public JLabel getReaction(Reaction<Metabolite, ?, ?> rxn) {
        JLabel label = new JLabel();
        label.setBackground(Color.WHITE);

        int nParticipants = rxn.getAllReactionParticipants().size();

        if( nParticipants == 0 ) {
            return label;
        }

        int height = 128;
        int width = (nParticipants * 128) +
                    (128) // arrow
          + (nParticipants > 2 ? ((nParticipants - 2) * 15) : 0);

        BufferedImage masterImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = (Graphics2D) masterImg.getGraphics();

        Rectangle2D bounds = new Rectangle2D.Double(-128, 0, 128, 128);

        List<Metabolite> reactants = rxn.getReactantMolecules();
        for( int i = 0 ; i < reactants.size() ; i++ ) {

            bounds = new Rectangle2D.Double(bounds.getX() + bounds.getWidth(),
                                            0, 128, 128);
            BufferedImage subImage = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
            drawMolecule((Graphics2D) subImage.getGraphics(), new Rectangle(0, 0, 128, 128),
                         reactants.get(i));
            g2.drawImage(subImage, (int) bounds.getX(), (int) bounds.getY(), null);

            if( i + 1 < reactants.size() ) {
                bounds = new Rectangle2D.Double(bounds.getX() + bounds.getWidth(),
                                                0, 15, 128);
                drawPlus(g2, bounds);
            }
        }
        bounds = new Rectangle2D.Double(bounds.getX() + bounds.getWidth(),
                                        0, 128, 128);
        drawArrow(g2, bounds, Reversibility.REVERSIBLE);
        List<Metabolite> products = rxn.getProductMolecules();
        for( int i = 0 ; i < products.size() ; i++ ) {
            bounds = new Rectangle2D.Double(bounds.getX() + bounds.getWidth(),
                                            0, 128, 128);
            BufferedImage subImage = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
            drawMolecule((Graphics2D) subImage.getGraphics(), new Rectangle(0, 0, 128, 128),
                         products.get(i));
            g2.drawImage(subImage, (int) bounds.getX(), (int) bounds.getY(), null);
            if( i + 1 < products.size() ) {
                bounds = new Rectangle2D.Double(bounds.getX() + bounds.getWidth(),
                                                0, 15, 128);
                drawPlus(g2, bounds);
            }
        }

        label.setIcon(new ImageIcon(masterImg));

        return label;
    }


    public void drawMolecule(Graphics2D g2, Rectangle2D bounds, Metabolite metabolite) {

        g2.setColor(Color.WHITE);
        g2.fill(bounds);


        if( metabolite.getChemicalStructures().iterator().hasNext() ) {
            IAtomContainer atomContainer = metabolite.getChemicalStructures().iterator().next().
              getMolecule();


            sdg.setMolecule(new Molecule(atomContainer));
            try {
                sdg.generateCoordinates();
                renderer.paint(sdg.getMolecule(), new AWTDrawVisitor(g2), bounds, true);
                g2.dispose();
            } catch( CDKException ex ) {
                ex.printStackTrace();
            }
        } else {
            g2.setColor(Color.DARK_GRAY);
            String na = "Unavailable";
            int strWdth = g2.getFontMetrics().stringWidth(na);
            g2.drawString(na, (int) bounds.getCenterX() - (strWdth / 2), (int) bounds.getCenterY());
        }

    }


    public void drawPlus(Graphics2D g2, Rectangle2D bounds) {
        double length = (bounds.getWidth() / 2) * 0.8;

        double centreX = (bounds.getWidth() / 2d) + bounds.getX();
        double centreY = (bounds.getHeight() / 2d) + bounds.getY();
        g2.setColor(Color.WHITE);
        g2.fill(bounds);
        g2.setColor(Color.DARK_GRAY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.draw(new Line2D.Double(centreX - length, centreY, centreX + length, centreY));
        g2.draw(new Line2D.Double(centreX, centreY - length, centreX, centreY + length));

    }


    public void drawArrow(Graphics2D g2, Rectangle2D bounds, Reversibility reversibility) {
        double length = (bounds.getWidth() / 2) * 0.8;

        double centreX = (bounds.getWidth() / 2d) + bounds.getX();
        double centreY = (bounds.getHeight() / 2d) + bounds.getY();
        g2.setColor(Color.WHITE);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fill(bounds);
        g2.setColor(Color.DARK_GRAY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.draw(new Line2D.Double(centreX - (length * 0.65), centreY, centreX + (length * 0.65),
                                  centreY));

        int rightX[] = new int[]{
            (int) (centreX + (length * 0.7)),
            (int) (centreX + (length * 0.7)),
            (int) (centreX + length)
        };
        int y[] = new int[]{
            (int) (centreY + (length * 0.11)),
            (int) (centreY - (length * 0.11)),
            (int) (centreY)
        };


        int leftX[] = new int[]{
            (int) (centreX - (length * 0.7)),
            (int) (centreX - (length * 0.7)),
            (int) (centreX - length)
        };

        if( reversibility != Reversibility.IRREVERSIBLE_LEFT_TO_RIGHT ) {
            g2.fillPolygon(new Polygon(rightX, y, 3));
            g2.drawPolygon(new Polygon(rightX, y, 3));
        }
        if( reversibility != Reversibility.IRREVERSIBLE_RIGHT_TO_LEFT ) {
            g2.fillPolygon(new Polygon(leftX, y, 3));
            g2.drawPolygon(new Polygon(leftX, y, 3));
        }


    }


    public static void main(String[] args) throws IOException {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
//        Reaction rxn = new R
//        new ReactionRenderer().getReaction()
        ImageIO.write(img, "png", new File("/Users/johnmay/Desktop/sample.png"));
    }


}

