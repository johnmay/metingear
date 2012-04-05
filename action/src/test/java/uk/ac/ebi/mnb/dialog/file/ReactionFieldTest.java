/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.dialog.file;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johnmay
 */
public class ReactionFieldTest {

    @Test
    public void oneSideInsertion() {

        assertEquals(new Replacement(6, 9, "adp"), ReactionField.getReplacement("atp + adp", 9));
        assertEquals(new Replacement(6, 9, "h+ "), ReactionField.getReplacement("atp + h+ ", 9));
        assertEquals(new Replacement(6, 8, "h+"), ReactionField.getReplacement("atp + h+", 8));
        assertEquals(new Replacement(0, 3, "atp"), ReactionField.getReplacement("atp + miss this", 3));
        assertEquals(new Replacement(4, 4, ""), ReactionField.getReplacement("atp + miss this", 4));
        assertEquals(new Replacement(0, 0, ""), ReactionField.getReplacement(" + miss this", 0));
        assertEquals(new Replacement(0, 0, ""), ReactionField.getReplacement("+ miss this", 0));

    }

    @Test
    public void multipleSideInsertion() {
        assertEquals(new Replacement(6, 9, "adp"), ReactionField.getReplacement("atp + adp <->", 9));
        assertEquals(new Replacement(12, 12, ""), ReactionField.getReplacement("atp + adp <->", 12));
        assertEquals(new Replacement(13, 17, " amp"), ReactionField.getReplacement("atp + adp <-> amp", 17));
    }
}
