/**
 * AlignmentViewer.java
 *
 * 2011.10.11
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
package uk.ac.ebi.mnb.dialog.popup;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.utility.ColorUtility;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.observation.sequence.LocalAlignment;
import uk.ac.ebi.mdk.ui.render.alignment.AbstractAlignmentColor;
import uk.ac.ebi.mdk.ui.render.alignment.BasicAlignmentColor;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.Arrays;

/**
 * @name    AlignmentViewer - 2011.10.11 <br>
 *          View alignments in a small pop-up. The intention is the
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public final class AlignmentViewer extends PopupDialog {

    private static final Logger LOGGER = Logger.getLogger(AlignmentViewer.class);
    private int buffer = 5;
    private JTextPane sequence = new JTextPane();
    private Style match = sequence.addStyle("Match", null);
    private Style equivalent = sequence.addStyle("Equivalent", null);
    private Style mismatch = sequence.addStyle("Mismatch", null);
    private GeneProduct product;
    private AbstractAlignmentColor defaultColor = new BasicAlignmentColor(ColorUtility.EMBL_PETROL, ColorUtility.shade(ColorUtility.EMBL_PETROL, 0.4f), ColorUtility.CLEAR_WHITE);

    /**
     * 
     * Create a new alignment viewer with a specified buffer size. Then buffer determines how much the sequence to
     * display either side of the given index. The default is 5 which means 11 characters will be displayed.
     *
     */
    public AlignmentViewer(JFrame frame, int buffer) {

        super(frame, ModalityType.MODELESS);

        this.buffer = buffer;
        getPanel().add(sequence);
        sequence.setFont(new Font("Courier New", Font.PLAIN, 10));
        sequence.setBackground(getPanel().getBackground());
        setAlwaysOnTop(rootPaneCheckingEnabled);
        setColor(defaultColor);
        setFocusable(false);
        setFocusableWindowState(false);
    }

    public void setColor(AbstractAlignmentColor color) {
        StyleConstants.setBackground(match, color.match);
        StyleConstants.setForeground(match, ColorUtility.getTextColor(color.match));
        StyleConstants.setBackground(equivalent, color.equivalent);
        StyleConstants.setForeground(equivalent, ColorUtility.getTextColor(color.equivalent));
        StyleConstants.setBackground(mismatch, color.mismatch);
        StyleConstants.setForeground(mismatch, ColorUtility.getTextColor(color.mismatch));
    }

    /**
     * Sets the size of buffer each side of the selected index
     * @param buffer 
     */
    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    /**
     * Sets the query product in the viewer
     * @param product
     */
    public void setProduct(GeneProduct product) {
        this.product = product;
    }

    public void setSequence(LocalAlignment alignment, float location) {

        if (product == null || alignment.hasSequences() == false) {
            this.sequence.setText("Alignment does not contain sequence!");
            return;
        }

        String querySequence = product.getSequences().iterator().next().getSequenceAsString();
        String targetSequence = alignment.getSubjectSequence();
        String alignmentSequence = alignment.getAlignmentSequence();


        int length = product.getSequences().iterator().next().getLength();

        int index = (int) Math.floor(length * location);

        int start = Math.max(0, index - buffer);
        int end = Math.min(length, index + buffer);

        // shift indices for the alignment
        int startTrg = Math.max(start - alignment.getQueryStart() + 1, 0);
        int endTrg = Math.min(end - alignment.getQueryStart() + 1, targetSequence.length());
        int startAln = Math.max(start - alignment.getQueryStart() + 1, 0);
        int endAln = Math.min(end - alignment.getQueryStart() + 1, alignmentSequence.length());

        String querySubseq = pad(querySequence.substring(start, end), (start == 0));
        String alignmentSubseq = pad(endAln > startAln ? alignmentSequence.substring(startAln, endAln) : "", (startAln == 0));
        String targetSubseq = pad(endTrg > startTrg ? targetSequence.substring(startTrg, endTrg) : "", (startTrg == 0));

        this.sequence.setText(querySubseq + "\n"
                              + alignmentSubseq + "\n"
                              + targetSubseq);

        StyledDocument document = sequence.getStyledDocument();

        int size = buffer * 2;
        char[] alns = alignmentSubseq.toCharArray();
        for (int i = 0; i < size; i++) {
            if (alns[i] == ' ' || alns[i] == '-') {
                document.setCharacterAttributes(i, 1, mismatch, true);
                document.setCharacterAttributes(size + 1 + i, 1, mismatch, true);
                document.setCharacterAttributes(size + size + 2 + i, 1, mismatch, true);
            } else if (alns[i] == '+') {
                document.setCharacterAttributes(i, 1, equivalent, true);
                document.setCharacterAttributes(size + 1 + i, 1, equivalent, true);
                document.setCharacterAttributes(size + size + 2 + i, 1, equivalent, true);
            } else {
                document.setCharacterAttributes(i, 1, match, true);
                document.setCharacterAttributes(size + 1 + i, 1, match, true);
                document.setCharacterAttributes(size + size + 2 + i, 1, match, true);
            }
        }

    }

    /**
     * Pads a string with hyphens
     * @param s
     * @param paddingLeft
     * @return
     */
    public String pad(String s, boolean paddingLeft) {
        if (s == null) {
            return s;
        }
        int add = (buffer * 2) - s.length(); // may overflow int size... should not be a problem in real life
        if (add <= 0) {
            return s;
        }
        StringBuffer str = new StringBuffer(s);
        char[] ch = new char[add];
        Arrays.fill(ch, '-');
        if (paddingLeft) {
            str.insert(0, ch);
        } else {
            str.append(ch);
        }
        return str.toString();
    }
}
