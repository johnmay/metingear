/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;
import uk.ac.ebi.chemet.entities.reaction.Reversibility;
import uk.ac.ebi.chemet.render.reaction.ReactionRenderer;
import uk.ac.ebi.core.CompartmentImplementation;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.core.MetaboliteImplementation;
import uk.ac.ebi.core.reaction.MetabolicParticipant;
import uk.ac.ebi.resource.chemical.BasicChemicalIdentifier;
import uk.ac.ebi.resource.reaction.BasicReactionIdentifier;


/**
 *
 * @author johnmay
 */
public class ReactionRendererTest {

    @Test
    public void testRenderUniporterReaction() throws IOException {

        MetaboliteImplementation atp = new MetaboliteImplementation(BasicChemicalIdentifier.nextIdentifier(), "atp", "ATP");
        MetaboliteImplementation alanine = new MetaboliteImplementation(BasicChemicalIdentifier.nextIdentifier(), "dala", "D-Alanine");

        final MetabolicReaction rxn = new MetabolicReaction(BasicReactionIdentifier.nextIdentifier(), "up",
                                                            "uniportTest");

        rxn.addReactant(new MetabolicParticipant(atp, CompartmentImplementation.CYTOPLASM));
        rxn.addReactant(new MetabolicParticipant(alanine, CompartmentImplementation.EXTRACELLULA));

        rxn.addProduct(new MetabolicParticipant(atp, CompartmentImplementation.CYTOPLASM));
        rxn.addProduct(new MetabolicParticipant(alanine, CompartmentImplementation.CYTOPLASM));

        rxn.setReversibility(Reversibility.IRREVERSIBLE_LEFT_TO_RIGHT);

        final ReactionRenderer renderer = new ReactionRenderer();


        File f = File.createTempFile("testImage", ".png");
        ImageIO.write((BufferedImage) renderer.renderTransportReaction(rxn).getImage(), "png", f);
        System.out.println(f);

    }


    public void testGetReaction() {
    }


    public void testDrawMolecule() {
    }


    public void testDrawPlus() {
    }


    public void testDrawArrow() {
    }


    public void testMain() throws Exception {
    }
}
