/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mnb.io.tabular.parser;

import java.util.Arrays;
import junit.framework.TestCase;
import mnb.io.tabular.ExcelEntityResolver;
import uk.ac.ebi.chemet.entities.reaction.Reversibility;
import static junit.framework.Assert.*;


/**
 *
 * @author johnmay
 */
public class ReactionParserTest extends TestCase {

    public ReactionParserTest(String testName) {
        super(testName);
    }


    /**
     * Test of parseReaction method, of class ReactionParser.
     */
    public void testParseReaction() throws Exception {
    }


    /**
     * Test of getReactionSides method, of class ReactionParser.
     */
    public void testGetReactionSides() {
        ReactionParser parser = new ReactionParser(null);
        String[] expected = new String[]{ "A + B ", " C + D" };

        String[] actual = parser.getReactionSides("A + B <--> C + D");
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
        actual = parser.getReactionSides("A + B <==> C + D");
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
        actual = parser.getReactionSides("A + B <=> C + D");
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);

        actual = parser.getReactionSides("A + B --> C + D");
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
        actual = parser.getReactionSides("A + B -> C + D");
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);

        actual = parser.getReactionSides("A + B <-- C + D");
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
        actual = parser.getReactionSides("A + B <- C + D");
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);


        actual = parser.getReactionSides("A + B < C + D");
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
        actual = parser.getReactionSides("A + B > C + D");
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);

        // exchange reactions
        actual = parser.getReactionSides("A + B <==>");
        assertEquals(1, actual.length);
        assertEquals(expected[0], actual[0]);
        actual = parser.getReactionSides("A + B <==> ");
        assertEquals(expected[0], actual[0]);


        // fail case
        actual = parser.getReactionSides("");
        assertEquals(0, actual.length);

    }


    public void testGetReactionArrow() {
        ReactionParser parser = new ReactionParser(null);
        Reversibility actual = parser.getReactionArrow("A + B <--> C + D");
        assertEquals(Reversibility.REVERSIBLE, actual);
        actual = parser.getReactionArrow("A + B <==> C + D");
        assertEquals(Reversibility.REVERSIBLE, actual);
        actual = parser.getReactionArrow("A + B <-> C + D");
        assertEquals(Reversibility.REVERSIBLE, actual);
        actual = parser.getReactionArrow("A + B --> C + D");
        assertEquals(Reversibility.IRREVERSIBLE_LEFT_TO_RIGHT, actual);
        actual = parser.getReactionArrow("A + B -> C + D");
        assertEquals(Reversibility.IRREVERSIBLE_LEFT_TO_RIGHT, actual);
        actual = parser.getReactionArrow("A + B > C + D");
        assertEquals(Reversibility.IRREVERSIBLE_LEFT_TO_RIGHT, actual);
        actual = parser.getReactionArrow("A + B <-- C + D");
        assertEquals(Reversibility.IRREVERSIBLE_RIGHT_TO_LEFT, actual);
        actual = parser.getReactionArrow("A + B <- C + D");
        assertEquals(Reversibility.IRREVERSIBLE_RIGHT_TO_LEFT, actual);
        actual = parser.getReactionArrow("A + B < C + D");
        assertEquals(Reversibility.IRREVERSIBLE_RIGHT_TO_LEFT, actual);

        // fail case
        actual = parser.getReactionArrow("A + B - C + D");
        assertEquals(Reversibility.UNKNOWN, actual);
        // fail case
        actual = parser.getReactionArrow("A + B = C + D");
        assertEquals(Reversibility.UNKNOWN, actual);




    }


}

