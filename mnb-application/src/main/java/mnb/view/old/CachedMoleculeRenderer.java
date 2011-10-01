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

package mnb.view.old;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.jchempaint.renderer.AtomContainerRenderer;
import org.openscience.jchempaint.renderer.color.CPKAtomColors;
import org.openscience.jchempaint.renderer.font.AWTFontManager;
import org.openscience.jchempaint.renderer.generators.BasicAtomGenerator;
import org.openscience.jchempaint.renderer.generators.BasicBondGenerator;
import org.openscience.jchempaint.renderer.visitor.AWTDrawVisitor;


/**
 * CachedMoleculeRenderer.java
 * Molecule Renderer draws the CDK IMolecules cashing the results in a HashMap for quick redraw.
 *
 * @author johnmay
 * @date May 19, 2011
 */
public class CachedMoleculeRenderer {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      CachedMoleculeRenderer.class);
    private final Map<IAtomContainer, BufferedImage> moleculeImageMap;
    private final AtomContainerRenderer renderer =
                                        new AtomContainerRenderer(
      Arrays.asList(new BasicBondGenerator(),
                    new BasicAtomGenerator()),
                                                                  new AWTFontManager());
    private final StructureDiagramGenerator structureGenerator = new StructureDiagramGenerator();


    public CachedMoleculeRenderer() {
        moleculeImageMap = new HashMap<IAtomContainer, BufferedImage>();
        renderer.getRenderer2DModel().setBackColor(Color.WHITE);
        renderer.getRenderer2DModel().setAtomColorer(new CPKAtomColors());
        renderer.getRenderer2DModel().setUseAntiAliasing(true);

    }


    public BufferedImage getImageFromMolecule(IMolecule molecule, Rectangle bounds) {
        return getImage(molecule, bounds);
    }


    public BufferedImage getImage(IAtomContainer molecule, Rectangle bounds) {
        // todo check the size and store different sizes?
        if( moleculeImageMap.containsKey(molecule) == Boolean.FALSE ) {
            // no cache so renderer a new one
            moleculeImageMap.put(molecule, drawMolecule(molecule, bounds));
        }
        return moleculeImageMap.get(molecule);
    }


    /**
     * Draws the molecule onto the buffered image
     * @param molecule
     * @return
     */
    private BufferedImage drawMolecule(IAtomContainer molecule, Rectangle bounds) {
        BufferedImage img = new BufferedImage(bounds.width, bounds.height,
                                              BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = (Graphics2D) img.getGraphics();

        try {
            structureGenerator.setMolecule(new Molecule(molecule));
            structureGenerator.generateCoordinates();
            IMolecule moleculeWithXYZ = structureGenerator.getMolecule();
            renderer.paintMolecule(moleculeWithXYZ, new AWTDrawVisitor(g2), bounds, true);
        } catch( Exception ex ) {
            logger.error("Error generating structure coordinates");
        }

        g2.dispose();
        return img;
    }


}

