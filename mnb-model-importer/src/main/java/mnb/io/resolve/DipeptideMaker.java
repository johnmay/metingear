/**
 * DipeptideImproter.java
 *
 * 2011.11.14
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
package mnb.io.resolve;

import com.jgoodies.forms.layout.CellConstraints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.AminoAcidCompoundSet;
import uk.ac.ebi.mnb.view.MComboBox;
import uk.ac.ebi.mnb.view.PanelFactory;

/**
 *          DipeptideImproter - 2011.11.14 <br>
 *          Importer tool for creating di-peptides
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class DipeptideMaker {

    private static final Logger LOGGER = Logger.getLogger(DipeptideMaker.class);
    private MComboBox firstPeptide;
    private MComboBox secondPeptide;
    private JPanel component = PanelFactory.createDialogPanel("p,4dlu,p", "p");

    public DipeptideMaker() {

        List<AminoAcidCompound> aaList = AminoAcidCompoundSet.getAminoAcidCompoundSet().getAllCompounds();
        Map<String, AminoAcidCompound> map = new HashMap();
        for (AminoAcidCompound aa : aaList) {
            map.put(aa.getLongName(), aa);
        }

        CellConstraints cc = new CellConstraints();

        firstPeptide = new MComboBox(map.keySet());
        secondPeptide = new MComboBox(map.keySet());

        component.add(firstPeptide, cc.xy(1, 1));
        component.add(secondPeptide, cc.xy(3, 1));


    }

    public JPanel getComponent() {
        return component;
    }



}
