package uk.ac.ebi.metingear.tools.structure;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import uk.ac.ebi.mdk.tool.transport.AminoAcid;
import uk.ac.ebi.mdk.ui.render.table.ChemicalStructureRenderer;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** @author John May */
public final class StructureTable {

    public static JTable aminoAcids() throws InvalidSmilesException {

        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        
        List<IAtomContainer> containers = new ArrayList<IAtomContainer>();
        
        for (AminoAcid aa : AminoAcid.values()) {
            IAtomContainer container = smipar.parseSmiles(aa.smiles());
            container.setProperty("Name",
                                  aa.structureName());
            containers.add(container);
        }
        
        return tableOf(containers, Arrays.asList("Name"));
    }
    
    public static JTable tableOf(List<IAtomContainer> containers, List<String> properties) {
        JTable table = new JTable(createModel(containers, properties));
        table.getColumnModel().getColumn(0)
                              .setCellEditor(null);
        table.getColumnModel().getColumn(0)
                              .setCellRenderer(new ChemicalStructureRenderer());
        return table;
    }

    private static TableModel createModel(List<IAtomContainer> containers, List<String> properties) {
        Object[][] data  = new Object[containers.size()][properties.size() + 1];
        Object[]   names = new Object[properties.size() + 1];
        
        names[0] = "Structure";
        for (int i = 0; i < properties.size(); i++)
            names[i + 1] = properties.get(i);
        
        for (int i = 0; i < containers.size(); i++) {
            data[i][0] = containers.get(i);
            for (int j = 0; j < properties.size(); j++)
                data[i][j+1] = containers.get(i).getProperty(properties.get(j));
        }                  
        
        return new DefaultTableModel(data, names);
    }
}
