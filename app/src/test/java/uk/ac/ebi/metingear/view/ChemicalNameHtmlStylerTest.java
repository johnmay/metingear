/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.metingear.view;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChemicalNameHtmlStylerTest {

    @Test public void matchDAtStart() {
        assertTrue(ChemicalNameHtmlStyler.GLYCERALDEHYDE_STEREOHEMISTRY.matcher("D-alanine").find());
    }

    @Test public void matchDAfterHyphen() {
        assertTrue(ChemicalNameHtmlStyler.GLYCERALDEHYDE_STEREOHEMISTRY.matcher("beta-D-alanine").find());
    }

    @Test public void matchDAfterSpace() {
        assertTrue(ChemicalNameHtmlStyler.GLYCERALDEHYDE_STEREOHEMISTRY.matcher("beta D-alanine").find());
    }

    @Test public void matchDBeforeSpace() {
        assertTrue(ChemicalNameHtmlStyler.GLYCERALDEHYDE_STEREOHEMISTRY.matcher("beta D alanine").find());
    }

    @Test public void betaAtStart() {
        assertTrue(ChemicalNameHtmlStyler.ANOMERIC_STEREOCHEMISTRY.matcher("beta D alanine").find());
    }

    @Test public void alphaAtStart() {
        assertTrue(ChemicalNameHtmlStyler.ANOMERIC_STEREOCHEMISTRY.matcher("alpha D alanine").find());
    }

    @Test public void cipAtStart() {
        assertTrue(ChemicalNameHtmlStyler.CIP_STEREOCHEMISTRY.matcher("(2S,4Z)-Dihydroorotate").find());
    }
    
    @Test public void alanine() {
        assertEquals("<html>ᴅ-alanine</html>",ChemicalNameHtmlStyler.styleHtml("D-alanine"));
        assertEquals("<html>ʟ-alanine</html>",ChemicalNameHtmlStyler.styleHtml("L-alanine"));
    }

    @Test public void pantothenate() {
        assertEquals("<html>(<i>R</i>)-pantothenate</html>", ChemicalNameHtmlStyler.styleHtml("(R)-Pantothenate"));
        assertEquals("<html>(<i>S</i>)-pantothenate</html>", ChemicalNameHtmlStyler.styleHtml("(S)-Pantothenate"));
    }

    @Test public void alphaAndBetaAlanine() {
        assertEquals("<html>α-alanine</html>", ChemicalNameHtmlStyler.styleHtml("alpha-Alanine"));
        assertEquals("<html>β-alanine</html>", ChemicalNameHtmlStyler.styleHtml("beta-Alanine"));
    }
    
    @Test public void alphaAlphaTrehalase() {
        assertEquals("<html>α,α-trehalase</html>", ChemicalNameHtmlStyler.styleHtml("alpha,alpha-trehalase"));
    }
    
    @Test public void carboxyethyOrnithine() {
        assertEquals("<html><i>N</i><sup>5</sup>-(ʟ-1-carboxyethyl)-ʟ-ornithine</html>", ChemicalNameHtmlStyler.styleHtml("N(5)-(L-1-carboxyethyl)-L-ornithine"));
    }

    @Test public void snGlycero3phosphocholine() {
        assertEquals("<html><i>sn</i>-glycero-3-phosphocholine</html>", ChemicalNameHtmlStyler.styleHtml("sn-glycero-3-phosphocholine"));
    }

    @Test public void udpNAcetyl() {
        assertEquals("<html>UDP-<i>N</i><sup></sup>-acetylmuramoyl</html>", ChemicalNameHtmlStyler.styleHtml("UDP-N-acetylmuramoyl"));
    }
    
    @Test public void chebi17422() {
        assertEquals("<html><i>P</i><sup>1</sup>,<i>P</i><sup>4</sup>-bis(5'-adenosyl) tetraphosphate</html>", ChemicalNameHtmlStyler.styleHtml("P(1),P(4)-bis(5'-adenosyl) tetraphosphate"));
    }
    
    @Test public void n2AcetylLornithine() {
        assertEquals("<html><i>N</i><sup>2</sup>-acetyl-ʟ-ornithine</html>", ChemicalNameHtmlStyler.styleHtml("N2-Acetyl-L-ornithine"));
    }
    
    @Test public void OAcetylLserine() {        
        assertEquals("<html><i>O</i><sup></sup>-acetyl-ʟ-serine</html>", ChemicalNameHtmlStyler.styleHtml("O-Acetyl-L-Serine"));
    }
    
    @Test public void cisTransPrefix() {        
        assertEquals("<html><i>cis</i>-3-(3-carboxyethenyl)-3,5-cyclohexadiene-1,2-diol</html>", ChemicalNameHtmlStyler.styleHtml("cis-3-(3-carboxyethenyl)-3,5-cyclohexadiene-1,2-diol"));
    }

    @Test public void maintainCaseWhenNeeded() {
        assertEquals("<html>UDPglucose</html>", ChemicalNameHtmlStyler.styleHtml("UDPglucose"));
    }

    @Test public void hemeO() {
        assertEquals("<html>heme <i>O</i><sup></sup></html>", ChemicalNameHtmlStyler.styleHtml("Heme O"));
    }
    
}