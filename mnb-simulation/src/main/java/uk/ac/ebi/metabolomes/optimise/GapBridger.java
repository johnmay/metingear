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

package uk.ac.ebi.metabolomes.optimise;

import uk.ac.ebi.optimise.gap.GapFind;
import com.sri.biospice.warehouse.database.Warehouse;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;

import org.openscience.cdk.exception.CDKException;

import uk.ac.ebi.metabolomes.biowh.BiowhConnection;
import uk.ac.ebi.metabolomes.biowh.DataSetProvider;
import uk.ac.ebi.metabolomes.core.reaction.BiochemicalReaction;
import uk.ac.ebi.metabolomes.core.reaction.matrix.InChIStoichiometricMatrix;
import uk.ac.ebi.metabolomes.identifier.InChI;
import uk.ac.ebi.metabolomes.io.homology.ReactionMatrixIO;
import uk.ac.ebi.warehouse.exceptions.UnknownStructureException;
import uk.ac.ebi.warehouse.util.ReactionLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * GapBridger.java – MetabolicDevelopmentKit – Jun 30, 2011
 * Class tries bridging gaps by finding a reaction removes the dead end metabolite
 * @author johnmay <johnmay@ebi.ac.uk, john.wilkinsonmay@gmail.com>
 */
public class GapBridger {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(GapBridger.class);
    private InChIStoichiometricMatrix s;


    public GapBridger(InChIStoichiometricMatrix s) {
        this.s = s;
    }


    /**
     * Find a reaction which allows transformation of metabolites
     * @param i
     */
    public List<BiochemicalReaction> findReactionTransformation(int i)
      throws SQLException, CDKException {
        InChI inchi = s.getMolecule(i);
        BiochemicalReaction[] potentialReactions = null;


        potentialReactions = ReactionLoader.getInstance().getInvolvedReactions(inchi);

        List<BiochemicalReaction> toAdd = new ArrayList<BiochemicalReaction>();
        System.out.println(i + " : " + ReactionLoader.getInstance().getNameForInChI(inchi) + " : " +
                           potentialReactions.length);

        for( int j = 0 ; j < potentialReactions.length ; j++ ) {
            BiochemicalReaction biochemicalReaction = potentialReactions[j];

            if( containsAll(biochemicalReaction.getInchiProducts()) ) {
                toAdd.add(biochemicalReaction);
                System.out.println(biochemicalReaction.toString());
            }
        }

        return toAdd;
    }


    public boolean containsAll(List<InChI> products) {
        for( int k = 0 ; k < products.size() ; k++ ) {
            if( !s.containsMolecule(products.get(k)) ) {
                return false;
            }
        }

        return true;
    }


    public static void main(String[] args)
      throws FileNotFoundException, IOException, SQLException, CDKException {
        BiowhConnection connection = new BiowhConnection();
        Warehouse warehouse = connection.getWarehouseObject();
        DataSetProvider.loadPropsForCurrentSchema();

        ReactionLoader bwhReacHelper = ReactionLoader.getInstance();
        ReactionMatrixIO.setSeparator('\t');
        InChIStoichiometricMatrix s =
                                  ReactionMatrixIO.readInChIStoichiometricMatrix(new FileReader(
          "/Users/johnmay/Desktop/s.tsv"));
        GapFind locator = new GapFind(s);
        Integer[] nonConsumptionIs = locator.getTerminalNCMetabolites();
        GapBridger bridger = new GapBridger(s);

        InChI ref = new InChI("InChI=1S/C7H7NO2/c8-6-4-2-1-3-5(6)7(9)10/h1-4H,8H2,(H,9,10)");

        int count = 0;

        for( InChI molecule : s.getMolecules() ) {
            RegularExpression regex = new RegularExpression("p[-+][[:digit:]]");
//            System.out.println( molecule + "\t" +  regex.matches( molecule.getInchi()) );
            count += (regex.matches(molecule.getInchi()) ? 1 : 0);
        }

        System.out.println(count);

        // From looking in cytoscape 40 looks like a good candidate
        for( int i = 0 ; i < nonConsumptionIs.length ; i++ ) {
            InChI query = s.getMolecule(nonConsumptionIs[i]);
            System.out.println(ReactionLoader.getInstance().getNameForInChI(query) + ":" + query);
            bridger.findReactionTransformation(nonConsumptionIs[i]);
        }

        //       System.out.println( query );
        //bridger.findReactionTransformation( nonConsumptionIs[40] );
    }


}

