/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.metabolomes.optimise;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.caf.utility.preference.type.FilePreference;
import uk.ac.ebi.core.CorePreferences;
import uk.ac.ebi.metabolomes.core.reaction.matrix.BasicStoichiometricMatrix;
import uk.ac.ebi.optimise.SimulationUtil;
import uk.ac.ebi.optimise.gap.GapFind;

import java.util.logging.Level;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author johnmay
 */
public class GapFindTest {
    private static final Logger LOGGER = Logger.getLogger(GapFindTest.class);

    private BasicStoichiometricMatrix s;
    private static Boolean runnable;

    @BeforeClass
    public static void setup() {
        runnable = SimulationUtil.setup();
    }

    @Before
    public void setupMatrix() {
        s = BasicStoichiometricMatrix.create();

        // internal reactions
        s.addReaction("A => B", false);
        s.addReaction("B => C", false);
        s.addReaction("C => D", false);
        s.addReaction("D => E", false);
    }


    /**
     * Test of findNonProductionMetabolites method, of class DeadEndDetector.
     */
    @Test
    public void testFindNonProductionMetabolites()
            throws Exception {

        if (!runnable) return;

        // drain E and F
        s.addReaction(new String[]{"E"},
                      new String[0]);

        GapFind gf = new GapFind(s);
        Integer[] unproduced = gf.getUnproducedMetabolites();

        assertEquals(5, unproduced.length);

        for (Integer i = 0; i < unproduced.length; i++) {
            assertEquals(unproduced[i], i);
        }

        // allow influx of A
        s.addReaction(new String[0],
                      new String[]{"A"});
        s.display();
        gf = new GapFind(s);
        unproduced = gf.getUnproducedMetabolites();

        assertEquals(0, unproduced.length);
    }

    /**
     * Test of findNonConsumptionMetabolites method, of class DeadEndDetector.
     */
    @Test
    public void testFindNonConsumptionMetabolites()
            throws Exception {
        if (!runnable) return;

        // allow influx of A
        s.addReaction(new String[0],
                      new String[]{"A"});

        GapFind gf = new GapFind(s);
        Integer[] unconsumed = gf.getUnconsumedMetabolites();

        assertEquals(5, unconsumed.length);

        for (Integer i = 0; i < unconsumed.length; i++) {
            assertEquals(unconsumed[i], i);
        }

        // drain E
        s.addReaction(new String[]{"E"},
                      new String[0]);
        s.addReaction( new String[]{ "F" } , new String[ 0 ] );
        gf = new GapFind(s);
        unconsumed = gf.getUnconsumedMetabolites();

        // should only be 1 which is F
        assertEquals(1, unconsumed.length);
        assertEquals("F",
                     s.getMolecule(unconsumed[0]));
    }

    /**
     * Test of getTerminalNCMetabolites method, of class DeadEndDetector.
     */
    @Test
    public void testRoot() {
        if (!runnable) return;
        try {
            GapFind gf = new GapFind(s);
            Integer[] root = gf.getRootUnproducedMetabolites();
    
            s.display();
            
            // there should be 2 root non-production metabolites, E and C
            assertEquals(1, root.length);
            assertEquals("A",
                         s.getMolecule(root[0]));

            // product A via exchange reaction
            s.addReaction(new String[0],
                          new String[]{"A"}    );
            gf = new GapFind(s);
            root = gf.getRootUnproducedMetabolites();

        } catch (UnsatisfiedLinkError ex) {
            java.util.logging.Logger.getLogger(GapFindTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(GapFindTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getRootNPMetabolites method, of class DeadEndDetector.
     */
    @Test
    public void testTerminal() {
        if (!runnable) return;
        try {
            GapFind gf = new GapFind(s);
            Integer[] terminal = gf.getTerminalUnconsumpedMetabolites();

            // check there should be 2 root non-production metabolites, A and B
            assertEquals(terminal.length, 1);
            assertEquals("E",
                         s.getMolecule(terminal[0]));

            //  produce A via exchange reaction
            s.addReaction(new String[]{"E"},
                          new String[0]);
            gf = new GapFind(s);
            terminal = gf.getTerminalUnconsumpedMetabolites();

            // there should be no NP dead end metabolites
            assertEquals(terminal.length, 0);
        } catch (UnsatisfiedLinkError ex) {
            java.util.logging.Logger.getLogger(GapFindTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(GapFindTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
