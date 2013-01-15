/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.io.tabular.parser;

import junit.framework.TestCase;
import uk.ac.ebi.mdk.domain.entity.reaction.Direction;
import uk.ac.ebi.mdk.domain.tool.AutomaticCompartmentResolver;


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
        ReactionParser parser = new ReactionParser(null, new AutomaticCompartmentResolver());
        String[] expected = new String[]{"A + B ", " C + D"};

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
        ReactionParser parser = new ReactionParser(null, new AutomaticCompartmentResolver());
        Direction actual = parser.getReactionArrow("A + B <--> C + D");
        assertEquals(Direction.BIDIRECTIONAL, actual);
        actual = parser.getReactionArrow("A + B <==> C + D");
        assertEquals(Direction.BIDIRECTIONAL, actual);
        actual = parser.getReactionArrow("A + B <-> C + D");
        assertEquals(Direction.BIDIRECTIONAL, actual);
        actual = parser.getReactionArrow("A + B --> C + D");
        assertEquals(Direction.FORWARD, actual);
        actual = parser.getReactionArrow("A + B -> C + D");
        assertEquals(Direction.FORWARD, actual);
        actual = parser.getReactionArrow("A + B > C + D");
        assertEquals(Direction.FORWARD, actual);
        actual = parser.getReactionArrow("A + B <-- C + D");
        assertEquals(Direction.BACKWARD, actual);
        actual = parser.getReactionArrow("A + B <- C + D");
        assertEquals(Direction.BACKWARD, actual);
        actual = parser.getReactionArrow("A + B < C + D");
        assertEquals(Direction.BACKWARD, actual);

        // fail case
        actual = parser.getReactionArrow("A + B - C + D");
        assertEquals("Unknown direction not detected for '='", Direction.UNKNOWN, actual);
        // fail case
        actual = parser.getReactionArrow("A + B = C + D");
        assertEquals("Unknown direction not detected for '='", Direction.UNKNOWN, actual);




    }
}